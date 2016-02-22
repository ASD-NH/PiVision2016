package core;
import java.awt.Dimension;

public final class Constants {
    
    public enum TargetType {
        ball,
        tower
    }
    
    //resolution presets
    public static final Dimension MAX_RES = new Dimension(640, 480);
    public static final Dimension MED_RES = new Dimension(320, 240);
    public static final Dimension MIN_RES = new Dimension(176, 144);
    
    //Data flags: Element 0 in sent data arrays; indicate whether received data
    //pertains to tower (1) or ball (2)
    public static final int TOWER_FLAG = 1;
    public static final int TOWER_SIZE = 9;
    public static final int BALL_FLAG = 2;
    public static final int BALL_SIZE = 4;
    
    /* TOWER FILTER */
    //This is the number of frames that have to get a positive match before a target will show up as detected
    public static final int TOWER_HISTORY_SIZE = 1;
    //The number of blank data arrays that will be tolerated before we decide the target is no longer there
    public static final int TOWER_FLICKER_TOLERANCE = 1;
    
    /* BALL FILTER */
    //This is the number of frames that have to get a positive match before a target will show up as detected
    public static final int BALL_HISTORY_SIZE = 1;
    //The number of blank data arrays that will be tolerated before we decide the target is no longer there
    public static final int BALL_FLICKER_TOLERANCE = 1;
    
}