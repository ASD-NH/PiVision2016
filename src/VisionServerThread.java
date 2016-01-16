import java.io.*;
import java.net.*;
import java.util.*;

public class VisionServerThread extends Thread
{
    protected DatagramSocket socket = null;
    private boolean running = true;
    public int port = 00000;
    public InetAddress address = null;
            
    public VisionServerThread() throws IOException{
        this("VisionSeverThread");
    }
    public VisionServerThread(String name) throws IOException{
        super(name);
        socket = new DatagramSocket(31415);
    }
    public void run(){
        if (socket == null){
            return;
        }
        else {
            byte[] buf = new byte[1024];
            while(running){
                try{
                    
                    
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