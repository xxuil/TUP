import java.io.*;
import java.net.*;
import java.util.Scanner;

public class BookClient {
    static Socket s;
    static InputStream instream;
    static OutputStream outstream;
    static Scanner in;
    static PrintWriter out;
    static byte[] rbuf = new byte[1024];
    static DatagramPacket sPacket, rPacket;
    static String mode = "UDP";
    static InetAddress ia;
    static DatagramSocket datasocket;
    public static void main (String[] args) {
        String hostAddress;
        int tcpPort;
        int udpPort;
        int clientId;

        if (args.length != 2) {
            System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
            System.out.println("\t(1) <command-file>: file with commands to the server");
            System.out.println("\t(2) client id: an integer between 1..9");
            System.exit(-1);
        }

        String commandFile = args[0];
        clientId = Integer.parseInt(args[1]);
        hostAddress = "localhost";
        tcpPort = 7000;// hardcoded -- must match the server's tcp port
        udpPort = 8000;// hardcoded -- must match the server's udp port
        try {
            ia = InetAddress.getByName(hostAddress);
            datasocket = new DatagramSocket();
        }catch(UnknownHostException e){
            e.printStackTrace();
        }catch(SocketException a){
            a.printStackTrace();
        }
        PrintWriter write;
        try {
            Scanner sc = new Scanner(new FileReader(commandFile));
            write = new PrintWriter("out_" + clientId + ".txt", "UTF-8");

            while(sc.hasNextLine()) {
                String cmd = sc.nextLine();
                String[] tokens = cmd.split(" ");
                if (tokens[0].equals("setmode")) {
                    if(tokens[1].equals("T")){
                        try {
                            s = new Socket(hostAddress, tcpPort);
                            instream = s.getInputStream();
                            outstream = s.getOutputStream();
                            in = new Scanner(instream);
                            out = new PrintWriter(outstream);
                            mode = "TCP";
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }else {

                        mode = "UDP";
                    }
                    // TODO: set the mode of communication for sending commands to the server
                }else if (tokens[0].equals("exit")) {
                    try {
                        s.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    // TODO: send appropriate command to the server
                } else if ((!tokens[0].equals("borrow") && (!tokens[0].equals("return")) && (!tokens[0].equals("list")) && (!tokens[0].equals("inventory")))){
                    System.out.println("ERROR: No such command");
                }
                String response = "";
                if(mode.equals("TCP")) {
                    out.println(cmd);
                    out.flush();
                    response = in.nextLine(); //make a text file and put this string
                }else {
                    byte[] buf = new byte[cmd.length()];
                    buf = cmd.getBytes();
                    sPacket = new DatagramPacket(buf, buf.length, ia, udpPort);
                    try {
                        datasocket.send(sPacket);
                        rPacket = new DatagramPacket(rbuf, rbuf.length);
                        datasocket.receive(rPacket);
                        response = new String(rPacket.getData(), 0, rPacket.getLength());  //make a text file and put this string
                    } catch (IOException a) {
                        a.printStackTrace();
                    }
                }
                write.println(response);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }
}
