import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
    private static final int TIMEOUT = 5000;
    private static final int MAXNUM = 5;

    public static void main(String args[])throws Exception{
        String str_send = "Hello Server";
        byte[] buf = new byte[1024];
        DatagramSocket socket = new DatagramSocket(9000);
        InetAddress loc = InetAddress.getLocalHost();
        DatagramPacket dpsend = new DatagramPacket(str_send.getBytes(),str_send.length(),loc,8000);
        DatagramPacket dpget = new DatagramPacket(buf, 1024);
        socket.setSoTimeout(TIMEOUT);
        int tries = 0;
        boolean received = false;
        while(!received && (tries < MAXNUM)){
            socket.send(dpsend);

            try{
                socket.receive(dpget);
                if(!dpget.getAddress().equals(loc)){
                    throw new IOException("Received packet from unknown source");
                }
                received = true;
            }catch (InterruptedIOException e){
                tries ++;
                System.out.println("Time out," + (MAXNUM - tries) + " more tries..." );
            }

            if(received){
                System.out.println("Client received data from server: ");
                String str_get = new String(dpget.getData(), 0, dpget.getLength());
                System.out.println(str_get + " from " + dpget.getAddress().getHostAddress() + ": "
                        + dpget.getPort());
                dpget.setLength(1024);
                received = false;
            }
            else{
                System.out.println("No response -- abort");
            }

            //socket.close();
        }
    }
}
