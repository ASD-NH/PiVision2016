import java.awt.Dimension;
import java.awt.image.BufferedImage;

import boofcv.gui.image.ImagePanel;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;

public class MultiSpectralDisplay {
    
    private ImagePanel m_display;
    Dimension m_resolution;
    private BufferedImage m_image = null;
    
    MultiSpectralDisplay(Dimension res) {
        
        m_display = new ImagePanel();
        m_resolution = res;
        //init display window
        m_display.setPreferredSize(m_resolution);
        ShowImages.showWindow(m_display, "Debug Display", true);
        
    }
    
    //set source to the RGB image (to be displayed)
    void setImageRGB(MultiSpectral<ImageUInt8> source) {
        
        //convert to displayable format
        m_image = ConvertBufferedImage.convertTo_U8(source, m_image, true);
        //set image
        m_display.setBufferedImageSafe(m_image);
        
    }
    
}