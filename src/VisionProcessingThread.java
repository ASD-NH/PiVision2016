import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics.*;
import java.awt.GraphicsEnvironment;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import boofcv.struct.*;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

public class VisionProcessingThread extends Thread{
    
    private Dimension m_camRes = new Dimension(640, 480);
	private CameraInterface m_webcam;
	private MultiSpectral<ImageUInt8> m_image;
	private boolean m_running = true;
	
    public VisionProcessingThread(){
        this("VisionProcessingThread");
    }
    public VisionProcessingThread(String name){
        super(name);
        m_webcam = new CameraInterface(1, m_camRes);
    }
    
    public void run() {
    	while(m_running) {
    		//m_image is the webcam stream
    		m_image = m_webcam.getImage();
    	}
    }

}