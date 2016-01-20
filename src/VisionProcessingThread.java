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
    
    //Camera quality values
    private static final Dimension MAX = new Dimension(640, 480);
    private static final Dimension MED = new Dimension(320, 240);
    private static final Dimension MIN = new Dimension(176, 144);
    
    //camera resolution
    private static Dimension m_camRes;
    
    //core interface and webcam variables
	private CameraInterface m_webcam;
	public static int m_webcamIndex = 0;
	private MultiSpectral<ImageUInt8> m_image;
	private boolean m_running = true;
	
	//debug display object
	private MultiSpectralDisplay m_display;
	public static boolean m_displayDisplay;
	
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
        if(m_displayDisplay) {
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
        System.out.println("[INFO] Starting main loop");
    	while(m_running) {
    		//m_image is the current webcam image
    		m_image = m_webcam.getImage();
    		
    		//sets m_hsvImage to the rgb --> hsv conversion result
    		ColorHsv.rgbToHsv_F32(
    		        ImageConversion.MultiSpectralUInt8ToFloat32(m_image),
    		        m_hsvImage);
    		
    		
    		if (m_displayDisplay) {
    			//Convert the HSV image to RGB temporarily.
    			ColorHsv.hsvToRgb_F32(m_hsvImage, m_hsvImage);
    			 //reduce precision to UInt8 for display
                m_image = ImageConversion.MultiSpectralFloat32ToUInt8(m_hsvImage);
                //debug display update image
    		    m_display.setImageRGB(m_image);
    		}
    	}
    }
   
    
    /*
     * Below here are just commandline argument related functions
     * Safe to ignore for the most part
     */
    
   //set resolution
    public static void setResolution(String quality) {
        if (quality.equals("maximum")) {
            m_camRes = MAX;
            System.out.println("[INIT] Initializing at maximum resolution");
        }
        else if (quality.equals("medium")) {
            m_camRes = MED;
            System.out.println("[INIT] Initializing at medium resolution");
        }
        //assume minimum if nothing matches
        else {
            m_camRes = MIN;
            System.out.println("[INIT] Initializing at minimum resolution");
        }
    }
    
    //argument sanity check
    public static boolean checkArgs(String[] args) {
        if (args[args.length - 1].equals("true") || 
                args[args.length - 1].equals("false")) {
            
            if(args.length == 2 && (args[0].equals("minimum") ||
                                    args[0].equals("medium") ||
                                    args[0].equals("maximum"))) {
                return true;
            }
            else if(args.length == 3 && (args[1].equals("minimum") ||
                                         args[1].equals("medium") ||
                                         args[1].equals("maximum")) &&
                                         isInteger(args[0])) {
                return true;
            }
            else {
                return false;
            }
            
        }
        else {
            return false;
        }
    }
    
    //checks if strings are integers
    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }
    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
}