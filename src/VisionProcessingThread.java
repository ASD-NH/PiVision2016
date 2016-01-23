import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import boofcv.struct.*;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
import boofcv.alg.color.ColorHsv;
import boofcv.core.image.ConvertImage;
import boofcv.gui.image.*;
import boofcv.io.image.ConvertBufferedImage;

public class VisionProcessingThread extends Thread{
    
    //camera resolution
    private static Dimension m_camRes;
    
    //core interface and webcam variables
	private CameraInterface m_webcam;
	private static int m_webcamIndex;
	private MultiSpectral<ImageUInt8> m_image;
	private boolean m_running = true;
	
	//debug display object
	private MultiSpectralDisplay m_display;
	public static boolean m_showDisplay;
	
	//image processing related
	MultiSpectral<ImageFloat32> m_hsvImage = new MultiSpectral<ImageFloat32>(ImageFloat32.class, m_camRes.width, m_camRes.height, 3);
	
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
    		
    		
    		
    		
    		if (m_showDisplay) {
    			//Convert the HSV image to RGB temporarily.
    			ColorHsv.hsvToRgb_F32(m_hsvImage, m_hsvImage);
    			 //reduce precision to UInt8 for display
                m_image = ImageConversion.MultiSpectralFloat32ToUInt8(m_hsvImage);
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