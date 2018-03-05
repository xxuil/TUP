import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Server {
    private final static boolean DEBUG = true;
    private boolean isOpen = true;

    public static void main(String[] args){
        try {
            new Server().setupNetwork();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupNetwork() throws Exception{
        setupUPD();
    }

    private void setupUPD() throws Exception{
        String send = "Hello Client";
        byte[] buf = new byte[1024];
        DatagramSocket socket = new DatagramSocket(3000);
        DatagramPacket dpget = new DatagramPacket(buf, 1024);

        if(DEBUG){System.out.println("Server is on");}

        while(isOpen){
            socket.receive(dpget);
            if(DEBUG){System.out.println("Server received data from client: ");}
            String receive = new String(dpget.getData(), 0, dpget.getLength());
            if(DEBUG){System.out.println(receive + " from " + dpget.getAddress().getHostAddress() + ": "
                    + dpget.getPort());}

            DatagramPacket dpsend = new DatagramPacket(send.getBytes(), send.length(), dpget.getAddress(), 9000);
            socket.send(dpsend);
            dpget.setLength(1024);

            isOpen = false;
        }
        socket.close();
    }
}
