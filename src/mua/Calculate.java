package mua;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Calculate {

    // 运算优先级 + - * / %
    public static int priority(String oper) {
        if (oper.equals("(") || oper.equals(")"))
            return 0;
        else if (oper.equals("+") || oper.equals("-"))
            return 1;
        else if (oper.equals("*") || oper.equals("/") || oper.equals("%"))
            return 2;
        else
            return -1;
    }

    // 计算表达式
    public static double compute(String str) {
        Stack<String> op = new Stack<>();// 操作符栈
        List<String> RPN = new ArrayList<>();// 逆波兰表达式队列
        // 转为逆波兰表达式
        for (int i = 0; i < str.length(); i++) {
            char s = str.charAt(i);
            if (s == ' ') {

            } else if (s == '0' || s == '1' || s == '2' || s == '3' || s == '4' || s == '5' || s == '6' || s == '7'
                    || s == '8' || s == '9') { // 数字
                String num = String.valueOf(s);
                while (i + 1 < str.length() && (str.charAt(i + 1) == '0' || str.charAt(i + 1) == '1'
                        || str.charAt(i + 1) == '2' || str.charAt(i + 1) == '3' || str.charAt(i + 1) == '4'
                        || str.charAt(i + 1) == '5' || str.charAt(i + 1) == '6' || str.charAt(i + 1) == '7'
                        || str.charAt(i + 1) == '8' || str.charAt(i + 1) == '9' || str.charAt(i + 1) == '.')) {
                    num = num + str.charAt(i + 1);
                    i++;
                } // 多位数字(>9)
                RPN.add(num);
            } else if (s == '(') { // 左括号
                op.push(String.valueOf(s));
            } else if (s == ')') { // 右括号
                while (!op.peek().equals("(")) {
                    RPN.add(op.pop());
                }
                op.pop();
            } else if (s == '-' && i != 0
                    && (str.charAt(i - 1) == '*' || str.charAt(i - 1) == '/' || str.charAt(i - 1) == '%')) { // 类似3*-1
                String num = "-" + str.charAt(i + 1);
                i++;
                while (i + 1 < str.length() && (str.charAt(i + 1) == '0' || str.charAt(i + 1) == '1'
                        || str.charAt(i + 1) == '2' || str.charAt(i + 1) == '3' || str.charAt(i + 1) == '4'
                        || str.charAt(i + 1) == '5' || str.charAt(i + 1) == '6' || str.charAt(i + 1) == '7'
                        || str.charAt(i + 1) == '8' || str.charAt(i + 1) == '9' || str.charAt(i + 1) == '.')) {
                    num = num + str.charAt(i + 1);
                    i++;
                } // 多位数字(>9)
                RPN.add(num);
            } else if (s == '-' && (i == 0 || (str.charAt(i - 1) != '0' && str.charAt(i - 1) != '1'
                    && str.charAt(i - 1) != '2' && str.charAt(i - 1) != '3' && str.charAt(i - 1) != '4'
                    && str.charAt(i - 1) != '5' && str.charAt(i - 1) != '6' && str.charAt(i - 1) != '7'
                    && str.charAt(i - 1) != '8' && str.charAt(i - 1) != '9'))) { // 类似 -2+3 -> 0-2+3,-(1+1)->0-(1+1)
                RPN.add("0");
                op.push(String.valueOf(s));
            } else {
                while (!op.isEmpty() && priority(op.peek()) >= priority(String.valueOf(s))) {
                    RPN.add(op.pop());
                }
                op.push(String.valueOf(s));
            }
        }
        while (!op.isEmpty()) {
            RPN.add(op.pop());
        }

        // 计算逆波兰表达式
        Stack<Double> operand = new Stack<>(); // 操作数栈
        for (int i = 0; i < RPN.size(); i++) {
            if (RPN.get(i).equals("+")) { // +
                operand.push(operand.pop() + operand.pop());
            } else if (RPN.get(i).equals("-")) { // -
                double operand2 = operand.pop();
                operand.push(operand.pop() - operand2);
            } else if (RPN.get(i).equals("*")) { // *
                operand.push(operand.pop() * operand.pop());
            } else if (RPN.get(i).equals("/")) { // /
                double operand2 = operand.pop();
                operand.push(operand.pop() / operand2);
            } else if (RPN.get(i).equals("%")) { // %
                double operand2 = operand.pop();
                operand.push(operand.pop() % operand2);
            } else { // 数字
                operand.push(Double.valueOf(RPN.get(i)));
            }
        }
        return operand.pop();
    }

}

