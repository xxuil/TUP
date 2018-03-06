import java.io.*;
import java.net.*;
import java.util.*;

public class TCPServer extends BookServer implements Runnable {
    private int tcpPort = 7000;
    @Override
    public void run(){
        try{
            @SuppressWarnings("resource")
            ServerSocket serverSock = new ServerSocket(tcpPort);
            if(DEBUG){System.out.println("TCP established");}
            Socket Tsocket;
            while((Tsocket = serverSock.accept()) != null){
                int port = Tsocket.getPort();
                if(DEBUG){System.out.println("TCP connected from client: " + port);}

                PrintWriter writer = new PrintWriter(Tsocket.getOutputStream());
                ClientHandler client = new ClientHandler(Tsocket, writer);
                Thread t = new Thread(client);
                t.start();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable{
        private Socket s;
        private PrintWriter writer;
        private Scanner reader;
        private File invfile;
        private FileOutputStream iout;
        private PrintStream inven1;
        ClientHandler(Socket s, PrintWriter writer) throws IOException{
            File invfile = new File("./src", "inventory.txt");
            FileOutputStream iout = new FileOutputStream(invfile);
            inven1 = new PrintStream(iout);
            this.s = s;
            this.writer = writer;
        }

        @Override
        public void run(){
            try {
                reader = new Scanner(s.getInputStream());
            }catch (IOException e){
                e.printStackTrace();
            }
            String message;
            boolean flag1 = false;
            while (reader.hasNext()) {
                if(DEBUG) System.out.println("Server trying to receive message");
                message = reader.nextLine();
                if(DEBUG) System.out.println("Message from ClientHandler: " + message);
                String ret = processCommand(message);
                if(ret.contains("inventory")){
                    ret = ret.substring(9, ret.length());
                    flag1 = true;
                }
                if(flag1){
                    inven1.println(ret);
                    flag1 = false;
                }

                if(DEBUG) System.out.println("Message output from ClientHandler: " + ret);

                String[] set = ret.split("\n");
                ret = "";

                for(String temp : set){
                    ret = ret + temp + "&&";
                }

                writer.println(ret);
                writer.flush();
                if(DEBUG) System.out.println("Server sent message");

                if(s.isClosed()){
                    break;
                }
            }
            if(DEBUG){System.out.println("TCP disconnect from client: " + s.getPort());}
        }
    }
}
