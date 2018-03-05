import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    private final static boolean DEBUG = true;

    public static void main(String[] args) throws IOException{
        byte[] buf = new byte[1024];
        DatagramSocket socket = new DatagramSocket(3000);
        DatagramPacket dp_receive = new DatagramPacket(buf, 1024);

        if(DEBUG){System.out.print("Server is on");}

        while(true){
            
        }
    }
}
