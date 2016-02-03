import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import boofcv.struct.*;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import georegression.struct.shapes.EllipseRotated_F64;
import boofcv.alg.color.ColorHsv;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.PixelMath;
import boofcv.alg.shapes.FitData;
import boofcv.alg.shapes.ShapeFittingOps;
import boofcv.core.image.ConvertImage;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.*;
import boofcv.io.image.ConvertBufferedImage;
import java.net.*;

public class VisionProcessingThread extends Thread{
    
    //camera resolution
    private static Dimension m_camRes; 
    //core interface and webcam variables
	private CameraInterface m_webcam;
	private MultiSpectral<ImageUInt8> m_image;
	
	//loop as long as this is true
	private boolean m_running = true;

	//do different things depending on the target
	Constants.TargetType m_target;
	
	//debug display object
	private DebugDisplay m_display;
	public static boolean m_showDisplay;
	
	//image processing related
	
    public VisionProcessingThread(int camIndex, Constants.TargetType target){
        this("VisionProcessingThread", camIndex, target);
    }
    public VisionProcessingThread(String name, int camIndex, Constants.TargetType target){
        super(name);
        //set target
        m_target = target;
        //init webcam
        m_webcam = new CameraInterface(camIndex, m_camRes);
        //init display
        if(m_showDisplay) {
            System.out.println("[INFO] Initializing display");
            
            //error catching if for example the system is headless
            try {
                m_display = new DebugDisplay(m_camRes);
                System.out.println("[INFO] Display initializion succeeded");
            } catch (Exception e) {
                System.out.println("[ERROR] Cannot initialize display, continuing without one...");
            }
        }
    }
    
    public void run() {
    	while(m_running) {
    		//m_image is the current webcam image
    		m_image = m_webcam.getImage();
    		
    		//the array to send to the rio
    		int[] values;
    		
    		if (m_target == Constants.TargetType.tower) {
    		    values = findTower();
    		}
    		//equals ball
    		else {
    		    values = findBall();
    		}
    		
    		//Send data to RIO
    		if (VisionServerThread.address != null){
    		    
    		    System.out.println("[INFO] Creating packet");
    		    DatagramPacket dataPacket;
                byte[] byteData = new byte[1024];
        		
        		for (int i =0; i< values.length; i++) {
        		    values[i]=(int)(Math.random()*300);
        		}
        		
        		System.out.println(Arrays.toString(values));
        		byteData = NetUtils.intToByte(values);
        		
        		dataPacket = new DatagramPacket(byteData, byteData.length,
        		        VisionServerThread.address,VisionServerThread.port);
        		try {
        	        System.out.println("[INFO] Attempting to send to " + VisionServerThread.address + " on port: " + VisionServerThread.port);
        		    VisionServerThread.socket.send(dataPacket);
        		    System.out.println("[INFO] Success");
        		    Thread.sleep(5000);
        		}
        		catch(Exception e){
        		    System.out.println("[ERROR] Send failed");
        		}
    		}
    		
    		
    		
    		if (m_showDisplay) {
    			//update the image on the display
    		    m_display.setImageRGB(m_image);
    		}
    	}
    }
    
    //code to find the tower
    private int[] findTower() {
        int[] towerData = new int[9];
        
        ImageUInt8 filtered = new ImageUInt8(m_camRes.width, m_camRes.height);
        ThresholdImageOps.threshold(m_image.getBand(0), filtered, 160, true);
        
        CannyEdge<ImageUInt8, ImageSInt16> canny = FactoryEdgeDetectors.canny(2, true, true, ImageUInt8.class, ImageSInt16.class);
        canny.process(filtered, 0.1f, 0.3f, filtered);
        
        PixelMath.multiply(filtered, 255, filtered);
        
        m_image.setBand(0, filtered);
        m_image.setBand(1, filtered);
        m_image.setBand(2, filtered);
        
        return towerData;
    }
    //code to find the ball
    
    private EllipseRotated_F64 largestArea(List<EllipseRotated_F64> ellipses){
    	EllipseRotated_F64 largest = null;
    	
    	for(EllipseRotated_F64 e : ellipses){
    		if(largest == null){
    			largest = e;
    		}
    		else {
    			double largeArea = Math.PI * largest.a * largest.b;
    			double newArea = Math.PI * e.a * e.b;
    			
    			if(newArea > largeArea){
    				largest = e;
    			}
    		}
    	}
    	
    	return largest;
    }
    
    private int[] findBall() {
        int[] ballData = new int[9];
        
        MultiSpectral<ImageFloat32> hsvImage = new MultiSpectral<ImageFloat32>(ImageFloat32.class, m_camRes.width, m_camRes.height, 3);
        ImageUInt8 valueBand = new ImageUInt8(m_camRes.width, m_camRes.height);
        
        //sets m_hsvImage to the hsv version of the source image
        ColorHsv.rgbToHsv_F32(
                ImageConversion.MultiSpectralUInt8ToFloat32(m_image),
                hsvImage);
        
        //extracts just the value band from the hsv image
        ConvertImage.convert(hsvImage.getBand(2), valueBand);
        //valueBand = m_image.getBand(0).clone();
        
        //threshold the image to make the ball clear
        valueBand = ThresholdImageOps.localSquare(valueBand, null, 20, 0.98f, true, null, null);
        
        //edge detect to locate the ball
        CannyEdge<ImageUInt8, ImageSInt16> canny = FactoryEdgeDetectors.canny(2,true, true, ImageUInt8.class, ImageSInt16.class);
        canny.process(valueBand, 0.1f, 0.3f, valueBand);
        BufferedImage gImage = new BufferedImage(valueBand.width, valueBand.height, 4);
        Graphics2D g = gImage.createGraphics();
        g.setStroke(new BasicStroke(2));
        
        ConvertBufferedImage.convertTo_U8(m_image, gImage, true);
        
        List<Contour> contours = BinaryImageOps.contour(valueBand, ConnectRule.EIGHT, null);
        
        List<EllipseRotated_F64> validEllipses = new ArrayList<EllipseRotated_F64>();
        
        for(Contour c : contours){
        	FitData<EllipseRotated_F64> circle = ShapeFittingOps.fitEllipse_I32(c.external, 0, false, null);
        	
        	double ratio = circle.shape.a / circle.shape.b;
        	if(ratio < 1){
        		ratio = 1 / ratio;
        	}
        	
        	double averageRadius = (circle.shape.a + circle.shape.b) / 2;
        	
        	if(ratio < 1.08 && circle.error < 0.1 && averageRadius > 20){
        		validEllipses.add(circle.shape);
        	}
        }
        
        EllipseRotated_F64 ball = largestArea(validEllipses);
        if(ball != null){
	        g.setColor(Color.CYAN);
	        double averageRadius = (ball.a + ball.b) / 2;
	        g.drawOval((int)(ball.center.x - averageRadius), (int)(ball.center.y - averageRadius), (int)averageRadius * 2, (int)averageRadius * 2);
        }
        
        m_image = ImageConversion.toMultiSpectral(gImage);
        
        return ballData;
    }
    
    //mutators
    public static void setResolution(Dimension resolution) {
        m_camRes = resolution;
    }
    public static void setShowDisplay(boolean showDisplay) {
        m_showDisplay = showDisplay;
    }
    
}