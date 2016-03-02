package core;
import java.awt.Dimension;

import com.github.sarxos.webcam.Webcam;

import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;
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