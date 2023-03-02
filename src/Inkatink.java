import java.util.*;

public class Inkatink {
    public static final String BWORDS = "+-*/%^<>=&|";

    public static final String UWORDS = "~-!";

    public static final String WORDS = BWORDS+UWORDS;


    private Stack<Object> sts;
    private HashMap<String, Integer> vars;
    private HashMap<String, Stack<String>[]> fun;
    private HashSet<String> special;

    public static final Scanner uin = new Scanner(System.in);

    public Inkatink() {
        fun = new HashMap<>();
        vars = new HashMap<>();
        sts = new Stack<>();
        special = new HashSet<>();
        special.add("print");
        special.add("def");
        special.add("as");
        special.add("if");
        special.add("then");
        special.add("else");

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

    public int popInt(){
        return (int)sts.pop();
    }
    public String popStr(){
        return (String)sts.pop();
    }

    public void compute(String s){
//        Stack<String> st = itop(s.split(" "));
//        System.out.println(st);
//        System.out.println(st.peek());

        if(s.startsWith("#"))
            return;
        if(s.contains("read")) {
            System.out.print("> ");
            s = s.replaceFirst("read", uin.nextLine());
        }
        while(s.contains("pop"))
            s = s.replaceFirst("pop", String.valueOf(popInt()));
        String[] line = s.split(" ");
        Stack<String> str = itop(line);
//        System.out.println(str);
//        System.out.println(str.peek());
        parse(str);
//        System.out.println(parse);
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
                } else if (special.contains(st)) {
                    while (!op.isEmpty()) {
                        o.push(op.pop());
                    }
                    o.push(st);
                } else {
                    if (!st.equals("(")) {
                        o.push(st);
                    }
                }
            }
        }
        while (!op.isEmpty()) {
            o.push(op.pop());
        }
        while (!o.isEmpty()){
            op.push(o.pop());
        }
        return op;
    }
    public Stack<String> itop(String[] s) {
        return itop(s,0);
    }

    public boolean isLower(String c1, String c2) {
        return precedence(c2) > precedence(c1);
    }

    public int precedence(String c) {
        return switch (c) {
            case "+", "-" -> 1;
            case "*", "/", "%" -> 2;
            case "^" -> 3;
            case ">", "<", "=" -> 0;
            case "&", "|", "~" -> -1;
            case "(" -> 5;
            case "?", ":" -> -2;
            case ";" -> -4;
            default -> 4;
        };
    }

    public int parse(Stack<String> s) {
        String st;
//        System.out.println(s);
//        System.out.println(s.peek());

        while (!s.isEmpty()) {
            st = s.pop();
            if (st.equals("?")){
                if(popInt() == 0)
                    while(!st.equals(":"))
                        st = s.pop();
            } else if (st.equals(";")) {
                return popInt();
            } else if (BWORDS.contains(st)) {
                sts.push(eval(st,0));
            } else if (UWORDS.contains(st)) {
                sts.push(eval(st,1));
            } else if (fun.containsKey(st)) {
                sts.push(eval(st,2));
            } else if (vars.containsKey(st)) {
                sts.push(vars.get(st));
            } else if (special.contains(st)) {
                spevl(st,s);
            } else {
                try {
                    sts.push(Integer.parseInt(st));
                } catch (Exception ex){
                    sts.push(st);
                }
            }
//            System.out.println(sts);
//            System.out.println(sts.peek());
        }

        return (int)sts.peek();
    }

    private void spevl(String s,Stack<String> st) {
        switch (s){
            case "def" -> {
                String name = st.pop();
                String temp;
                Stack<String>[] vars = new Stack[2];
                vars[0] = new Stack<>();
                vars[1] = new Stack<>();
                System.out.println(st);
                while(!(temp = st.pop()).equals("as")){
                    vars[0].push(temp);
                }
                while(!(temp = st.pop()).equals(";")){
                    vars[1].push(temp);
                }
                fun.put(name,vars);
            }
            case "print" -> System.out.println(parse(st));
            case "clear" -> sts.clear();
//            default -> ;
        }
    }

    public int eval(String s, int i) {
        return switch(i) {
            case 0 -> bineval(popInt(), popInt(), s);
            case 1 -> uneval(popInt(), s);
            case 2 -> funeval(s);
            default -> 0;
        };
    }

    public int funeval(String s){
//        Stack<String>[] st = fun.get(s);
//        Stack<Integer> flipper = new Stack<>();
//        for(int i = 0; i < st[0].length; i++) {
//            flipper.push(sts.pop());
//        }
//        for(String key: st[0]){
//            vars.put(key,flipper.pop());
//        }
//        this.parse(st);
        return 0;
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