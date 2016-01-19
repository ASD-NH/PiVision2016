import java.util.*;
import java.lang.*;
import java.io.*;

public class VisionServer{
    public static void main(String[] args) throws IOException
        {
            //new VisionServerThread().start();
            new VisionProcessingThread().start();
        }
}
