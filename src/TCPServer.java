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

            while(isOpen){
                Socket Tsocket = serverSock.accept();
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
        private BufferedReader reader;

        ClientHandler(Socket s, PrintWriter writer) throws IOException{
            this.s = s;
            this.writer = writer;
            reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }

        @Override
        public void run(){
            String message;
            try {
                while (((message = reader.readLine()) != null)) {
                    if(DEBUG) System.out.println(message);
                    String ret = processCommand(message);
                    writer.println(ret);
                    writer.flush();

                    if(s.isClosed()){
                        break;
                    }
                }
                if(DEBUG){System.out.println("TCP disconnect from client: " + s.getPort());}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
