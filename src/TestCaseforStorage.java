import java.util.Map;

public class TestCaseforStorage {
    public static void main(String args[]){
        Map<String, Integer> inventory = BookServer.inven();
        BookStorage storage = new BookStorage(inventory);

    }
}
