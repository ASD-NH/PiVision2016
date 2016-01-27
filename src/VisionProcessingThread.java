import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import boofcv.struct.*;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageSInt16;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import boofcv.alg.color.ColorHsv;
import boofcv.alg.feature.detect.edge.CannyEdge;
import boofcv.alg.filter.binary.BinaryImageOps;
import boofcv.alg.filter.binary.GThresholdImageOps;
import boofcv.alg.misc.PixelMath;
import boofcv.core.image.ConvertImage;
import boofcv.factory.feature.detect.edge.FactoryEdgeDetectors;
import boofcv.gui.image.*;
import boofcv.io.image.ConvertBufferedImage;
import java.net.*;

public class VisionProcessingThread extends Thread{
    
    //camera resolution
    private static Dimension m_camRes; 
    int[] values = new int[8];
    //core interface and webcam variables
	private CameraInterface m_webcam;
	private static int m_webcamIndex;
	private MultiSpectral<ImageUInt8> m_image;
	private boolean m_running = true;
    Utilities util= new Utilities();

	//debug display object
	private MultiSpectralDisplay m_display;
	public static boolean m_showDisplay;
	
	//image processing related
	MultiSpectral<ImageFloat32> m_hsvImage = new MultiSpectral<ImageFloat32>(ImageFloat32.class, m_camRes.width, m_camRes.height, 3);
	ImageUInt8 m_valueBand = new ImageUInt8(m_camRes.width, m_camRes.height);
	
    public VisionProcessingThread(){
        this("VisionProcessingThread");
    }
    public VisionProcessingThread(String name){
        super(name);
        //init webcam
        m_webcam = new CameraInterface(m_webcamIndex, m_camRes);
        //init display
        if(m_showDisplay) {
            System.out.println("[INFO] Initializing display");
            
            //error catching if for example the system is headless
            try {
                m_display = new MultiSpectralDisplay(m_camRes);
                System.out.println("[INFO] Display initializion succeeded");
            } catch (Exception e) {
                System.out.println("[ERROR] Cannot initialize display, continuing without one...");
            }
        }
    }
    
    public void run() {
        System.out.println("[INFO] Starting main processing loop");
    	while(m_running) {
    		//m_image is the current webcam image
    		m_image = m_webcam.getImage();
    		
    		//sets m_hsvImage to the rgb --> hsv conversion result
    		ColorHsv.rgbToHsv_F32(
    		        ImageConversion.MultiSpectralUInt8ToFloat32(m_image),
    		        m_hsvImage);
    		
    		ConvertImage.convert(m_hsvImage.getBand(2), m_valueBand);
    		
    		GThresholdImageOps.localSauvola(m_hsvImage.getBand(2), m_valueBand, 20, 0.3f, true);    		
    		
    		CannyEdge<ImageUInt8, ImageSInt16> canny = FactoryEdgeDetectors.canny(2,true, true, ImageUInt8.class, ImageSInt16.class);
    		canny.process(m_valueBand, 0.1f, 0.3f, m_valueBand);
    		//TODO use this "canny" detector to draw/process the curves
    		
    		PixelMath.multiply(m_valueBand, 255, m_valueBand);
    		
    		m_image.setBand(0, m_valueBand);
    		m_image.setBand(1, m_valueBand);
    		m_image.setBand(2, m_valueBand);
    		
    		
    		if (VisionServerThread.address != null){
    		    System.out.println("Creating Packet");
    		    DatagramPacket dataPacket;
                byte[] byteData = new byte[1024];
        		
        		for (int i =0;i<values.length;i++){
        		    values[i]=(int)(Math.random()*300);
        		}
        		System.out.println(Arrays.toString(values));
        		byteData = util.intToByte(values);
        		
        		 dataPacket = new DatagramPacket(byteData,byteData.length,VisionServerThread.address,VisionServerThread.port);
        		try {
        	        System.out.println("trying to send to " + VisionServerThread.address + "on port: " + VisionServerThread.port);
        		    VisionServerThread.socket.send(dataPacket);
        		    System.out.println("sent");
        		    Thread.sleep(5000);
        		}
        		catch(Exception e){
        		    e.printStackTrace();
        		    System.out.println("send error");
        		}
    		}
    		if (m_showDisplay) {
    			
    			 //reduce precision to UInt8 for display
                //m_image = ImageConversion.MultiSpectralFloat32ToUInt8(m_hsvImage);
                //debug display update image
    		    m_display.setImageRGB(m_image);
    		}
    	}
    }
    
    //mutators
    public static void setWebcam(int index) {
        m_webcamIndex = index;
    }
    public static void setResolution(Dimension resolution) {
        m_camRes = resolution;
    }
    public static void setShowDisplay(boolean showDisplay) {
        m_showDisplay = showDisplay;
    }
    
}