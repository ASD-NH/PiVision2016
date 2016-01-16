import java.awt.Dimension;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.ImageIO;
import magick.*;
import magick.util.*;

public class VisionProcessingThread extends Thread{
    
	private CameraInterface m_webcam;
	private MagickImage m_image;
	private boolean m_running = true;
	
    public VisionProcessingThread(){
        this("VisionProcessingThread");
    }
    public VisionProcessingThread(String name){
        super(name);
        m_webcam = new CameraInterface();
    }
    
    public void run() {
    	while(m_running) {
    		//m_image is the webcam stream
    		m_image = m_webcam.getImage();
    	}
    }

}