import java.awt.Dimension;
import java.awt.image.*;
import com.github.sarxos.webcam.Webcam;

import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.*;

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
		System.out.println("[INFO] Camera selected: " + m_webcam.getName());
		
		//DEBUG print out supported resolutions
		m_supportedResolutions = m_webcam.getViewSizes();
		System.out.println("[INFO] Supported resolutions:");
		for (int i = 0; i < m_supportedResolutions.length; i++) {
		    System.out.println("[INFO] " + m_supportedResolutions[i].width + 
		            "x" + m_supportedResolutions[i].height);
		}
		
		//Change resolution
		System.out.println("[INFO] Attempting to change resolution to " + 
		                    resolution.width + "x" + resolution.height);
		m_webcam.setViewSize(resolution);
		System.out.println("[INFO] Successfully changed resolution");
		
		m_webcam.open();
	}
	
	//converts source image type to boof type
	
	
	public MultiSpectral<ImageUInt8> getImage() {
		//return converted image
		return ImageConversion.toMultiSpectral(m_webcam.getImage());
	}

}