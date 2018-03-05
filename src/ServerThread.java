import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class ServerThread implements Runnable{
    private BookStorage storage;
    private Socket s;
    public ServerThread(BookStorage storage, Socket s){
        this.storage = storage;
        this.s = s;
    }
    @Override
    public void run(){
        Scanner sc = null;
        try{
            sc = new Scanner(s.getInputStream());
            PrintWriter write = new PrintWriter(s.getOutputStream());
            String input = sc.nextLine();
            Scanner sc1 = new Scanner(input);
            String tag = sc1.next();
            if(tag.equals("borrow")) {
                String name = sc1.next();
                String book = sc1.next();
                int result = storage.borrow(name, book);
                if(result != -1 && result != -2){
                    write.println("You request has been approved " + result + " " + name + " " + book);
                }else if(result == -1){
                    write.println("Request Failed - Book not available");
                }else{
                    write.println("Request Failed - We do not have this book");
                }
            }else if(tag.equals("return")) {
                int id = sc1.nextInt();
                boolean result = storage.return_1(id);
                if(result){
                    write.println(id + " is returned");
                }else{
                    write.println(id + " not found, no such borrow record");
                }
            }else if(tag.equals("list")){
                String name = sc1.next();
                String send = "";
                Map<Integer, String> result = storage.list(name);
                if(storage.list(name) == null){
                    write.println("No record found for " + name);
                }else{
                    Set<Integer> get = result.keySet();
                    for(Integer o : get){
                        String val = result.get(o);
                        send = send + o + " " + val + "\n";
                    }
                    write.println(send);
                }
            }else if(tag.equals("inventory")){
                String send = "";
                Map<String, Integer> quantity = storage.inventory();
                Set<String> result = quantity.keySet();
                for(String good : result){
                    int val = quantity.get(good);
                    send = send + good + " " + val + "\n";
                }
                write.println(send);
            }
            write.flush();
            s.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            sc.close();
        }
    }
}
