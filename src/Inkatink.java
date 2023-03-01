import java.util.*;

public class Inkatink {
    public static final String BWORDS = "+-*/%^<>=&|";

    public static final String UWORDS = "~-!";

    public static final String WORDS = BWORDS+UWORDS;


    private Stack<Integer> sts;
    private HashMap<String, Integer> vars;
    private HashMap<String, String[][]> fun;
    private HashSet<String> special;

    public Inkatink() {
        fun = new HashMap<>();
        vars = new HashMap<>();
        sts = new Stack<>();
        special = new HashSet<>();
//        String[][] sss = {{"A","B","C"},{"A","+","B","-","C"}};
//        fun.put("o",sss);
//
//        special.add("def");

//
//        ops.put("+",1);
//        ops.put("-",2);
//        ops.put("*",3);
//        ops.put("/",4);
//        ops.put("%",5);
//        ops.put("^",6);
//        ops.put("<",7);
//        ops.put(">",8);
//        ops.put("=",9);
//        ops.put("&",10);
//        ops.put("|",11);
    }

    public void compute(String s){
        String[] line = s.split(" ");
        switch (line[0]){
            case "def" -> {
                int index = -1;
                for(int i = 2; index == -1; i++){
                    if(line[i].equals(":")){
                        index = i - 2;
                    }
                }
                if(index == 0){
                    vars.put(line[1], this.parse(itop(line,3)));
                } else {
                    String[][] fundef = new String[2][];
                    fundef[0] = new String[index];
                    fundef[1] = new String[line.length - index - 3];
                    for (int i = 0; i < fundef[0].length; i++) {
                        fundef[0][i] = line[i + 2];
                    }
                    for (int i = 0; i < fundef[1].length; i++) {
                        fundef[1][i] = line[i + index + 3];
                    }
                    fun.put(line[1], fundef);
                }
            }
            case "push" -> sts.push(this.parse(itop(line,1)));
//            case "print" -> System.out.println(sts.pop());
            default -> System.out.println(parse(itop(line)));
        }
        if(!sts.isEmpty())
            vars.put("pop",sts.peek());
    }

    public Stack<String> itop(String[] s, int i) {
        Stack<String> o = new Stack<>();
        Stack<String> op = new Stack<>();
        for (String st : s) {
            if(i > 0) {
                i--;
            } else {
                if (st.equals(")")) {
                    while (op.size() > 0 && !op.peek().equals("("))
                        o.push(op.pop());
                    if (op.size() > 0 && op.peek().equals("("))
                        op.pop();

                } else if (WORDS.contains(st) || fun.containsKey(st)) {
                    if (op.size() > 0)
                        if (isLower(st, op.peek())) {
                            o.push(op.pop());
                        }
                    op.push(st);
                } else {
                    if (!st.equals("(")) {
                        o.push(st);
                    }
                }
            }
        }
        while (op.size() > 0) {
            o.push(op.pop());
        }
        return o;
    }
    public Stack<String> itop(String[] s) {
        return itop(s,0);
    }

    public static boolean isLower(String c1, String c2) {
        return precedence(c2) > precedence(c1);
    }

    public static int precedence(String c) {
        return switch (c) {
            case "+", "-" -> 1;
            case "*", "/", "%" -> 2;
            case "^" -> 3;
            case ">", "<", "=" -> 0;
            case "&", "|", "~" -> -1;
            case "(" -> 5;
            default -> 4;
        };
    }

    public int parse(Stack<String> s) {

        for (String st : s) {
            if (BWORDS.contains(st)) {
                sts.push(eval(st,0));
            } else if (UWORDS.contains(st)) {
                sts.push(eval(st,1));
            } else if (fun.containsKey(st)) {
                sts.push(eval(st,2));
            } else if (vars.containsKey(st)) {
                sts.push(vars.get(st));
            } else {
                sts.push(Integer.parseInt(st));
            }
        }
        return sts.pop();
    }

    public int eval(String s, int i) {
        return switch(i) {
            case 0 -> bineval(sts.pop(), sts.pop(), s);
            case 1 -> uneval(sts.pop(), s);
            case 2 -> funeval(s);
            default -> 0;
        };
    }

    public int funeval(String s){
        String[][] st = fun.get(s);
        Stack<Integer> flipper = new Stack<>();
        for(int i = 0; i < st[0].length; i++) {
            flipper.push(sts.pop());
        }
        for(String key: st[0]){
            vars.put(key,flipper.pop());
        }
        return this.parse(itop(st[1]));
    }

    public int bineval(int a, int b, String s) {
        return switch (s) {
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
        };
    }
    public int uneval(int a,  String s) {
        return switch (s) {
            case "!" -> fact(a);
            case "~" -> ~a;
            default -> 0;
        };
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
        for(int i = 3; i <= u; i++){
            out*=i;
        }
        return out;
    }
}