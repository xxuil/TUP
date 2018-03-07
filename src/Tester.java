import java.util.concurrent.atomic.AtomicInteger;

public class Tester {
    static AtomicInteger ID = new AtomicInteger(1);
    public static void main(String[] args) throws Exception{
        Thread[] t = new Thread[6];

        t[0] = new Thread(new ServerHandler());
        t[1] = new Thread(new ClientHandler());
        t[2] = new Thread(new ClientHandler());
        t[3] = new Thread(new ClientHandler());
        t[4] = new Thread(new ClientHandler());
        t[5] = new Thread(new ClientHandler());

        t[0].start();
        t[1].start();
        t[2].start();
        t[3].start();
        t[4].start();
        t[5].start();

    }

    static class ServerHandler implements Runnable{
        @Override
        public void run() {
            try{
                String[] sinput = {"./src/input.txt"};
                BookServer.main(sinput);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    static class ClientHandler implements Runnable{
        @Override
        public void run() {
            try{
                String[] cinput = {"./src/cmdFile" + String.valueOf(ID.intValue()), String.valueOf(ID.intValue())};
                ID.incrementAndGet();
                BookClient.main(cinput);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
