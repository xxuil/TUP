import java.io.*;
import java.net.*;
import java.util.*;

public class BookClient {
    private final static boolean DEBUG = true;
    static String mode = "UDP";
    private static InetAddress ia;
    static DatagramSocket Usocket;

    private ArrayList<String> commands;

    private int clientId;
    private int tcpPort;
    private int udpPort;
    byte[] buff = new byte[1024];
    private DatagramPacket dpsend;
    private DatagramPacket dpget;

    private String hostAddress;
    private BufferedReader reader;
    private PrintWriter writer;
    private PrintStream write;

    public static void main (String[] args) throws Exception{
        BookClient client = new BookClient();
        client.parseCommand(args);
        client.udpConnect();
        client.processCommand();

    }

    public BookClient(){
        commands = new ArrayList<>();
        clientId = -1;
        tcpPort = 7000;
        udpPort = 8000;
        hostAddress = "localhost";
    }

    private void tcpConnect(){
        try{
            @SuppressWarnings("resource")
            Socket sock = new Socket(hostAddress, tcpPort);
            InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
            reader = new BufferedReader(streamReader);
            writer = new PrintWriter(sock.getOutputStream());
            System.out.println("TCP networking established");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void tcpStart(){
        Thread readerThread = new Thread(new TCPHandler());
        readerThread.start();
    }

    private void udpConnect() throws Exception{
        String str_send = "hello";
        ia = InetAddress.getByName(hostAddress);
        Usocket = new DatagramSocket();
        dpsend = new DatagramPacket(str_send.getBytes(), str_send.length(), ia, udpPort);
        dpget = new DatagramPacket(buff, 1024);
        Usocket.send(dpsend);
    }

    private void udpDisconect() throws Exception{
        Usocket.disconnect();
    }

    private void parseCommand(String[] args) throws Exception{
        if (args.length != 2) {
            System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
            System.out.println("\t(1) <command-file>: file with commands to the server");
            System.out.println("\t(2) client id: an integer between 1..9");
            System.exit(-1);
        }

        String commandFile = args[0];
        clientId = Integer.parseInt(args[1]);

        Scanner sc = new Scanner(new FileReader(commandFile));
        write = new PrintStream("out_" + clientId + ".txt", "UTF-8");
        while(sc.hasNextLine()) {
            String cmd = sc.nextLine();
            commands.add(cmd);
        }
    }

    private void processCommand() throws Exception{
        for(int i = 0; i < commands.size(); i++) {
            String cmd = commands.get(i);
            String[] tokens = cmd.split(" ");

            if (tokens[0].equals("setmode")) {
                if(tokens[1].equals("T")){
                    if(mode.equals("UDP"))
                        mode = "UtoT";
                    else mode = "TCP";
                } else {
                    if(mode.equals("TDP"))
                        mode = "TtoU";
                    else mode = "UDP";
                }
                commands.remove(i);
            }
            else if (tokens[0].equals("exit")) {

                commands.remove(i);
                return;
            }

            else if((!tokens[0].equals("borrow") && (!tokens[0].equals("return")) && (!tokens[0].equals("list")) && (!tokens[0].equals("inventory")))){
                System.out.println("ERROR: No such command");
            }

            String ret = "";
            if(mode.equals("UDP") || mode.equals("UtoT")) {
                ret = sendCommand(cmd);
            }

            else if(mode.equals("TCP") || mode.equals("TtoU")) {
                ret = sendCommand(cmd);
            }

            if(mode.equals("UtoT") || mode.equals("TtoU")){
                break;
            }
        }

        if(mode.equals("UtoT")){
            tcpStart();
            mode = "TCP";
        }
        else if(mode.equals("TtoU")){
            mode = "TCP";
        }
        else {
            System.setOut(write);
        }
    }

    private String sendCommand(String cmd) throws Exception {
        String response = "";

        if(mode.equals("UDP") || mode.equals("UtoT")) {
            byte[] send = new byte[cmd.length()];
            send = cmd.getBytes();
            dpsend.setData(send);
            boolean received = false;

            while(!received){
                Usocket.send(dpsend);
                Usocket.receive(dpget);
                response = new String(dpget.getData(), 0, dpget.getLength());
                if(DEBUG){System.out.println(response);}

                if(mode.equals("UtoT") && response.equals("T")){
                    tcpConnect();
                    udpDisconect();

                } else if(mode.equals("UDP") && !response.equals("U")){
                    write.println(response);
                }

                dpget.setLength(1024);
                received = true;
            }
        }

        else if(mode.equals("TDP")) {
        }

        return response;
    }

    class TCPHandler implements Runnable {

        TCPHandler() {
        }

        @Override
        public void run() {
            try {
                processCommand();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
