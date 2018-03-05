import java.io.*;
import java.net.*;
import java.util.*;

public class BookServer{
    private final static boolean DEBUG = true;
    private static BookStorage storage;
    private int udpPort;
    private int byteLength;
    private static boolean isOpen;
    private static ArrayList<PrintWriter> oos = new ArrayList<>();
    private static ArrayList<String> clientNameList = new ArrayList<>();
    private static HashMap<String, Client> userMap = new HashMap<>();

    public static void main (String[] args) throws Exception{
        parseInput(args);
        BookServer UDP = new BookServer();
        UDP.setupUPD();
    }

    private static void parseInput(String[] args){
        if (args.length != 1) {
            System.out.println("ERROR: No Argument");
            System.exit(-1);
        }

        Map<String, Integer> inventory = new HashMap<>();
        String fileName = args[0];
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
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }

        storage = new BookStorage(inventory);
    }

    public BookServer(){
        udpPort = 8000;
        byteLength = 1024;
        isOpen = true;
    }

    private void setupUPD() throws Exception{
        String send = "UServer";
        byte[] buf = new byte[byteLength];
        DatagramSocket socket = new DatagramSocket(udpPort);
        DatagramPacket dpget = new DatagramPacket(buf, byteLength);

        if(DEBUG){System.out.println("UDP Server On");}

        while(isOpen){
            socket.receive(dpget);
            if(DEBUG){System.out.println("Server receive: ");}
            String receive = new String(dpget.getData(), 0, dpget.getLength());
            int port = dpget.getPort();
            if(DEBUG){System.out.println(receive + " from " + dpget.getAddress().getHostAddress() + ": "
                    + dpget.getPort());}

            send = processCommand(receive);

            if(DEBUG){System.out.println("Server send: " + send);}

            DatagramPacket dpsend = new DatagramPacket(send.getBytes(), send.length(),
                    dpget.getAddress(), dpget.getPort());
            socket.send(dpsend);
            dpget.setLength(byteLength);

        }
        socket.close();
    }

    private static String processCommand(String command){
        String message = "";
        String[] parse = command.split(" ");
        if(parse[0].equals("setmode")) {
            if(parse[1].equals("U"))
                return "U";
            else if(parse[1].equals("T")){
                Thread TCP = new Thread(new TCPHandler());
                TCP.start();
                return "T";
            }
        }
        else if(parse[0].equals("borrow")){
            String name = parse[1];
            String book = "";

            for(int i = 2; i < parse.length; i++){
                book = book + parse[i] + " ";
            }

            int result = storage.borrow(name, book);
            if(result != -1 && result != -2){
                message = "You request has been approved " + result + " " + name + " " + book;
            }else if(result == -1){
                message = "Request Failed - Book not available";
            }else{
                message = "Request Failed - We do not have this book";
            }
            return message;
        }

        else if(parse[0].equals("return")){
            int id = Integer.parseInt(parse[1]);
            boolean result = storage.return_1(id);
            if(result){
                message = id + " is returned";
            }else{
                message = id + " not found, no such borrow record";
            }
            return message;
        }

        else if(parse[0].equals("list")){
            String name = parse[1];
            Map<Integer, String> result = storage.list(name);
            if(storage.list(name) == null){
                message = "No record found for " + name;
            }else{
                Set<Integer> get = result.keySet();
                for(Integer o : get){
                    String val = result.get(o);
                    message = message + o + " " + val +"\n";
                }
            }
            int i = message.lastIndexOf("\n");
            message = message.substring(0, i);
            return message;
        }

        else if(parse[0].equals("inventory")){
            Map<String, Integer> quantity = storage.inventory();
            Set<String> result = quantity.keySet();
            for(String good : result){
                int val = quantity.get(good);
                message = message + good + " " + val +"\n";
            }
            int i = message.lastIndexOf("\n");
            message = message.substring(0, i);
            return message;
        }
        else{
            if(DEBUG){System.out.println("ERROR");}
            return "";
        }
        return "";
    }

    static class TCPHandler implements Runnable{
        private int tcpPort = 7000;
        @Override
        public void run(){
            try{
                @SuppressWarnings("resource")
                ServerSocket serverSock = new ServerSocket(tcpPort);
                if(DEBUG){System.out.println("TCP established");}

                while(isOpen){
                    Socket Tsocket = serverSock.accept();

                    ServerThread client = new ServerThread(storage, Tsocket);
                    Thread t = new Thread(client);
                    t.start();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
