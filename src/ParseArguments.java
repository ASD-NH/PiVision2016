import java.awt.Dimension;

class ParseArguments {
    
    public static void parse(String[] args) {
        if (isValid(args)) {
            if (args.length == 3) {
                printWebcamInfo(Integer.parseInt(args[0]));
                VisionProcessingThread.setWebcam(Integer.parseInt(args[0]));
                
                //this function prints its own init info
                setResolution(args[1]);
                
                printDisplayInfo(Boolean.parseBoolean(args[2]));
                VisionProcessingThread.setShowDisplay(Boolean.parseBoolean(args[2]));
            }
            //args.length = 2
            else {
                //two arguments means we want to use default webcam
                printWebcamInfo(Constants.DEFAULT_CAM);
                VisionProcessingThread.setWebcam(Constants.DEFAULT_CAM);
                
                //this function prints its own init info
                setResolution(args[0]);
                
                printDisplayInfo(Boolean.parseBoolean(args[1]));
                VisionProcessingThread.setShowDisplay(Boolean.parseBoolean(args[1]));
            }
        }
        else {
            
            System.out.println(Constants.USAGE_MESSAGE);
            
            printWebcamInfo(Constants.DEFAULT_CAM);
            VisionProcessingThread.setWebcam(Constants.DEFAULT_CAM);
            
            printResolutionInfo(Constants.DEFAULT_RES);
            VisionProcessingThread.setResolution(Constants.DEFAULT_RES);
            
            printDisplayInfo(Constants.DEFAULT_DISPLAY);
            VisionProcessingThread.setShowDisplay(Constants.DEFAULT_DISPLAY);
            
        }
    }
    
    public static boolean isValid(String[] args) {
        if (args[args.length - 1].equals("true") || 
                args[args.length - 1].equals("false")) {
            
            if(args.length == 2 && (args[0].equals("minimum") ||
                                    args[0].equals("medium") ||
                                    args[0].equals("maximum"))) {
                return true;
            }
            else if(args.length == 3 && (args[1].equals("minimum") ||
                                         args[1].equals("medium") ||
                                         args[1].equals("maximum")) &&
                                         isInteger(args[0])) {
                return true;
            }
            else {
                return false;
            }
            
        }
        else {
            return false;
        }
    }
    
    public static void setResolution(String quality) {
        if (quality.equals("maximum")) {
            VisionProcessingThread.setResolution(Constants.MAX_RES);
            System.out.println("[INIT] Initializing at maximum resolution");
        }
        else if (quality.equals("medium")) {
            VisionProcessingThread.setResolution(Constants.MED_RES);
            System.out.println("[INIT] Initializing at medium resolution");
        }
        //assume minimum if nothing matches
        else {
            VisionProcessingThread.setResolution(Constants.MIN_RES);
            System.out.println("[INIT] Initializing at minimum resolution");
        }
    }
    
    //used for making sure the int portion of the argument is actually an int
    public static boolean isInteger(String s) {
        return isInteger(s,10);
    }
    public static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
    
    //init printing where it isn't done already
    public static void printWebcamInfo(int index) {
        if (index == 0) {
            System.out.println("[INIT] Initializing with default webcam");
        }
        else {
            System.out.println("[INIT] Initializing at webcam " + index);
        }
    }
    public static void printDisplayInfo(boolean display) {
        if (display) {
            System.out.println("[INIT] Initializing with display");
        }
        else {
            System.out.println("[INIT] Initializing without display");
        }
    }
    public static void printResolutionInfo(Dimension resolution) {
        if (resolution.equals(Constants.MAX_RES)) {
            System.out.println("[INIT] Initializing at maximum resolution");
        }
        else if (resolution.equals(Constants.MED_RES)) {
            System.out.println("[INIT] Initializing at medium resolution");
        }
        //assume minimum if nothing matches
        else {
            System.out.println("[INIT] Initializing at minimum resolution");
        }
    }
    
}