import java.io.*;
import java.net.*;
import java.util.*;

public class BookServer{
    protected final static boolean DEBUG = true;
    protected static BookStorage storage;
    private int udpPort;
    private int byteLength;
    protected static boolean isOpen;
    private PrintStream inven1;
    private static Thread TCP = null;
    private static ArrayList<PrintWriter> oos = new ArrayList<>();
    private static ArrayList<String> clientNameList = new ArrayList<>();
    //private static HashMap<String, Client> userMap = new HashMap<>();

    public static void main (String[] args) throws Exception{
        parseInput(args);
        BookServer UDP = new BookServer();
        UDP.setupUPD();
    }
    public static Map<String, Integer> inven(){
        return storage.inventory();
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
                    book = book.substring(0, book.length() - 1);
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
        File invfile = new File("./src", "inventory.txt");
        FileOutputStream iout = new FileOutputStream(invfile);
        inven1 = new PrintStream(iout);
        boolean flag1 = false;
        if(DEBUG){System.out.println("UDP Server On");}

        while(isOpen){
            socket.receive(dpget);
            String receive = new String(dpget.getData(), 0, dpget.getLength());
            int port = dpget.getPort();

            if(DEBUG){System.out.println("Server receive: " + receive + " from " +
                    dpget.getAddress().getHostAddress() + ": " + dpget.getPort());}

            send = processCommand(receive);
            if(send.contains("inventory")){
                send = send.substring(9, send.length());
                flag1 = true;
            }
            if(flag1){
                inven1.println(send);
                flag1 = false;
            }
            if(!send.equals("")){
                if(DEBUG){System.out.println("Server send: " + send);}

                DatagramPacket dpsend = new DatagramPacket(send.getBytes(), send.length(),
                        dpget.getAddress(), dpget.getPort());
                socket.send(dpsend);
                dpget.setLength(byteLength);
            }
        }
    }

    protected static String processCommand(String command){
        String message = "";
        String[] parse = command.split(" ");

        if(parse[0].equals("hello")){
            if(DEBUG){System.out.println("Initial connection established");}
        }

        else if(parse[0].equals("setmode")) {
            if(parse[1].equals("U"))
                return "U";

            else if(parse[1].equals("T")){
                if (TCP == null) {
                    TCP = new Thread(new TCPServer());
                    TCP.start();
                }
                return "T";
            }
        }
        else if(parse[0].equals("borrow")){
            String name = parse[1];
            String book = "";

            for(int i = 2; i < parse.length; i++){
                book = book + parse[i] + " ";
            }
            book = book.substring(0, book.length()-1);
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
                message = "No record found for " + name + "\n";
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
}
