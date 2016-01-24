import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;

public class VisionServerThread extends Thread
{
    public static DatagramSocket socket = null;
    private boolean running = true;
    public static int port = 00000;
    public static InetAddress address = null;
    DatagramPacket receivePacket;
    DatagramPacket responsePacket;
    byte[] receivedData; 
    byte[] sendData;
    String decodedData;
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
                    
                    decodedData = new String(receivedData,"UTF-8");
                    System.out.println("Packet Received from: " + address + " on port: " + port + " = " + decodedData + " length of "+ receivePacket.getLength());
                    
                    sendData = new byte[1024];
                    
                    if (decodedData.equals("start")){
                        int[] values ={0,0,0,0,0,0,0,0};
                        sendData=intToByte(values);
                    }
                    else {
                        int[] values = {1,2,3,4,5,6,7,8};
                        sendData=intToByte(values);
                        
                       
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
    public int getPort(){
        return port;
    }
    public DatagramSocket getSocket(){
        return socket;
    }
    public InetAddress getAddress(){
        return address;
    }
    public byte[] intToByte(int[] values){
        int valueCount=0;
        byte[] bytes = new byte[(values.length*2)];
        for (int i=0;i < bytes.length;i++){
            if (i%2==0){
                bytes[i]=(byte)(values[valueCount] & 0xff);
            }
            else {
                bytes[i]=(byte)(values[valueCount] & 0xff00);
                valueCount++;
            }
        }
        return bytes;
        
    }
}