package mua;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class Operation {
    // make <name> <value> ： 将value绑定到name上，绑定后的名字位于当前命名空间，返回value。
    // 此⽂档中的基本操作的名字不能重新命名
    public static Value make(Value a, Value b, NameSpace namespace) {
        return namespace.bindName(a.data, b);
    }

    // thing <name> ：返回word所绑定的值
    // :<name> ：与thing相同
    public static Value thing(String name, NameSpace namespace) {
        if (namespace.existName(name))
            return namespace.getValue(name);
        return null;
    }

    // print <value> ：输出value，返回这个value
    public static Value print(Value v) {
        if (v == null)
            return null;
        System.out.println(v.data);
        return v;
    }

    // read ：返回⼀个从标准输⼊读取的数字或字
    public static Value read(Scanner in) {
        String str = in.next();
        int type = Word.typeJudge(str);
        if (type == 1)
            return new Value(1, str); // number
        else
            return new Value(2, str); // word
    }

    // 运算符operator
    // add, sub, mul, div, mod ： <operator> <number> <number>
    public static Value operator(int type, Value a, Value b) {
        // 数字和布尔量在计算时可以被看作是字的特殊形式，即在字⾯量和变量中的字
        // 当其中的内容是数字或布尔量时，总是可以根据需要⾃动被转换成数字或布尔量
        int result_type = 1;
        String data1 = a.data;
        String data2 = b.data;
        if (a.type == 4 || b.type == 4) { // bool
            result_type = 4; // bool
            if (a.data.equals("true"))
                data1 = "1";
            else
                data1 = "0";
            if (b.data.equals("true"))
                data2 = "1";
            else
                data2 = "0";
        }

        double p = Double.valueOf(data1);
        double q = Double.valueOf(data2);
        double result = 0.0;
        switch (type) {
            case 1:
                result = (double) p + q; // add
                break;
            case 2:
                result = (double) p - q; // sub
                break;
            case 3:
                result = (double) p * q; // mul
                break;
            case 4:
                result = (double) p / q; // div
                break;
            case 5:
                result = p % q; // mod
                break;
            default:
                System.out.println("Not exist such operator!");
                break;
        }
        if (result_type == 4) { // bool
            if (result == 0)
                return new Value(4, "false");
            else
                return new Value(4, "true");
        } else // number
            return new Value(result_type, String.valueOf(result));
    }

    public static String getList(Scanner in, String str) {
        // 表的字⾯量以⽅括号 [] 包含，其中的元素以空格分隔；元素可是任意类型；元素类型可不⼀致
        // 表的第⼀个元素和 [ 之间，以及最后⼀个元素和 ] 之间不需要有空格分隔
        // 表中的字不需要 " 引导
        // 这是⼀个有三层表的字⾯量的例⼦： [a [b [c d] e]]
        while (countChar(str, '[') != countChar(str, ']')) // 还没读完，继续读
            str += " " + in.next();
        if (countChar(str, '[') == countChar(str, ']')) { // []必须成对，整个list读完了
            if (str.split(" |\\[|\\]").length == 0)
                return "";
            return str.substring(1, str.length() - 1);
        }
        return str.substring(1, str.length() - 1);
    }

    private static int countChar(String s, char t) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == t)
                count++;
        }
        return count;
    }

    public static Value erase(Value a, NameSpace namespace) {
        return namespace.unbindName(a.data);
    }

    public static Value isname(Value word, NameSpace namespace) {
        if (word.type != 2) {
            System.out.println(word.data + " is not a word!");
            return null;
        }
        return new Value(4, String.valueOf(namespace.isName(word.data))); // true/false
    }

    public static Value isnumber(Value a) {
        String reg = "^-?[0-9]+(.[0-9]+)?$";
        return new Value(4, (a.type == 1 || (a.type == 2 && a.data.matches(reg))) ? "true" : "false");
    }

    public static Value isword(Value a) {
        return new Value(4, a.type == 2 ? "true" : "false");
    }

    public static Value islist(Value a) {
        return new Value(4, a.type == 3 ? "true" : "false");

    }

    public static Value isbool(Value a) {
        return new Value(4, a.type == 4 ? "true" : "false");
    }

    // isempty <word|list> : 返回word或list是否是空
    public static Value isempty(Value a) {
        if (a.type == 2 || a.type == 3)
            return new Value(4, a.data.equals("") ? "true" : "false");
        return null;
    }

    // eq ： <operator> <number|word> <number|word>
    public static Value eq(Value a, Value b) {
        if (a.type != b.type)
            return new Value(4, "false");
        if (a.type == 1 && b.type == 1) // number
            return new Value(4, (Double.parseDouble(a.data) == Double.parseDouble(b.data) ? "true" : "false"));
        else if (a.type == 2 && b.type == 2) // word
            return new Value(4, (a.data.equals(b.data) == true ? "true" : "false"));
        return null;
    }

    // gt ： <operator> <number|word> <number|word>
    public static Value gt(Value a, Value b) {
        if (a.type == 1 || b.type == 1)
            return new Value(4, (Double.parseDouble(a.data) > Double.parseDouble(b.data) ? "true" : "false"));
        else
            return new Value(4, (a.data.compareTo(b.data) > 0 ? "true" : "false"));
    }

    // lt ： <operator> <number|word> <number|word>
    public static Value lt(Value a, Value b) {

        if (a.type == 1 || b.type == 1)
            return new Value(4, (Double.parseDouble(a.data) < Double.parseDouble(b.data) ? "true" : "false"));
        else
            return new Value(4, (a.data.compareTo(b.data) < 0 ? "true" : "false"));
    }

    // and ： <operator> <bool> <bool>
    public static Value and(Value a, Value b) {
        // 0,0->0 1,0->0 0,1->0 1,1->1
        if ((!a.data.equals("true") && !a.data.equals("false")) || (!b.data.equals("true") && !b.data.equals("false")))
            return new Value(4, "false");
        if (a.data.equals("true") && b.data.equals("true"))
            return new Value(4, "true");
        else
            return new Value(4, "false");
    }

    // or ： <operator> <bool> <bool>
    public static Value or(Value a, Value b) {
        // 0,0->0 0,1->1 1,0->1 1,1->1
        if ((!a.data.equals("true") && !a.data.equals("false")) || (!b.data.equals("true") && !b.data.equals("false")))
            return new Value(4, "false");
        if (a.data.equals("false") && b.data.equals("false"))
            return new Value(4, "false");
        else
            return new Value(4, "true");
    }

    // not ： not <bool>
    public static Value not(Value a) {
        if (a.data.equals("true"))
            return new Value(4, "false");
        else if (a.data.equals("false"))
            return new Value(4, "true");
        return null;
    }

    // if <bool> <list1> <list2> ：如果bool为真，则执⾏list1，否则执⾏list2
    // list均可以为空表，返回list1或list2执⾏后的结果
    // 如果被执⾏的是空表，返回空表
    // 如果被执⾏的表只有⼀项，且⾮OP，返回该项
    public static Value doif(Value v1, Value v2, Value v3, NameSpace namespace) {
        if (v1.type != 4) {
            System.out.println("if_condition is not a bool!");
            return null;
        }
        if (v1.data.equals("true")) {
            if (Operation.isempty(v2).data.equals("true")) // 空表
                return v2;
            if (v2.data.length() == 1)
                return v2;
            Scanner scan = new Scanner(v2.data);
            Value v = null;
            while (scan.hasNext())
                v = Command.readCommand(scan, namespace);
            return v;
        } else {
            if (Operation.isempty(v3).data.equals("true")) // 空表
                return v3;
            if (v3.data.length() == 1)
                return v3;
            Scanner scan = new Scanner(v3.data);
            Value v = null;
            while (scan.hasNext())
                v = Command.readCommand(scan, namespace);
            return v;
        }
        // return null;
    }

    // 获得一个不含有外面两个括号的表达式
    public static String getExpression(String expression, Scanner in) {
        if (countChar(expression, '(') == countChar(expression, ')'))
            return expression.substring(1, expression.length() - 1);
        expression += " " + in.next();
        while (countChar(expression, '(') != countChar(expression, ')')) {
            expression += in.next();
        }
        return expression.substring(1, expression.length() - 1);
    }

    // 处理前缀op和word,exp只有num和op
    public static String adjustExpression(String expression, NameSpace namespace) {
        ArrayList<Value> operations = new ArrayList<>();
        String match = "[\\+\\-\\*\\/\\(\\)\\%\\ ]";
        String operands = "";
        for (int i = 0; i < expression.length();) {
            int j = i;
            for (; j < expression.length(); j++) {
                if (!expression.substring(j, j + 1).matches(match)) {
                    operands += expression.substring(j, j + 1);
                } else {
                    if (operands.trim().length() != 0) {
                        if (Word.typeJudge(operands) == 1) // 数字
                            operations.add(new Value(1, operands));
                        else
                            operations.add(new Value(2, operands));
                    }
                    if (!expression.substring(j, j + 1).equals(" "))
                        operations.add(new Value(2, expression.substring(j, j + 1)));
                    operands = "";
                    // 如果遇到了运算符什么的就退出
                    break;
                }
            }
            i = j + 1;
        }
        if (operands.trim().length() != 0) {
            if (operands.matches(match))
                operations.add(new Value(1, operands));
            else {
                if (Word.typeJudge(operands) == 1) // 数字
                    operations.add(new Value(1, operands));
                else
                    operations.add(new Value(2, operands));
            }
        }
        String result = "";
        // 分割成一个后面空一个空格的形式
        for (Value v : operations) {
            if (result.equals(""))
                result += v.data;
            else
                result += " " + v.data;
        }

        String returnResult = "";
        Scanner scanner = new Scanner(result);
        while (true) {
            Value v = Command.getParameter(scanner, namespace);
            if (v == null)
                break;
            returnResult += v.data;
        }
        return returnResult.trim();
    }

    // return <value> ：停⽌执⾏函数，设定value为返回给调⽤者的值
    public static Value returnValue(Scanner in, NameSpace namespace, Value v) {
        namespace.bindName("-output", v); // output value
        while (in.hasNext())
            in.nextLine();
        return v;
    }

    // export <name> ：将本地make 的变量<name> 输出到全局，返回它的值：
    // 如果全局没有这个变量，则增加⼀个全局变量
    // 如果全局已经有了同名的变量，则替换全局变量的值
    // 在函数内make 出来的函数⼀样可以被export 到全部
    public static Value export(NameSpace namespace, Value v) {
        mua.Main.nameSpace.bindName(v.data, namespace.getValue(v.data));
        return v;
    }

    public static Value function(String cmdString, NameSpace namespace, Scanner in) {
        Value v1, v2;
        Value function = namespace.getValue(cmdString); // get the loacl function
        if (function == null)
            function = Main.nameSpace.getValue(cmdString); // get the global function
        if (function == null || function.type != 3) {
            System.out.println("No such function!");
            return null;
        }
        String functionCommand = function.data;
        Scanner newScanner = new Scanner(functionCommand);
        NameSpace newNameSpace = new NameSpace(); // local variables
        v1 = Command.getParameter(newScanner, namespace); // 参数表
        v2 = Command.getParameter(newScanner, namespace); // 操作表
        if (v1 != null && isempty(v1).data.equals("false")) {
            // 如果参数表不为null，就需要参数绑定
            String[] parameterList = v1.data.split(" |\t");
            for (int i = 0; i < parameterList.length; i++) {
                // 参数绑定：参数的值来源于总体
                Value v = Command.getParameter(in, namespace);
                newNameSpace.bindName(parameterList[i], v);
            }
        }
        if (v2 != null && isempty(v2).data.equals("false")) {
            // 如果操作表不为null，就写到要读取的地方
            newScanner = new Scanner(v2.data);
            while (newScanner.hasNext()) {
                Command.readCommand(newScanner, newNameSpace);
            }
        }
        return newNameSpace.getValue("-output"); // return value
    }

    // readlist: 返回一个从标准输入读取的一行，构成一个表，行中每个以空格分隔部分是list的一个元素，元素的类型为字
    public static Value readList(Scanner scan) {
        scan.nextLine();
        String result = scan.nextLine();
        return new Value(4, result.substring(1, result.length() - 1));
    }

    // word <word> <word|number|bool> ：将两个word合并为⼀个word，第⼆个值可以是word、number或bool
    public static Value word(Value a, Value b) {
        // Value.type: 1-number，2-word，3-list，4-bool
        if (a.type == 2 && b.type != 3) {
            return new Value(2, a.data + b.data); // word
        } else
            return null;
    }

    // sentence <value1> <value2> ：将value1和value2合并成⼀个表，两个值的元素并列，value1的在value2的前⾯
    public static Value sentence(Value a, Value b) {
        return new Value(3, a.data + " " + b.data);// list
    }

    // list <value1> <value2> ：将两个值合并为⼀个表，如果值为表，则不打开这个表
    public static Value list(Value a, Value b) {
        String p = null, q = null;
        if (a.type == 3) // list
            p = "[" + a.data + "]"; // 不打开这个表
        else
            p = a.data;
        if (b.type == 3) // list
            q = "[" + b.data + "]"; // 不打开这个表
        else
            q = b.data;
        return new Value(3, p + " " + q);// list
    }

    // join <list> <value>
    // 将value作为list的最后⼀个元素加⼊到list中（如果value是表，则整个value成为表的最后⼀个元素）
    public static Value join(Value a, Value b) {
        String p = null;
        if (b.type == 3) // list
            p = "[" + b.data + "]"; // 整个value成为表的最后⼀个元素
        else
            p = b.data;
        if (!a.data.equals("")) // empty list
            return new Value(4, a.data + " " + p);
        else
            return new Value(4, p);
    }

    // first <word|list> ：返回word的第⼀个字符，或list的第⼀个元素
    public static Value first(Value a) {
        if (a.type != 3) // not list (is word)
            return new Value(2, a.data.substring(0, 1)); // 返回word的第⼀个字符
        else // list
            return readNext(new Scanner(a.data)); // list的第⼀个元素
    }

    // last <word|list> ：返回word的最后⼀个字符，list的最后⼀个元素
    public static Value last(Value a) {
        if (a.type == 2) // word
            return new Value(2, a.data.substring(a.data.length() - 1)); // 返回word的最后⼀个字符
        else { // list
            Scanner scan = new Scanner(a.data);
            Value p, q = null;
            while ((p = readNext(scan)) != null)
                q = p;
            return q; // list的最后⼀个元素
        }
    }

    // butfirst <word|list> ：返回除第⼀个元素外剩下的表，或除第⼀个字符外剩下的字
    public static Value butfirst(Value a) {
        if (a.type == 2) // word
            return new Value(2, a.data.substring(1)); // 返回除第⼀个字符外剩下的字
        else {
            Scanner scan = new Scanner(a.data);
            Value result = new Value(3, "");
            Value p;
            p = readNext(scan);
            while ((p = readNext(scan)) != null) {
                if (p.type != 3) { // not list
                    if (result.data.equals("")) // empty
                        result.data += p.data;
                    else
                        result.data += " " + p.data;
                } else { // list
                    if (result.data.equals("")) // empty
                        result.data += "[" + p.data + "]";
                    else
                        result.data += " [" + p.data + "]";
                }
            }
            return result; // 返回除第⼀个元素外剩下的表
        }
    }

    // butlast <word|list> ：返回除最后⼀个元素外剩下的表，或除最后⼀个字符外剩下的字
    public static Value butlast(Value a) {
        if (a.type == 2) // word
            return new Value(2, a.data.substring(0, a.data.length() - 1)); // 返回除最后⼀个字符外剩下的字
        else { // list
            Scanner scan = new Scanner(a.data);
            Value result = new Value(4, "");
            Value p, q = null;
            while ((p = readNext(scan)) != null) {
                if (q != null) {
                    if (q.type != 3) { // not list
                        if (result.data.equals("")) // empty
                            result.data += q.data;
                        else
                            result.data += " " + q.data;
                    } else { // list
                        if (result.data.equals("")) // empty
                            result.data += "[" + q.data + "]";
                        else
                            result.data += " [" + q.data + "]";
                    }
                }
                q = new Value(p);
            }
            return result; // 返回除最后⼀个元素外剩下的表
        }
    }

    // read next value in the list
    private static Value readNext(Scanner scan) {
        if (!scan.hasNext())
            return null;
        String str = scan.next();
        if (str.startsWith("[")) // list
            return new Value(3, Operation.getList(scan, str));
        else // word
            return new Value(Word.typeJudge(str), str);
    }

    // random <number> ：返回[0,number)的⼀个随机数
    public static Value random(Value a) {
        return new Value(1, String.valueOf(new Random().nextDouble() * Double.valueOf(a.data))); // double [0,number)
    }

    // int <number> : floor the int
    public static Value Int(Value a) {
        return new Value(1, String.valueOf(Math.floor(Double.valueOf(a.data))));
    }

    // sqrt <number> ：返回number的平⽅根
    public static Value Sqrt(Value a) {
        return new Value(1, String.valueOf(Math.sqrt(Double.valueOf(a.data))));
    }

    // save <word> ：保存当前命名空间在word⽂件中，返回⽂件名
    public static Value save(Value v, NameSpace namespace) {
        String fileName = v.data;
        File file = new File(fileName);
        try {
            file.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            HashMap<String, Value> NameValueMap = namespace.getNameValueMap();
            String line = "";
            for (String name : NameValueMap.keySet()) { // save in lines, e.g: make "a 1
                line += "make \"" + name + " ";
                if (NameValueMap.get(name).type == 2) // word
                    line += "\"" + NameValueMap.get(name).data + "\n";
                else if (NameValueMap.get(name).type == 3) // list
                    line += "[" + NameValueMap.get(name).data + "]\n";
                else // number, boolean
                    line += NameValueMap.get(name).data + "\n";
                out.write(line);
                line = "";
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v; // 返回⽂件名
    }

    // load <word> ：从word⽂件中装载内容，加⼊当前命名空间，返回true
    public static Value load(Value v, NameSpace namespace) {
        String fileName = v.data;
        File file = new File(fileName);
        Long fileLength = file.length();
        byte[] fileContent = new byte[fileLength.intValue()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(fileContent);
            in.close();
            String content = new String(fileContent);
            Scanner scanner = new Scanner(content);
            while (scanner.hasNext())
                Command.readCommand(scanner, namespace); // run all "make" commands
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Value(4, "true"); // 返回true
    }

    // erall ：清除当前命名空间的全部内容，返回true
    public static Value erall(NameSpace namespace) {
        return namespace.erall();
    }

    // poall ：返回当前命名空间的全部名字的list
    public static Value poall(NameSpace namespace) {
        return namespace.poall();
    }

}