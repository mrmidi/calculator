package calculator;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model {

    public static HashMap<String, BigInteger> varMap = new HashMap<>();


    //set variable of variable
    //todo replace valuable parameter to name2 or something
    public static void setVar(String name, String value) {
        BigInteger val;

        try {
            val = new BigInteger(getVar(value));
            varMap.put(name, val);
        } catch (Exception e) {
            System.out.println("Unknown variable");
        }

//        if (!(val == "Unknown variable")) {
//            varMap.put(name, val);
//        } else {
//            System.out.println("Unknown variable");
//        }

    }



    //set variable value
    public static void setVar(String name, BigInteger val) {
        varMap.put(name, val);
    }

    public static String getVar(String name) {
        String result;
        result = String.valueOf(varMap.get(name));
        if (result.equals("null")) {
            result = "Unknown variable";
        }
        return result;
    }

    public static String checkVariable(String var1) {
        if (!var1.matches("^[a-zA-Z]+$")) {
            return "Invalid identifier";
        }
        return "OK";
    }

    public static String checkVariable(String var1, String var2) {
        //first variable should be only letters
        if (!var1.matches("^[a-zA-Z]+$")) {
            return "Invalid identifier";
        }
        //second variable should be letters or number (possible double)
        if (!var2.matches("^[a-zA-Z]+$") && !var2.matches("^[-]?\\d+[.,]?\\d*$")) {
            return "Invalid assignment";
        }
        return "OK";
    }

    //приоритет
    static int Prec(String op)
    {
        switch (op)
        {
            case "+":
            case "-":
                return 1;

            case "*":
            case "/":
                return 2;

            case "^":
                return 3;
        }
        return -1;
    }

    public static String solveEquation(String equation){
        if (!checkBrackets(equation)) {
            return "Invalid expression";
        }
        //System.out.println(equation);
        String[] parts = equation.split(" ");
        ArrayList<String> eqArray = new ArrayList<>();
        eqArray = getEquationArray(parts);
        ArrayList<String> resultArray = new ArrayList<>();
        ArrayDeque<String> stackArray = new ArrayDeque<>();
        ArrayDeque<String> solver = new ArrayDeque<>();

        // Convert infix to postfix
        for (int i = 0; i < eqArray.size(); i++) {
            String s = eqArray.get(i);
            String p;
            if (isDouble(s)) { //add number to array
                resultArray.add(eqArray.get(i));
            } else if (s.equals("(")) {
                stackArray.push(s);
            } else if (s.equals(")")) {
                //closing bracket. move all operators from stack to result until opening bracket
                while (!stackArray.peek().equals("(")) {
                    resultArray.add(stackArray.pop());
                }
                //and remove bracket itselft
                stackArray.pop();
            } else if (s.matches("[*+-\\/\\^]")) {
                p = stackArray.peek();
                while (!stackArray.isEmpty() && Prec(s) <= Prec(stackArray.peek())) {
                    resultArray.add(stackArray.pop());
                }
                stackArray.push(s);
            }

        }
        resultArray.addAll(stackArray);

        for (int i = 0; i < resultArray.size(); i++) {
            //changed to work with bigint
            BigInteger a;
            BigInteger b;
            String op;
            if (!resultArray.get(i).matches("[*+-\\/\\^]")) {
                solver.push(resultArray.get(i));
            } else {
                b = new BigInteger(solver.poll());
                a = new BigInteger(solver.poll());
                op = resultArray.get(i);
                solver.push(String.valueOf(process(a, b, op)));
            }
        }


        return solver.peek();
    }

    public static boolean isDouble(String val) {
        try {
            Double.valueOf(val);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static String solveEquationOld(String equation) {
        Double a = 0.0;
        String result = "Invalid expression";
        ArrayList<Double> numArray = new ArrayList<>();
        //extract operands
        //String opRegEx = "(\\s*-?\\d+\\s*)";
        String opRegEx = "\\s*-?[\\da-zA-Z]+\\s*";
        String[] operands = equation.split(opRegEx);
        //get variables
        String[] vars = equation.split("\\s[+-\\/*]+\\s");
        numArray = getNumbers(vars);
        try {
            a = numArray.get(0);
        } catch (Exception e) {
            return "Invalid expression";
        }

        for (int i = 0; i < numArray.size()-1; i++) {
            Double b = numArray.get(i+1);
            //written for passing the tests
            try {
                String operand = operands[i+1];
                a = process(a, b, operand);
//                counted = true;
            } catch(Exception e) {
//                    System.out.println("Invalid expression");
//                counted = false;
            }

        }
        try {
            result = String.valueOf(a);
        } catch (Exception e) {
            result = "Invalid expression";
        }
        return result;
    }


    //delete. deprecated
    public static ArrayList<Double> getNumbers(String vars[]) {
        ArrayList<Double> numArray = new ArrayList<>();
        for (int i = 0; i < vars.length; i++) {
            if (!(vars[i].matches("[-]?\\d+[.,]?\\d*"))) {
                    vars[i] = getVar(vars[i]);
            }
            if (vars[i] == "Unknown variable") {
                System.out.println("Unknown variable in equation!");
                numArray.clear();
                break;
            }
            numArray.add(Double.valueOf(vars[i]));
        }
        return numArray;
    }

    public static ArrayList<String> getEquationArray(String equation[]) {
        ArrayList<String> eqArray = new ArrayList<>();
        for (int i = 0; i < equation.length; i++) {
            if (!(equation[i].matches("[-]?\\d+[.,]?\\d*")) && (!equation[i].matches("[+-\\/*()^]+"))) {
                equation[i] = getVar(equation[i]);
            }
            if (equation[i] == "Unknown variable") {
                System.out.println("Unknown variable in equation!");
                eqArray.clear();
                return eqArray;
            }
            eqArray.add(equation[i]);

        }
        return eqArray;
    }

    public static Double process(Double a, Double b, String operand){
        Double result;
        switch (operand) {
            case "+":
                result = a + b;
                break;
            case "-":
                result = a - b;
                break;
            case "*":
                result = a * b;
                break;
            case "/":
                result = a / b;
                break;
            case "^":
                result = Math.pow(a, b);
                break;
            default:
                result = 0.0;
        }
        return result;
    }

    //another constructor to solve double
    public static BigInteger process(BigInteger a, BigInteger b, String operand){
        BigInteger result;
        switch (operand) {
            case "+":
                result = a.add(b);
                break;
            case "-":
                result = a.subtract(b);
                break;
            case "*":
                result = a.multiply(b);
                break;
            case "/":
                result = a.divide(b);
                break;
            default:
                result = BigInteger.ONE;
        }
        return result;
    }

    public static void processEquation(String equation) {
        Deque<String> queue = new ArrayDeque<>();
        ArrayList<String> outArray = new ArrayList<>();
        String [] parts = equation.split(" ");
        for (int i = 0; i < parts.length; i++) {

        }



    }

    public static void runCommand(String command) {
        String cmd = command.replaceAll("\\/", "");
        switch (cmd) {
            case "help":
                System.out.println("+ and - operations");
                break;
            case "exit":
                System.out.println("Bye!");
                System.exit(0);
            default:
                System.out.println("Unknown command");
        }


    }

    public static String formatDouble(String string) {
        String result = "";
        Double dbl;
        //DecimalFormat format = new DecimalFormat("0.#");
        DecimalFormat format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        try {
            dbl = Double.valueOf(string);
            result = String.valueOf(format.format(dbl));
        } catch (Exception e) {
            result = string;
        }
        return result;
    }

    public static Boolean checkBrackets(String str) {
        Deque<Character> queue = new ArrayDeque<>();
        char c;
        char peek;
        if (str.isEmpty()) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            if (c == '(' || c == '{' || c == '[') {
                queue.push(c);
            }
            if (c == ')' || c == '}' || c == ']') {
                if (queue.isEmpty()) {
                    return false;
                }
                peek = queue.peek();
                if (c == '}' && peek == '{' || c == ')' && peek == '(' || c == ']' && peek == '[') {
                    queue.pop();
                } else {
                    return false;
                }
            }
        }

        return queue.isEmpty();
    }



}
