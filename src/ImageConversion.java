import java.awt.image.BufferedImage;

import boofcv.core.image.ConvertImage;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.ImageUInt8;
import boofcv.struct.image.MultiSpectral;

//Commonly used image conversion utilities not included in BoofCV
public class ImageConversion {
    
    //Convert buffered image to MultiSpectral UInt8
    public static MultiSpectral<ImageUInt8> toMultiSpectral(BufferedImage input) {
        MultiSpectral<ImageUInt8> output = ConvertBufferedImage.convertFromMulti(
                input,
                null,
                true,
                ImageUInt8.class);
        return output;
    }
    
    //Convert MultiSpectral UInt8 to MultiSpectral Float32
    public static MultiSpectral<ImageFloat32>
    MultiSpectralUInt8ToFloat32(MultiSpectral<ImageUInt8> input) {
        MultiSpectral<ImageFloat32> out =
                new MultiSpectral<ImageFloat32>(ImageFloat32.class,
                input.getWidth(), input.getHeight(),
                input.getNumBands());

        for (int i = 0; i < input.getNumBands(); i++) {
            out.bands[i] =
                    ConvertImage.convert(input.getBand(i), out.bands[i]);
        }
        
        return out;
        
    }
    
    //Convert MultiSpectral Float32 to MultiSpectral UInt8
    public static MultiSpectral<ImageUInt8> 
    MultiSpectralFloat32ToUInt8(MultiSpectral<ImageFloat32> input) {
        MultiSpectral<ImageUInt8> out =
                new MultiSpectral<ImageUInt8>(ImageUInt8.class,
                        input.getWidth(), input.getHeight(),
                        input.getNumBands());

        for (int i = 0; i < input.getNumBands(); i++) {
            out.bands[i] =
                    ConvertImage.convert(input.getBand(i), out.bands[i]);
        }
        
        return out;
        
    }
    
}