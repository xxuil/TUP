import java.io.*;
import java.net.*;
import java.util.*;

public class Server{
    private final static boolean DEBUG = true;

    private static BookStorage storage;
    public boolean isOpen = true;
    private int udpPort;
    private int byteLength = 1024;
    private int clientPort;

    private static ArrayList<PrintWriter> oos = new ArrayList<>();
    private static ArrayList<String> clientNameList = new ArrayList<>();
    private static HashMap<String, Client> userMap = new HashMap<>();

    private Server(){
    }

}
