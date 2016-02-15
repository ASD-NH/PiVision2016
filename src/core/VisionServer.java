package core;
import java.util.*;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

import java.lang.*;
import java.io.*;

public class VisionServer{
    //top left port is 0 top right port is 1
    //Uncomment this out when deploying to the pi (sets the webcam driver to the pi one)
    static {
        Webcam.setDriver(new V4l4jDriver());
    }
    
    public static void main(String[] args) throws IOException
    {
        
            new VisionServerThread().start();
            
            new VisionProcessingThread(1, Constants.TargetType.tower).start();
            new VisionProcessingThread(0, Constants.TargetType.ball).start();

        
    }
    
}