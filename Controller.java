package calculator;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.*;
import java.util.ArrayList;

public class Controller {

    public static void readCmd() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String cmd = scanner.nextLine();
            //for passing the test
            if (Pattern.compile("[\\*\\/]{2,}").matcher(cmd).find()) {
                System.out.println("Invalid expression");
            }
            try {
                cmd = cleanGarbage(cmd);
            } catch (Exception e) {
                cmd = ""; //catches NPE if command is empty;
            }

            execute(getCmdType(cmd), cmd);
        }
    }


    //Clears entered string from spaces and excessive operands
    public static String cleanGarbage(String cmd) {
        if (!cmd.isEmpty()) {
            cmd = cmd.replaceAll("=+", " = ");
            cmd = cmd.replaceAll("\\(", " ( ");
            cmd = cmd.replaceAll("\\)"," ) " );
            cmd = cmd.replaceAll("[\\^]+", " ^ ");
            cmd = cmd.replaceAll("-{2}", "+");
            cmd = cmd.replaceAll("[+]+", " + ");
            cmd = cmd.replaceAll("[*]+", " * ");
            cmd = cmd.replaceAll("^\\s+", "");
//            cmd = cmd.replaceAll("\\s+", "");
//            cmd = cmd.replaceAll("[\\/]+", " / "); сломало комманды
            cmd = cmd.replaceAll("\\+\\s+-|-\\s+\\+", " - ");
            cmd = cmd.replaceAll(" +", " ");
        }
        return cmd;
    }

    //Determinate what to do with an entered string. This can be an mathematical operation,
    //command, that begins with "/", setting and showing a variable
    public static String getCmdType(String cmd) {
        String result = "continue";
        if (cmd.isEmpty()) {
            return "continue";
        }
        if (cmd.contains("=")) {
            return "set variable";
        } else if (cmd.substring(0, 1).equals("/")) {
            return "command";
        } else if (cmd.matches("^[a-zA-Z\\d\\s]+$")) {
            return "show variable";
        } else if (Pattern.compile("[+\\-*\\^\\(\\)\\/]+").matcher(cmd).find()) {
            return "equation";
        }
        return result;
    }

    public static void execute(String type, String cmd) {
        switch (type) {
            case "set variable":
                //remove spaces
                cmd = cmd.replaceAll("\\s", "");
                //split to array with name and value
                String[] parts = cmd.split("=");
                //check if we are assigning one and only one variable to another
                if (parts.length > 2) {
                    System.out.println("Invalid assignment");
                    break;
                }
                //check for incorrect variables
                String check = Model.checkVariable(parts[0], parts[1]);
                if (!check.equals("OK")) {
                    System.out.println(check);
                    break;
                }

                //set variable value. try to set double and if it fails set value of another variable
                //update: now it's BigInteger...

                try {
                    BigInteger tmp = new BigInteger(parts[1]);
                    Model.setVar(parts[0], tmp);
                } catch (Exception e) {
                Model.setVar(parts[0], parts[1]);
            }
            break;
            case "show variable":
                cmd = cmd.replaceAll("\\s+", "");
                System.out.println(Model.getVar(cmd));
                break;
            case "equation":
                System.out.println(Model.solveEquation(cmd));
                break;
            case "command":
                Model.runCommand(cmd);
                break;

        }
    }


}
