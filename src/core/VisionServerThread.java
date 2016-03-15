package core;
import java.io.IOException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import server.NetUtils;

public class VisionServerThread extends Thread
{
   
   
    private static boolean newDataRequested = false;
    
    public static DatagramSocket socket = null;
    private boolean running = true;
    public static int port = 00000;
    public static InetAddress address = null;
    DatagramPacket receivePacket;
    DatagramPacket responsePacket;
    byte[] receivedData; 
    byte[] sendData;
    public static int[] decodedData;
    int[] startArray ={0,0,0,0,0,0,0,0,0};
    int[] requestArray ={3,3,3,3,3,3,3,3,3};
    //# to start from when sequentially trying sockets (for error catching)
    private int baseSocket = 31415;
    private int currSocket = baseSocket;
    //exit if this stays true for the entire try/catch block
    private boolean errorFree = false;
    
    public VisionServerThread() throws IOException{
        this("VisionSeverThread");
    }
    public VisionServerThread(String name) throws IOException{
        super(name);
        //Error catching
        while (!errorFree) {
            //open socket cannot be found, break
            if (currSocket - baseSocket > 50) {
                System.out.println("[ERROR] Open socket could not be found, aborting thread");
                break;
            }
            try {
                errorFree = true;
                socket = new DatagramSocket(currSocket);
                System.out.println("[INFO] Socket Initialized at: " + currSocket);
            } catch (BindException e) {
                errorFree = false;
                currSocket += 1;
                System.out.println("[WARNING] Socket" +(currSocket-1) +"already in use, trying socket " + currSocket);
            }
        }
        
    }
  
    public void run(){
        if (socket == null){
            return;
        }
        else {
            
           
           
            while(running){
                try{
                    System.out.println("[INFO] Waiting to Receive Packet");
                    receivedData= new byte[1024];
                    receivePacket = new DatagramPacket(receivedData,receivedData.length);
                    socket.receive(receivePacket);
                   
                    address = receivePacket.getAddress();
                    port = receivePacket.getPort();
                    receivedData = receivePacket.getData();
                    
                    decodedData = NetUtils.byteToInt(receivedData);
                    System.out.println("Packet Received from: " + address + " on port: " + port + " = " + decodedData + " length of "+ receivePacket.getLength());
                    
                    sendData = new byte[1024];
                    
                    if (Arrays.equals(decodedData,startArray)){
                        int[] values ={0,0,0,0,0,0,0,0};
                        sendData=NetUtils.intToByte(values);
                    }
                    else if (Arrays.equals(decodedData,requestArray)) {
                       newDataRequested = true;
                       int[] values = {0,3,3,3,3,3,3,3,3,3};
                       sendData=NetUtils.intToByte(values);
                    }
                    else {
                        int[] values = {1,2,3,4,5,6,7,8};
                        sendData=NetUtils.intToByte(values);
                        
                       
                    }
                    
                    
                    responsePacket = new DatagramPacket(sendData,sendData.length,address,port);
                    socket.send(responsePacket);
                    System.out.println("Packet Sent");
                }
                catch(IOException e){
                    e.printStackTrace();
                }
              
            }
        }
    }
    
    public static boolean newDataRequested() {
       if (newDataRequested) {
          newDataRequested = false;
          return true;
       }
       else {
          return false;
       }
    }

}