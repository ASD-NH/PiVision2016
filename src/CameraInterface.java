import java.awt.Dimension;
import java.awt.image.*;
import com.github.sarxos.webcam.Webcam;
import magick.*;

public class CameraInterface {

	private Webcam m_webcam = null;
	private Dimension[] m_supportedResolutions;
	
	//create with default webcam
	CameraInterface(Dimension resolution) {
		this(0, resolution);
	}
	//create with index of a webcam (use if there are multiple webcams)
	CameraInterface(int cameraIndex, Dimension resolution) {
		m_webcam = Webcam.getWebcams().get(cameraIndex);
		//m_webcam.open();
		System.out.println("Camera selected: " + m_webcam.getName());
		
		//DEBUG print out supported resolutions
		m_supportedResolutions = m_webcam.getViewSizes();
		System.out.println("Supported resolutions:");
		for (int i = 0; i < m_supportedResolutions.length; i++) {
		    System.out.println(m_supportedResolutions[i].width + 
		            "x" + m_supportedResolutions[i].height);
		}
		
		System.out.println("Attempting to change resolution to " + 
		                    resolution.width + "x" + resolution.height);
		m_webcam.setViewSize(resolution);
		System.out.println("Successfully changed resolution");
		
		m_webcam.open();
	}
	
	//Convert image format received from webcam to a format that can be operated on
	private MagickImage toMagickImage(BufferedImage bi) {
		
		//create raw data from buffered image
		Object data = null;
	    int w = bi.getWidth();
	    int h = bi.getHeight();
	    data = ((DataBufferByte)bi.getRaster().getDataBuffer()).getData();
	    
	    //use raw data to make a MagickImage
	    MagickImage converted = new MagickImage();
	    try {
			converted.constituteImage(w,h,"RGB",(byte[])data);
		} catch (MagickException e) {
			System.out.println("Error converting BufferedImage to MagickImage. Check camera input.");
		}
	    
	    return converted;
	}
	
	public MagickImage getImage() {
		//return converted image
		return toMagickImage(m_webcam.getImage());
	}

}