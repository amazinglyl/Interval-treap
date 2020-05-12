
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yilong Luan
 *
 *
 */

public class IntervalTreap {
    public Node root;
    public int size;

    public IntervalTreap() {};

    public Node getRoot() {
        return root;
    }

    public int getSize() {
        return size;
    }

    public int getHeight() {
        return root == null? 0: root.height;
    }

    // interval insertion, add a node z into the interval treap
    public void intervalInsert(Node z) {
        size++;
        Node pre = null;
        Node cur = root;
        z.left = null;
        z.right = null;
        z.parent = null;

        // step 1, insert like BST 
        while (cur!=null) {
            pre = cur;
            cur.iMax = Math.max(cur.iMax,z.iMax);
            if (z.interv.low < cur.interv.low)
                cur = cur.left;
            else
                cur = cur.right;
        }
        if (pre == null) {
            root = z;
        }
        else {
            z.parent = pre;
            if (z.interv.low < pre.interv.low)
                pre.left = z;
            else
                pre.right = z;
        }

        // step 2, rotate and update
        while (pre != null && pre.priority > z.priority) {
            if (z == pre.left) {
                rightRotate(pre);
            }
            else {
                leftRotate(pre);
            }
            updateHeight(pre);
            updateHeight(pre.parent);
            pre = z.parent;
        }
        
        // update height of rest nodes upward 
        while (pre != null) {
            updateHeight(pre);
            pre = pre.parent;
        }
    }

    // interval deletion, delete a node z from the interval treap
    public void intervalDelete(Node z) {
        size--;
        Node temp = null;
        Node y = null;

        // step 1, delete a node like BST
        if (z.left == null) {
            temp = z.parent;
            transplant(z, z.right);
        }
        else if (z.right == null) {
            temp = z.parent;
            transplant(z, z.left);
        }
        else {
            y = minimum(z.right);
            temp = y.parent == z? y: y.parent;
            if(y.parent != z){
                transplant(y, y.right);
                y.right = z.right;
                y.right.parent = y;
            }
            transplant(z,y);
            y.left = z.left;
            y.left.parent = y;
        }
        
        // temp, the lowest point where the treap has changed
        // update iMax from temp to root before rotation
        while (temp != null) {
            updateHeight(temp);
            updateImax(temp);
            temp = temp.parent;
        }

        // rotate for case 3b of BST and update corresponding iMax and height 
        if (y != null){
            while ((y.left != null && y.left.priority < y.priority) || (y.right != null && y.right.priority < y.priority)) {
                Node min = y;
                
                // find minimum value among y, y.left and y.right, reverse rotate
                if (y.left != null && y.left.priority < min.priority)
                    min = y.left;
                if (y.right != null && y.right.priority < min.priority)
                    min = y.right;
                if (min == y .left)
                    rightRotate(y);
                else
                    leftRotate(y);
            }      
            while (y != null){
                updateHeight(y);
                y = y.parent;
            }
        }
    }
    
    //interval search, find an interval in the treap that overlaps interval i
    public Node intervalSearch(Interval i) {
        Node cur = root;
        while (cur != null && (i.low > cur.interv.high || cur.interv.low > i.high)) {
            if (cur.left != null && cur.left.iMax >= i.low)
                cur = cur.left;
            else
                cur = cur.right;
        }
        return cur;
    }
    
    //interval search exactly, find an interval that is same to interval i
    public Node intervalSearchExactly(Interval i) {
        Node cur = root;
        List<Node> list = new ArrayList<>();

        while (cur != null) {
            if (cur.iMax < i.high)
            	cur = null;
            else if (cur.interv.low < i.low)
            	   cur = cur.right;
            else if (cur.interv.low > i.low)
                cur = cur.left;
            // if cur.inter.low = i.low, check possible duplicate nodes    
            else     
            	{checkDuplicate(cur, i, list); 
            	cur = null;
            	}
            }
        if (list.isEmpty() != true) cur = list.get(0); 
        return cur;
        }

    //when we reach a node that has the same low value, check its left and right subtrees to find next duplicate node, then recursive 
    public void checkDuplicate(Node x, Interval i, List<Node> list) {
		Node y = null;	
		
		//if find the same interval, return this node
		if (x.interv.high == i.high) {
	        list.add(x);
	        return;
		}
		
		//left subtree, the next duplicate node could only be the first right child that has no right child unless if other nodes are different
		if (x.left != null && x.left.iMax >= i.high) {
			y = x.left;
			while (y != null && y.interv.low != i.low) {
				y = y.right;
			}
			if (y != null && y.interv.low == i.low) {
				checkDuplicate(y, i, list);
			}
		}
			
		//right subtree, the next duplicate node could only be the first left child that has no left child if other nodes are different
		if (x.right != null && x.right.iMax >= i.high) {
			y = x.right;
			while (y != null && y.interv.low != i.low) {
				y = y.left;
			}
			if (y != null && y.interv.low == i.low) {
				checkDuplicate(y, i, list);
			}
		}
	}
    
    //interval overlap, return all intervals in treap overlap to the intervals i 
	public List<Interval> overlappingIntervals(Interval i){
        List<Interval> list = new ArrayList<>();
        findOverlap(i,root,list);
        return list;
    }

    public void findOverlap(Interval i, Node cur, List<Interval> list){
        if (i.low <= cur.interv.high && cur.interv.low <= i.high)
            list.add(cur.interv);
        if (cur.left != null && cur.left.iMax >= i.low)
            findOverlap(i, cur.left, list);
        if (cur.right != null && (cur.interv.low <= i.high && cur.right.iMax >= i.low))
            findOverlap(i, cur.right,  list);
    }

    //some auxiliary methods
    public void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;
        if (y.left != null)
            y.left.parent = x;
        y.parent = x.parent;
        if (x.parent == null)
            root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
            x.parent.right = y;
        y.left = x;
        x.parent = y;
        y.iMax = x.iMax;
        updateImax(x);
    }

    public void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;
        if (y.right != null)
            y.right.parent = x;
        y.parent = x.parent;
        if (x.parent == null)
            root = y;
        else if (x == x.parent.left)
            x.parent.left = y;
        else
            x.parent.right = y;
        y.right = x;
        x.parent = y;
        y.iMax = x.iMax;
        updateImax(x);
    }

    public void updateImax(Node x) {
        int max = x.interv.high;
        if (x.left != null)
            max = Math.max(max, x.left.iMax);
        if (x.right != null)
            max = Math.max(max, x.right.iMax);
        x.iMax = max;
    }

    public void updateHeight(Node x) {
        int h = 0;
        if (x.left != null)
            h = Math.max(h, x.left.height+1);
        if (x.right != null)
            h = Math.max(h, x.right.height+1);
        x.height = h;
    }

    public void transplant(Node u, Node v) {
        if (u.parent == null)
            root = v;
        else if (u == u.parent.left)
            u.parent.left = v;
        else
            u.parent.right = v;
        if (v != null)
            v.parent = u.parent;
    }

    public Node minimum(Node x) {
        while (x.left != null) {
            x = x.left;
        }
        return x;
    }
}
