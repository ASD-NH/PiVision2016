package server;

import java.net.DatagramPacket;

import core.VisionServerThread;

public class NetUtils {
    
    public static byte[] intsToBytes(int[] values){
        byte[] bytes = new byte[(values.length*4)];
        byte[] tempBytes = new byte[4];
        
        for (int i =0; i<values.length;i++){
            tempBytes=intToByte(values[i]);
            for (int j =0;j <4 ;j++){
               bytes[i*4+j]=tempBytes[i];
            }
         }
        return bytes;
        
    }
    public static byte[] intToByte(int num){
        byte[] m_intToByteConvertBuf = new byte[4];
        m_intToByteConvertBuf[0] = (byte) ((num & 0xff000000) >> 24);
        m_intToByteConvertBuf[1] = (byte) ((num & 0xff0000) >> 16);
        m_intToByteConvertBuf[2] = (byte) ((num & 0xff00) >> 8);
        m_intToByteConvertBuf[3] = (byte) (num & 0xff);
        
        return m_intToByteConvertBuf;
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
