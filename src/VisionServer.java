import java.util.*;
import java.lang.*;
import java.io.*;

public class VisionServer{
    
    //Uncomment this out when deploying to the pi (sets the webcam driver to the pi one)
    static {
        //Webcam.setDriver(new V4l4jDriver());
    }
    
    public static void main(String[] args) throws IOException
    {
        //run set functions on VisionProcessingThread depending on # of args
        if (args.length == 3 && VisionProcessingThread.checkArgs(args)) {
            
            new VisionServerThread().start();
            
            VisionProcessingThread.m_webcamIndex = Integer.parseInt(args[0]);
            System.out.println("[INIT] Initializing at webcam index " + args[0]);
            
            VisionProcessingThread.setResolution(args[1]);
            
            VisionProcessingThread.m_displayDisplay = Boolean.parseBoolean(args[2]);
            if (VisionProcessingThread.m_displayDisplay) {System.out.println("[INIT] Initializing with display");}
            else {System.out.println("[INIT] Initializing without display");}
            
            new VisionProcessingThread().start();
            
        }
        else if (args.length == 2 && VisionProcessingThread.checkArgs(args)) {
            
            new VisionServerThread().start();;
            
            System.out.println("[INIT] Initializing at default webcam");
            
            VisionProcessingThread.setResolution(args[0]);
            
            VisionProcessingThread.m_displayDisplay = Boolean.parseBoolean(args[1]);
            if (VisionProcessingThread.m_displayDisplay) {System.out.println("[INIT] Initializing with display");}
            else {System.out.println("[INIT] Initializing without display");}
            
            new VisionProcessingThread().start();
            
        }
        //launch anyway with defaults and print a warning
        else {
            //print usage message
            System.out.println("[WARNING] Incorrect or missing arguments");
            System.out.print("Usage:\n\n"
                            + "Use system default camera:\n"
                            + "\tVisionServer.jar (String) Quality (boolean) ShowDisplay\n"
                            + "\tQuality Options: minimum, medium, maximum\n\n"
                            + "Set specific camera:\n"
                            + "\tVisionServer.jar (int) CameraIndex (String) Quality (boolean) ShowDisplay\n"
                            + "\tQuality Options: minimum, medium, maximum\n");
            
            //start anyway with some default options
            System.out.println("\nLaunching anyway with default camera, medium resolution, and no display");
            new VisionServerThread().start();;
            VisionProcessingThread.setResolution("medium");
            VisionProcessingThread.m_displayDisplay = Boolean.parseBoolean("false");
            new VisionProcessingThread().start();
        }
        
    }
    
}
