import java.util.*;
import com.github.sarxos.webcam.Webcam;
//import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import java.lang.*;
import java.io.*;

public class VisionServer{
    
    //Uncomment this out when deploying to the pi (sets the webcam driver to the pi one)
    static {
        //Webcam.setDriver(new V4l4jDriver());
    }
    
    public static void main(String[] args) throws IOException
    {
        
            new VisionServerThread().start();
            
            ParseArguments.parse(args);
            new VisionProcessingThread().start();
        
    }
    
}