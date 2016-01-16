import java.awt.image.*;
import com.github.sarxos.webcam.Webcam;
import magick.*;

public class CameraInterface {

	private Webcam m_webcam = null;
	
	//create with default webcam
	CameraInterface() {
		m_webcam = Webcam.getDefault();
		m_webcam.open();
		System.out.println("Camera selected: " + m_webcam.getName());
	}
	//create with index of a webcam (use if there are multiple webcams)
	CameraInterface(int cameraIndex) {
		m_webcam = Webcam.getWebcams().get(cameraIndex);
		m_webcam.open();
		System.out.println("Camera selected: " + m_webcam.getName());
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
			e.printStackTrace();
		}
	    
	    return converted;
	}
	
	public MagickImage getImage() {
		//return converted image
		return toMagickImage(m_webcam.getImage());
	}

}