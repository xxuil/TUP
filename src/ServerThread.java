import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread extends Thread{
    private BookStorage storage;
    private Socket s;
    public ServerThread(BookStorage storage, Socket s){
        this.storage = storage;
        this.s = s;
    }
    @Override
    public void run(){
        try{
            Scanner sc = new Scanner(s.getInputStream());
            PrintWriter write = new PrintWriter(s.getOutputStream());
            String input = sc.nextLine();
            Scanner sc1 = new Scanner(input);
            String tag = sc1.next();
            if(tag.equals("borrow")) {
                String name = sc1.next();
                String book = sc1.next();
                if(storage.borrow(name, book)){
                    write.println("Success");
                }else{
                    write.println("Failed");
                }
            }else if(tag.equals("return_1")) {

            }else if(tag.equals("list")){

            }else if(tag.equals("inventory")){

            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
