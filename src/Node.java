
import java.util.Random;

/**
 * @author Yilong Luan
 *
 *
 */

public class Node {
    public Interval interv;
    public int iMax;
    public int priority;
    public Node parent;
    public Node left;
    public Node right;
    public int height;

    public Node(Interval i) {
        interv = i;
        priority = new Random().nextInt(Integer.MAX_VALUE);
        height = 0;
        iMax = i.high;
    }

    public Node getParent() {
        return parent;
    }

    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }

    public Interval getInterv() {
        return interv;
    }

    public int getIMax() {
        return iMax; 
    }

    public  int getPriority() {
        return priority;
    }
}
