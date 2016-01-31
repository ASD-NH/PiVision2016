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
import georegression.struct.point.Point2D_F32;
import boofcv.alg.color.ColorHsv;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.feature.detect.edge.EdgeContour;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.Contour;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.filter.binary.ThresholdImageOps;
import boofcv.alg.misc.PixelMath;
import boofcv.alg.shapes.ShapeFittingOps;
import boofcv.core.image.ConvertImage;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.feature.VisualizeShapes;
import boofcv.gui.image.*;
import boofcv.io.image.ConvertBufferedImage;
import java.net.*;

class foundTarget {
	public List<PointIndex_I32> m_rawVertexes;
	public Dimension m_boundsBox;
	public List<PointIndex_I32> m_bounds;
	
	public foundTarget(List<PointIndex_I32> rawVertexes, Dimension boundsBox, List<PointIndex_I32> bounds){
		m_rawVertexes = rawVertexes;
		m_boundsBox = boundsBox;
		m_bounds = bounds;
	}
	
	public foundTarget(){
		m_rawVertexes = new ArrayList<PointIndex_I32>();
		m_boundsBox = new Dimension();
		m_bounds = new ArrayList<PointIndex_I32>();
	}
	
	public PointIndex_I32 getCenter(){
		
		int centerX = (m_bounds.get(0).x + m_bounds.get(1).x) / 2;
		int centerY = (m_bounds.get(0).y + m_bounds.get(3).y) / 2;
		
		return new PointIndex_I32(centerX, centerY, 0);
	}
}

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
    
    private double findLargestSegment(List<PointIndex_I32> vertexes, List<PointIndex_I32> segment){
    	double longestLength = 0;
    	
    	for(int i = 0; i < vertexes.size() - 1; i++){
    		double length = vertexes.get(i).distance(vertexes.get(i + 1));
    		if(length > longestLength){
    			longestLength = length;
    			if(segment != null){
	    			segment.clear();
	    			segment.add(vertexes.get(i));
	    			segment.add(vertexes.get(i + 1));
    			}
    		}
    	}
    	
    	return longestLength;
    }
    
    private List<PointIndex_I32> findBounds(List<PointIndex_I32> vertexes, int maxHeight, int maxWidth, Dimension dimension){
    	List<PointIndex_I32> result = new ArrayList<PointIndex_I32>();
    	
    	int minX = maxWidth;
    	int minY = maxHeight;
    	int maxX = 0;
    	int maxY = 0;
    	
    	for(PointIndex_I32 p : vertexes){
    		minX = Math.min(p.x, minX);
    		minY = Math.min(p.y, minY);
    		maxX = Math.max(p.x, maxX);
    		maxY = Math.max(p.y, maxY);
    	}
    	
    	result.add(new PointIndex_I32(minX, minY, 0));
    	result.add(new PointIndex_I32(maxX, minY, 1));
    	result.add(new PointIndex_I32(maxX, maxY, 2));
    	result.add(new PointIndex_I32(minX, maxY, 3));
    	
    	if(dimension != null){
    		dimension.width = maxX - minX;
    		dimension.height = maxY - minY;
    	}
    	
    	return result;
    }
    
    //code to find the tower
    private int[] findTower() {
        int[] towerData = new int[9];
        
        ImageUInt8 filtered = new ImageUInt8(m_camRes.width, m_camRes.height);
        ThresholdImageOps.threshold(m_image.getBand(0), filtered, 160, true);
        
        CannyEdge<ImageUInt8, ImageSInt16> canny = FactoryEdgeDetectors.canny(2, true, true, ImageUInt8.class, ImageSInt16.class);
        canny.process(filtered, 0.1f, 0.3f, filtered);
        
        List<Contour> contours = BinaryImageOps.contour(filtered, ConnectRule.EIGHT, null);
        BufferedImage gImage = new BufferedImage(filtered.width, filtered.height, 4);
        Graphics2D g = gImage.createGraphics();
        g.setStroke(new BasicStroke(2));
        
        ConvertBufferedImage.convertTo_U8(m_image, gImage, true);
        List<foundTarget> targets = new ArrayList<foundTarget>();
        
        for(Contour c : contours){
        	List<PointIndex_I32> vertexes = ShapeFittingOps.fitPolygon(c.external,true,0.05,0,100);
        	Dimension boundsBox = new Dimension();
        	List<PointIndex_I32> bounds = findBounds(
        			vertexes,
        			m_camRes.height,
        			m_camRes.width,
        			boundsBox);
        	
        	double ratio = boundsBox.getHeight() / boundsBox.getWidth();
        	
        	double length = findLargestSegment(vertexes, null);
        	
        	if(vertexes.size() < 25 && vertexes.size() > 5
        			&& ratio > 0.6 && ratio < 0.9){
	        	targets.add(new foundTarget(vertexes, boundsBox, bounds));
        	}
        }
        
        foundTarget centralTarget = null;
        PointIndex_I32 screenCenter = new PointIndex_I32(m_camRes.width / 2, m_camRes.height / 2, 0);
        
        for(foundTarget t : targets){
        	if(centralTarget == null){
        		centralTarget = t;
        	}
        	else {
        		PointIndex_I32 oldCenter = centralTarget.getCenter();
        		PointIndex_I32 newCenter = t.getCenter();
        		
        		if(newCenter.distance(screenCenter) < oldCenter.distance(screenCenter)){
        			centralTarget = t;
        		}
        	}
        }
        
        if(centralTarget != null){
	        g.setColor(Color.GREEN);
	    	VisualizeShapes.drawPolygon(centralTarget.m_bounds, true, g);
	    	g.drawOval(centralTarget.getCenter().x, centralTarget.getCenter().y, 5, 5);
        }
        
        m_image = ImageConversion.toMultiSpectral(gImage);
        
        /*
         * Format for data:
         *  -tower or ball (tower = 0)
         *  -top left x
         *  -top left y
         *  -top right x
         *  -top right y
         *  -bottom left x
         *  -bottom left y
         *  -bottom right x
         *  -bottom right y
         */
        if(centralTarget != null){
	        towerData[0] = 0;
	        towerData[1] = centralTarget.m_bounds.get(0).x;
	        towerData[2] = centralTarget.m_bounds.get(0).y;
	        towerData[3] = centralTarget.m_bounds.get(1).x;
	        towerData[4] = centralTarget.m_bounds.get(1).y;
	        towerData[5] = centralTarget.m_bounds.get(3).x;
	        towerData[6] = centralTarget.m_bounds.get(3).y;
	        towerData[7] = centralTarget.m_bounds.get(2).x;
	        towerData[8] = centralTarget.m_bounds.get(2).y;
        }
        else {
        	towerData[0] = 0;
        	for(int i = 1; i < towerData.length; i++){
        		towerData[i] = -1;
        	}
        }
        
        return towerData;
    }
    //code to find the ball
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
        
        //threshold the image to make the ball clear
        GThresholdImageOps.localSauvola(hsvImage.getBand(2), valueBand, 20, 0.3f, true);            
        
        //edge detect to locate the ball
        CannyEdge<ImageUInt8, ImageSInt16> canny = FactoryEdgeDetectors.canny(2,true, true, ImageUInt8.class, ImageSInt16.class);
        canny.process(valueBand, 0.1f, 0.3f, valueBand);
        
        //update m_image to be used for displaying the result
        PixelMath.multiply(valueBand, 255, valueBand);
        m_image.setBand(0, valueBand);
        m_image.setBand(1, valueBand);
        m_image.setBand(2, valueBand);
        
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