import java.util.*;

public class Inkatink {
    public static final String BWORDS = "+-*/%^<>=&|";

    public static final String UWORDS = "~-!";

    public static final String WORDS = BWORDS+UWORDS;


    private ArrayDeque<Object> sts;
    private HashMap<String, Integer> vars;
    private HashMap<String, Integer> parms;
    private HashMap<String, ArrayDeque<String>[]> fun;
    private HashSet<String> special;

    public static final Scanner uin = new Scanner(System.in);

    public Inkatink() {
        fun = new HashMap<>();
        vars = new HashMap<>();
        parms = new HashMap<>();
        sts = new ArrayDeque<>();
        special = new HashSet<>();
        special.add("print");
        special.add("printall");
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

    public void computeLines(String[] s){
        String[] line;
        for (int i = 0; i < s.length; i++){
            line = s[i].split(" ");
            try {
                switch (line[0]) {
                    case "#" -> {
                        continue;
                    }
//                    case "if" -> {
//                        parse(itop(line,1));
//                        if(popInt() != 0){
//                            while (!s[++i].equals("else")) {
//                                tempst.add(temp);
//                            }
//                        } else {
//
//                        }
//                    }
                    default -> {
                        parse(itop(line));
                    }
                }
            } catch (Exception ex) {
                System.err.println("problem at " + i);
                ex.printStackTrace();
//                System.err.println(ex.printStackTrace());
            }
        }
    }

    public ArrayDeque<String> itop(String[] s) {
        ArrayDeque<String> o = new ArrayDeque<>();
        ArrayDeque<String> op = new ArrayDeque<>();
        for (String st : s) {
            if (st.equals(")")) {
                while (op.size() > 0 && !op.peek().equals("("))
                    o.add(op.pop());
                if (op.peek().equals("("))
                    op.pop();

            } else if (WORDS.contains(st) || fun.containsKey(st)) {
                if (op.size() > 0)
                    if (isLower(st, op.peek())) {
                        o.add(op.pop());
                    }
                op.push(st);
            } else if (special.contains(st)) {
                while (!op.isEmpty()) {
                    o.add(op.pop());
                }
                o.add(st);
            } else {
                if (!st.equals("(")) {
                    o.add(st);
                }
            }
        }
        while (!op.isEmpty()) {
            o.add(op.pop());
        }
        return o;
    }

    public ArrayDeque<String> itop(String[] s, int i) {
        ArrayDeque<String> o = new ArrayDeque<>();
        ArrayDeque<String> op = new ArrayDeque<>();
        for (String st : s) {
            if(i > 0) {
                i--;
                continue;
            }
            if (st.equals(")")) {
                while (op.size() > 0 && !op.peek().equals("("))
                    o.add(op.pop());
                if (op.size() > 0 && op.peek().equals("("))
                    op.pop();

            } else if (WORDS.contains(st) || fun.containsKey(st)) {
                if (op.size() > 0)
                    if (isLower(st, op.peek())) {
                        o.add(op.pop());
                    }
                op.push(st);
            } else if (special.contains(st)) {
                while (!op.isEmpty()) {
                    o.add(op.pop());
                }
                o.add(st);
            } else {
                if (!st.equals("(")) {
                    o.add(st);
                }
            }
        }
        while (!op.isEmpty()) {
            o.add(op.pop());
        }
        return o;
    }

    public boolean isLower(String c1, String c2) {
        return precedence(c2) < precedence(c1);
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

    public void parse(ArrayDeque<String> s) {
        String st;
//        System.out.println(s);
//        System.out.println(s.peek());

        while (!s.isEmpty()) {
            st = s.pop();
            if (BWORDS.contains(st)) {
                bineval(popInt(), popInt(), st);
            } else if (UWORDS.contains(st)) {
                uneval(popInt(), st);
            } else if (fun.containsKey(st)) {
                funeval(st);
            } else if (parms.containsKey(st)) {
                sts.push(parms.get(st));
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
        }
    }

//    public void parse(ArrayDeque<String> s,HashMap<String,Integer> dict) {
//        String st;
//        System.out.println(s);
//        System.out.println(s.peek());
//        System.out.println(dict);
//
//
//        while (!s.isEmpty()) {
//            st = s.pop();
//            if (BWORDS.contains(st)) {
//                bineval(popInt(), popInt(), st);
//            } else if (UWORDS.contains(st)) {
//                uneval(popInt(), st);
//            } else if (fun.containsKey(st)) {
//                funeval(st);
//            } else if (dict.containsKey(st)) {
//                sts.push(dict.get(st));
//            } else if (vars.containsKey(st)) {
//                sts.push(vars.get(st));
//            } else if (special.contains(st)) {
//                spevl(st,s);
//            } else {
//                try {
//                    sts.push(Integer.parseInt(st));
//                } catch (Exception ex){
//                    sts.push(st);
//                }
//            }
//        }
//    }

    private void spevl(String s,ArrayDeque<String> st) {
        switch (s){
            case "def" -> {
                String name = st.pop();
                String temp;
                ArrayDeque<String>[] vars = new ArrayDeque[2];
                vars[0] = new ArrayDeque<>();
                vars[1] = new ArrayDeque<>();
                while(!(temp = st.pop()).equals("as")){
                    vars[0].push(temp);
                }
                while(!(temp = st.pop()).equals(";")){
                    vars[1].add(temp);
                }
                fun.put(name,vars);
            }
            case "if" -> {
                String temp;
                ArrayDeque<String> tempst = new ArrayDeque<>();
                while(!(temp = st.pop()).equals("then")){
                    tempst.add(temp);
                }

                parse(tempst);
                if(popInt() != 0) {
                    while (!(temp = st.pop()).equals("else")) {
                        tempst.add(temp);
                    }
//                    System.out.println(tempst +"tp");
                } else {
                    while (!st.pop().equals("else"));
//                    System.out.println(st +"st");
                    while (!(temp = st.pop()).equals(";")) {
                        tempst.add(temp);
                    }
                }
                parse(tempst);
                st.clear();
            }
            case "print" -> {
                parse(st);
                System.out.println(sts.peek());
            }
            case "printall" -> {
                parse(st);
                System.out.println(sts);
            }
            case "clear" -> sts.clear();
//            default -> ;
        }
    }

//    public void eval(String s, int i) {
//        sts.push( switch(i) {
//            case 0 -> bineval(popInt(), popInt(), s);
//            case 1 -> uneval(popInt(), s);
//            case 2 -> funeval(s);
//            default -> 0;
//        });
//    }

    public void funeval(String s){
        ArrayDeque<String>[] grum = new ArrayDeque[2];
        grum[0] = new ArrayDeque<>(fun.get(s)[0]);
        grum[1] = new ArrayDeque<>(fun.get(s)[1]);

        parms = new HashMap<>();

        while(!grum[0].isEmpty()){
            parms.put(grum[0].pop(),popInt());
        }
//        System.out.println(grum[1]);
//        System.out.println(parms + " dong");
        this.parse(grum[1]);
//        System.out.println(sts);
//        return (int)sts.peek();
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
        for(int i = 3; i <= u; i++){
            out*=i;
        }
        return out;
    }
}