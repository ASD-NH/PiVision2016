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

import magick.*;
import magick.util.*;

public class VisionProcessingThread extends Thread{
    
    private Dimension m_camRes = new Dimension(640, 480);
	private CameraInterface m_webcam;
	private MagickImage m_image;
	private boolean m_running = true;
	
    public VisionProcessingThread(){
        this("VisionProcessingThread");
    }
    public VisionProcessingThread(String name){
        super(name);
        m_webcam = new CameraInterface(m_camRes);
    }
    
    public void run() {
    	MagickCanvas display = new MagickCanvas();
    	display.setEnabled(true);
    	display.setVisible(true);
    	JFrame f = new JFrame("Debug Display");
    	f.setSize(300, 400);
    	f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	f.setVisible(true);
    	while(m_running) {
    		//m_image is the webcam stream
    		m_image = m_webcam.getImage();
    		display.setImage(m_image);
    		display.update(f.getGraphics());
    	}
    }

}