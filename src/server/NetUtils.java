package server;

import java.net.DatagramPacket;

import core.VisionServerThread;

public class NetUtils {
    
    public static byte[] intToByte(int[] values){
        int valueCount=0;
        byte[] bytes = new byte[(values.length*4)];
        for (int i=0;i < bytes.length;i++){
            if (i%2==0){
                bytes[i]=(byte)(values[valueCount] & 0xff);
            }
            else {
                bytes[i]=(byte)(((values[valueCount] & 0xff00) >> 8));
                valueCount++;
            }
        }
        return bytes;
        
    }
    
    public static void SendValues(byte[] byteData) {
        if (VisionServerThread.address != null){

            System.out.println("[INFO] Creating packet");
            DatagramPacket dataPacket;

            dataPacket = new DatagramPacket(byteData, byteData.length,
                  VisionServerThread.address,VisionServerThread.port);
            try {
               System.out.println("[INFO] Attempting to send to " + VisionServerThread.address + " on port: " + VisionServerThread.port);
               VisionServerThread.socket.send(dataPacket);
               System.out.println("[INFO] Success");
            }
            catch(Exception e){
               System.out.println("[ERROR] Send failed");
            }
            //System.out.println("values post send" + Arrays.toString(values));
         }
    }
    
}
