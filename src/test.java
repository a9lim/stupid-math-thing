import java.util.*;
public class test {
    public static void main(String[] args){
        ArrayDeque<Integer> t = new ArrayDeque<>();
        t.push(3);
        t.push(4);
        t.push(5);
        System.out.println(t);
        System.out.println(t.peek());
        System.out.println(t.pop());
        t.clear();
        t.add(3);
        t.add(4);
        t.add(5);
        System.out.println(t);
        System.out.println(t.peek());
        System.out.println(t.remove());

    }
}
