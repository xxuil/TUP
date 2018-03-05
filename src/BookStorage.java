import java.util.HashMap;
import java.util.Map;

public class BookStorage {
    private Map<String, Integer> inventory;
    private Map<String, String> students;
    public BookStorage(Map<String, Integer> inventory){
        this.inventory = inventory;
        students = new HashMap<>();
    }
    public synchronized void borrow(String stdname, String bookname){

    }
    public synchronized void return_1(int i){

    }
    public synchronized void list(String stdname){

    }
    public synchronized void inventory(){

    }
}
