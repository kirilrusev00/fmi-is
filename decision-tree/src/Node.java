import java.util.ArrayList;
import java.util.HashSet;

public class Node {
    boolean isRecurrence;
    boolean isLeaf = false;
    int attribute;
    String value;
    Node parent = null;
    ArrayList<Node> children;
    HashSet<Integer> prevAttributes;

    Node() {
        children = new ArrayList<>();
        prevAttributes = new HashSet<>();
    }
}
