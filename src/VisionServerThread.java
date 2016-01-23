import java.io.*;
import java.net.*;
import java.util.*;

public class VisionServerThread extends Thread
{
    public static DatagramSocket socket = null;
    private boolean running = true;
    public static int port = 00000;
    public static InetAddress address = null;
    DatagramPacket receivePacket;
    byte[] receivedData = new byte[1024]; 
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
                System.out.println("[WARNING] Socket already in use, trying socket " + currSocket);
            }
        }
        
    }
  
    public void run(){
        if (socket == null){
            return;
        }
        else {
            byte[] buf = new byte[1024];
            while(running){
                try{
                    System.out.println("[INFO] Waiting to Receive Packet");
                    receivePacket = new DatagramPacket(buf,buf.length);
                    socket.receive(receivePacket);
                    
                    
                    
                    address = receivePacket.getAddress();
                    port = receivePacket.getPort();
                    receivedData = receivePacket.getData();
                    decodedData = new String(receivedData,"UTF-8");
                    System.out.println("Packet Received = " + decodedData);
                    
                    if (decodedData == "start"){
                        String response = "Hello world";
                        buf = response.getBytes();
                    }
                    else {
                        String response = "Hello world";
                        buf = response.getBytes();
                    }
                    
                    DatagramPacket responsePacket = new DatagramPacket(buf,buf.length,address,port);
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
}