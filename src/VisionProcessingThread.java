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
    static final Dimension MAX = new Dimension(640, 480);
    static final Dimension MED = new Dimension(320, 240);
    static final Dimension MIN = new Dimension(176, 144);
    
    //set camera resolution
    private Dimension m_camRes = MAX;
    
    //core interface and webcam variables
	private CameraInterface m_webcam;
	private MultiSpectral<ImageUInt8> m_image;
	private boolean m_running = true;
	
	//debug display object
	private MultiSpectralDisplay m_display;
	
	//image processing related
	MultiSpectral<ImageFloat32> m_hsvImage = new MultiSpectral<ImageFloat32>(ImageFloat32.class, m_camRes.width, m_camRes.height, 3);
	
    public VisionProcessingThread(){
        this("VisionProcessingThread");
    }
    public VisionProcessingThread(String name){
        super(name);
        //init webcam
        m_webcam = new CameraInterface(1, m_camRes);
        //init display
        m_display = new MultiSpectralDisplay(m_camRes);
    }
    
    public void run() {
    	while(m_running) {
    		//m_image is the current webcam image
    		m_image = m_webcam.getImage();
    		
    		ColorHsv.rgbToHsv_F32(
    		        ImageConversion.MultiSpectralUInt8ToFloat32(m_image),
    		        m_hsvImage);
    		m_image = ImageConversion.MultiSpectralFloat32ToUInt8(m_hsvImage);
    		
    		//debug display update image
    		m_display.setImageRGB(m_image);
    	}
    }
    
}