import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.*;

public class Inkatink {
    public static final String BWORDS = "+-*/%^<>==&|";

    public static final String UWORDS = "~-!";

    public static final String WORDS = BWORDS+UWORDS;
    private int index;
    private final ArrayDeque<Integer> mindx;
    private final ArrayDeque<Object> sts;
    private final HashMap<String, Object> vars;
    private HashMap<String, Object> parms;
    private final HashMap<String, ArrayDeque<String>[]> fun;
    private final HashMap<String, String[][]> sbrts;
    private final HashMap<String, Object[]> arrs;
    private PrintWriter out;
    private Scanner in;


    public static final HashSet<String> special = new HashSet<>(20);

    public static final Scanner uin = new Scanner(System.in);

    public Inkatink() {
        mindx = new ArrayDeque<>();
        fun = new HashMap<>();
        vars = new HashMap<>();
        parms = new HashMap<>();
        sts = new ArrayDeque<>();
        sbrts = new HashMap<>();
        arrs = new HashMap<>();
        // print top of deque, popping
        // print [expression]
        special.add("print");
        // print full deque
        // printall [expression]
        special.add("printall");

        // define one-line function:
        // def [vars] as [function] fed
        special.add("def");
        special.add("as");
        special.add("fed");

        // conditional
        // if [boolean] then [iftrue] else [iffalse] fi
        special.add("if");
        special.add("then");
        special.add("else");
        special.add("fi");

        // create deque
        // deque [name]
        special.add("deque");
        // push to front of deque
        // push [name] [expression]
        special.add("push");
        // pop from front of deque
        // pop [name]
        special.add("pop");
        // add to rear of deque
        // add [name] [expression]
        special.add("add");
        // get size of deque
        // size [name]
        special.add("size");

        // new array
        special.add("arr");
        special.add("rra");
        special.add("get");

        // goto line
        // goto [int]
        special.add("goto");
        // create new variable
        // var [name] [expression]
        special.add("var");
        // set existing variable (local or global)
        // set [name] [expression]
        special.add("set");
        // apply function to variable and set - i.e. upon x + 8 is the same as set x x + 8
        // upon [name] [expression]
        special.add("upon");

        // read user input
        // input
        special.add("input");
        // parse file
        // eval [filename]
        special.add("eval");
        // set output file
        // setout [name]
        special.add("setout");
        // write to file
        // write [expression]
        special.add("write");

        // end program
        // end
        special.add("end");

    }
    public int popInt(){
        return (int)sts.pop();
    }
    public String popStr(){
        return (String)sts.pop();
    }

    public void computeLines(String[] lines){
        index = 0;
        ArrayDeque<String> line;
        LinkedList<String> preeb, sbr;
        while (index < lines.length && index > -1){
            if(!lines[index].startsWith("#")) {
                line = new ArrayDeque<>(List.of(lines[index].split(" ")));
                if("dsr".equals(line.peek())) {
                    line.pop();
                    String name = line.pop();
                    preeb = new LinkedList<>(line);
                    sbr = new LinkedList<>();
                    while (!"rsd".equals(lines[++index]))
                        sbr.add(lines[index]);
                    sbrts.put(name, new String[][]{preeb.toArray(new String[0]), sbr.toArray(new String[0])});
                } else {
                    try {
                        parse(itop(line));
                    } catch (Exception ex) {
                        System.err.println("problem at " + index);
                        ex.printStackTrace();
                    }
                }
            }
            index++;
        }
    }
    public ArrayDeque<String> itop(ArrayDeque<String> s) {
        ArrayDeque<String> o = new ArrayDeque<>();
        ArrayDeque<String> op = new ArrayDeque<>();
        String st;
        while (!s.isEmpty()) {
            st = s.pop();
            if (")".equals(st)) {
                while (!"(".equals(op.peek()))
                    o.add(op.pop());
                op.pop();
            } else if (WORDS.contains(st) || fun.containsKey(st) || sbrts.containsKey(st)) {
                if (!op.isEmpty() && isLower(st, op.peek()))
                    o.add(op.pop());
                op.push(st);
            } else if (special.contains(st)) {
                o.addAll(op);
                op.clear();
                o.add(st);
            } else if ("(".equals(st)) {
                op.push(st);
            } else if ("\"".equals(st)) {
                StringBuilder sb = new StringBuilder();
                while(!"\"".equals(s.peek()))
                    sb.append(s.pop()).append(" ");
                s.pop();
                o.add(sb.toString());
            } else {
                o.add(st);
            }
        }
        o.addAll(op);
        return o;
    }

    public boolean isLower(String c1, String c2) {
        return precedence(c2) < precedence(c1);
    }

    public int precedence(String c) {
        return switch (c) {
            case "=" -> -3;
            case "+", "-" -> 1;
            case "*", "/", "%" -> 2;
            case "^" -> 3;
            case ">", "<", "==" -> 0;
            case "&", "|", "~" -> -1;
            case "(" -> 5;
            default -> 4;
        };
    }

    public void parse(ArrayDeque<String> s) {
        String st;
        while (!s.isEmpty()) {
            st = s.pop();
            if (BWORDS.contains(st)) {
                bineval(popInt(), popInt(), st);
            } else if (UWORDS.contains(st)) {
                uneval(popInt(), st);
            } else if (fun.containsKey(st)) {
                funeval(st);
            } else if (sbrts.containsKey(st)) {
                sbrteval(st);
            } else if (parms.containsKey(st)) {
                sts.push(parms.get(st));
            } else if (vars.containsKey(st)) {
                sts.push(vars.get(st));
            } else if (arrs.containsKey(st)) {
                sts.push(arrs.get(st));
            } else if (special.contains(st)) {
                spevl(st,s);
            } else {
                try {
                    sts.push(Integer.parseInt(st));
                } catch (Exception ex){
                    sts.push(st);
                }
            }
        }
    }

    private void spevl(String s,ArrayDeque<String> st) {
        switch (s){
            case "def" -> {
                String name = st.pop();
                ArrayDeque<String>[] vars = new ArrayDeque[2];
                vars[0] = new ArrayDeque<>();
                vars[1] = new ArrayDeque<>();
                while(!"as".equals(st.peek()))
                    vars[0].push(st.pop());
                st.pop();
                while(!"fed".equals(st.peek()))
                    vars[1].add(st.pop());
                st.pop();
                fun.put(name,vars);
            }
            case "arr" -> {
                String name = st.pop();
                LinkedList<Object> temp = new LinkedList<>();
                while(!"rra".equals(st.peek()))
                    temp.add(st.pop());
                st.pop();
                arrs.put(name, temp.toArray());
            }
            case "get" -> {
                String name = st.pop();
                parse(st);
                sts.push(arrs.get(name)[popInt()]);
            }
            case "var" -> {
                String name = st.pop();
                parse(st);
                vars.put(name,sts.pop());
            }
            case "set" -> {
                String name = st.pop();
                parse(st);
                if(parms.replace(name,sts.peek()) == null)
                    vars.replace(name,sts.peek());
                sts.pop();
            }
            case "upon" -> {
                String name = st.peek();
                parse(st);
                if(parms.replace(name,sts.peek()) == null)
                    vars.replace(name,sts.peek());
                sts.pop();
            }
            case "input" -> {
                System.out.print("> ");
                parse(itop(new ArrayDeque<>(List.of(uin.nextLine().split(" ")))));
            }
            case "eval" -> {
                try {
                    Scanner in = new Scanner(new FileReader(st.pop()));
                    LinkedList<String> lines = new LinkedList<>();
                    while (in.hasNextLine())
                        lines.add(in.nextLine());
                    mindx.push(index);
                    computeLines(lines.toArray(new String[0]));
                    index = mindx.pop();
                } catch (Exception ex) {
                    System.err.println("problem at " + index);
                    ex.printStackTrace();
                }
            }
            case "setout" -> {
                try {
                    out = new PrintWriter(new FileWriter(st.pop()),true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            case "write" -> {
                parse(st);
                out.println(sts.pop());
            }
            case "if" -> {
                ArrayDeque<String> tempst = new ArrayDeque<>();
                while(!"then".equals(st.peek()))
                    tempst.add(st.pop());
                st.pop();

                parse(tempst);
                if(popInt() != 0) {
                    while (!"else".equals(st.peek()))
                        tempst.add(st.pop());
                    st.pop();
                    while (!"fi".equals(st.pop()));
                } else {
                    while (!"else".equals(st.pop()));
                    while (!"fi".equals(st.peek()))
                        tempst.add(st.pop());
                    st.pop();
                }
                parse(tempst);
            }
            case "deque" -> vars.put(st.pop(), new ArrayDeque<>());
            case "push" -> {
                ArrayDeque<Object> temp = (ArrayDeque<Object>) vars.get(st.pop());
                parse(st);
                temp.push(sts.pop());
            }
            case "add" -> {
                ArrayDeque<Object> temp = (ArrayDeque<Object>) vars.get(st.pop());
                parse(st);
                temp.add(sts.pop());
            }
            case "pop" -> sts.push(((ArrayDeque<Object>) vars.get(st.pop())).pop());
            case "peek" -> sts.push(((ArrayDeque<Object>) vars.get(st.pop())).peek());
            case "size" -> sts.push(((ArrayDeque<Object>) vars.get(st.pop())).size());
            case "goto" -> {
                parse(st);
                index = popInt()-2;
            }
            case "print" -> {
                parse(st);
                System.out.println(sts.pop());
            }
            case "printall" -> {
                parse(st);
                System.out.println(sts);
            }
            case "clear" -> sts.clear();
            case "end" -> index = -100;
        }

    }
    public void sbrteval(String s){
        String[][] grum = new String[2][];
        grum[0] = sbrts.get(s)[0];
        grum[1] = sbrts.get(s)[1];

        parms = new HashMap<>();
        for(int i = grum[0].length-1; i > -1; i--)
            parms.put(grum[0][i],sts.pop());
        mindx.push(index);
        computeLines(grum[1]);
        index = mindx.pop();
    }
    public void funeval(String s){
        ArrayDeque<String>[] grum = new ArrayDeque[2];
        grum[0] = new ArrayDeque<>(fun.get(s)[0]);
        grum[1] = new ArrayDeque<>(fun.get(s)[1]);

        parms = new HashMap<>();
        while(!grum[0].isEmpty())
            parms.put(grum[0].pop(),sts.pop());
        parse(grum[1]);
    }
    public void bineval(int a, int b, String s) {
        sts.push( switch (s) {
            case "+" -> b + a;
            case "-" -> b - a;
            case "*" -> b * a;
            case "/" -> b / a;
            case "%" -> b % a;
            case "^" -> exp(b, a);
            case ">" -> b > a ? 1 : 0;
            case "<" -> b < a ? 1 : 0;
            case "=" -> b == a ? 1 : 0;
            case "&" -> b & a;
            case "|" -> b | a;
            default -> 0;
        });
    }
    public void uneval(int a,  String s) {
        sts.push( switch (s) {
            case "!" -> fact(a);
            case "~" -> ~a;
            default -> 0;
        });
    }
    public int exp(int a, int b){
        if(b == 0)
            return 1;
        if(b == 1)
            return a;
        int out = exp(a,b/2);
        out *= out;
        if(b%2==1)
            out *= a;
        return out;
    }
    public int fact(int u){
        if(u < 2)
            return 0;
        int out = 2;
        for(int i = 3; i <= u; i++)
            out*=i;
        return out;
    }
}