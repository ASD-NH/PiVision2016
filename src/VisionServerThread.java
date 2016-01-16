import java.io.*;
import java.net.*;
import java.util.*;

public class VisionServerThread extends Thread
{
    protected DatagramSocket socket = null;
    private boolean running = true;
    
    public VisionServerThread() throws IOException{
        this("VisionSeverThread");
    }
    public VisionServerThread(String name) throws IOException{
        super(name);
        socket = new DatagramSocket(1234);
    }
    public void run(){
        if (socket == null){
            return;
        }
        else {
            while(running){
                try{
                    byte[] buf = new byte[1024];
                    
                    DatagramPacket packet = new DatagramPacket(buf,buf.length);
                    socket.receive(packet);
                    InetAddress address = packet.getAddress();
                    int port = packet.getPort();
                    
                    String response = "Hello world";
                    
                    buf = response.getBytes();
                    
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