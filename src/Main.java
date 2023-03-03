import java.util.*;
import java.io.*;
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner in = new Scanner(new FileReader("grunk.txt"));
//        new File("out.txt").createNewFile();
//        PrintWriter out = new PrintWriter(new FileWriter("out.txt"));
//
//        System.out.println("Hello world!");

//        String[] lines = new String[];

//        int ii = 0;
        LinkedList<String> lines = new LinkedList<>();
        while (in.hasNextLine()){
            lines.add(in.nextLine());
        }
        Inkatink i = new Inkatink(lines.toArray(new String[0]));

        i.computeLines();

//        i.parse("( 3 < 4 ) + 1");
//        i.parse("3 + 4 * 5");
//        i.parse("3 * ( 4 + 5 )");
//        i.parse("( 33 + -43 ) * ( -55 + 65 )");  //parentheses, negatives
//        i.parse("3 * 4 + 5 / 2 - 5");
//        i.parse("8 + 1 * 2 - 9 / 3");
//        i.parse("3 * ( 4 * 5 + 6 )");
//        i.parse("3 + ( 4 - 5 - 6 * 2 )");
//        i.parse("2 + 7 % 3");
//        i.parse("( 2 + 7 ) % 3");
//
//
//        //power and factorial do evaluate, but only left-to-right.  Do precedence in Infix_Extension
//        i.parse("( 1 + 1 ) ^ 3");   //power
//        i.parse("2 ^ 3 + 3");
//        i.parse("3 * 2 ^ 3");
//        i.parse("( 1 + 3 ) !");
//        i.parse("1 + 3 !");          //factorial
//        i.parse("1 * 3 !");
//        String test = "7 4 6 + 9 * +";
//        i.parse(test);
//        i.parse("3 4 5 * +");
//        i.parse("3 4 * 5 +");
//        i.parse("33 -43 + -55 65 + *");
//        i.parse("3 4 * 5 2 / + 5 -");
//        i.parse("8 1 2 * + 9 3 / -");
//        i.parse("3 4 5 * 6 + *");
//        i.parse("3 4 5 - 6 2 * - +");
//
//        /*  Improvements    % ^ !     */
//        i.parse("2 7 3 % +");  //modulus
//        i.parse("2 7 + 3 %");
    }
}