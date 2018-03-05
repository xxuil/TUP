import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class BookServer{
    static Map<String, Integer> inventory = new HashMap<>();
    static DatagramPacket rpacket, spacket;
    public static BookStorage storage;
    public static void main (String[] args) {
        int tcpPort;
        int udpPort;
        if (args.length != 1) {
            System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
            System.exit(-1);
        }
        String fileName = args[0];
        tcpPort = 7000;
        udpPort = 8000;
        String book = "";
        try{
            Scanner sc = new Scanner(new FileReader(fileName));
            while(sc.hasNext()){
                if(sc.hasNextInt()){
                    int num = sc.nextInt();
                    inventory.put(book, num);
                    book = "";
                }else {
                    book = book.concat(sc.next());
                    book = book + " ";
                }
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        try{
            DatagramSocket datasocket = new DatagramSocket(udpPort);
            byte[] buf = new byte[1000];
            while(true){
                spacket = new DatagramPacket(buf, buf.length);
                datasocket.receive(spacket);
                byte[] temp = spacket.getData();
            }
        }catch(IOException e){
            e.printStackTrace();
        }



        storage = new BookStorage(inventory);
        try {
            ServerSocket server = new ServerSocket(tcpPort);
            Socket s;
            while ((s = server.accept()) != null) {
                Thread t = new ServerThread(storage, s);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        // TODO: handle request from clients
    }
}
