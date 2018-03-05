import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread {
    private BookStorage storage;
    private Socket s;
    public ServerThread(BookStorage storage, Socket s){
        this.storage = storage;
        this.s = s;
    }
}
