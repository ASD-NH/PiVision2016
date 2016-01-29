import java.awt.Dimension;

public final class Constants {
    
    enum TargetType {
        ball,
        tower
    }
    
    //resolution presets
    public static final Dimension MAX_RES = new Dimension(640, 480);
    public static final Dimension MED_RES = new Dimension(320, 240);
    public static final Dimension MIN_RES = new Dimension(176, 144);
    
    //default settings
    public static final int DEFAULT_CAM = 0;
    public static final Dimension DEFAULT_RES = MED_RES;
    public static final boolean DEFAULT_DISPLAY = false;
    
    //usage message to be printed if arguments do not match
    public static final String USAGE_MESSAGE = "Usage:\n\n"
            + "Use system default camera:\n"
            + "\tVisionServer.jar (String) Quality (boolean) ShowDisplay\n"
            + "\tQuality Options: minimum, medium, maximum\n\n"
            + "Set specific camera:\n"
            + "\tVisionServer.jar (int) CameraIndex (String) Quality (boolean) ShowDisplay\n"
            + "\tQuality Options: minimum, medium, maximum\n\n"
            + "Launching anyway with the default values (see Constants.java)\n";
    
}