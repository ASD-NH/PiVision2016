import java.io.*;
import java.net.*;
import java.util.*;

public class VisionServerThread extends Thread
{
    protected DatagramSocket socket = null;
    private boolean running = true;
    public int port = 00000;
    public InetAddress address = null;
    
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
            if (currSocket - baseSocket > 10) {
                System.out.println("[ERROR] Open socket could not be found, aborting thread");
                break;
            }
            try {
                errorFree = true;
                socket = new DatagramSocket(currSocket);
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
                    
                    //99% sure this is extraneous code, delete at leisure
                    //(socket is already initialized in the constructor)
                    //socket = new DatagramSocket(31415);
                    
                    DatagramPacket packet = new DatagramPacket(buf,buf.length);
                    socket.receive(packet);
                    
                    address = packet.getAddress();
                    port = packet.getPort();
                    byte[] receivedPacket =  new byte[1024];
                    receivedPacket = packet.getData();
                    
                    if (Arrays.toString(receivedPacket) == "start"){
                        String response = "Hello world";
                        buf = response.getBytes();
                    }
                    else {
                        String response = "Hello world";
                        buf = response.getBytes();
                    }
                    
                    DatagramPacket responsePacket = new DatagramPacket(buf,buf.length,address,port);
                    socket.send(responsePacket);
                }
                catch(IOException e){
                    e.printStackTrace();
                }
                finally{
                    socket.close();
                }
            }
        }
    }
}