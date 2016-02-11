package core;
import java.awt.Dimension;
import java.awt.image.*;
import com.github.sarxos.webcam.Webcam;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.*;
import targeting.ImageConversion;

public class CameraInterface {

	private Webcam m_webcam = null;
	
	//create with default webcam
	CameraInterface(Dimension resolution) {
		this(0, resolution);
	}
	
	//create with index of a webcam (use if there are multiple webcams)
	CameraInterface(int cameraIndex, Dimension resolution) {
		m_webcam = Webcam.getWebcams().get(cameraIndex);
		//m_webcam.open();
		System.out.println("[INFO] Camera selected: " + m_webcam.getName());
		
		//Change resolution
		m_webcam.setViewSize(resolution);
		
		m_webcam.open();
	}
	
	public MultiSpectral<ImageUInt8> getImage() {
		//return converted image
		return ImageConversion.toMultiSpectral(m_webcam.getImage());
	}

}