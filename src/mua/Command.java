package mua;

import java.util.Scanner;

public class Command {
    public static Value readCommand(Scanner in, NameSpace namespace) {
        // while (in.hasNext()) {
        String cmdString = in.next(); // read in the command
        if (cmdString.equals("make")) {
            // make <name> <value> ： 将value绑定到name上，绑定后的名字位于当前命名空间，返回value。
            // 此⽂档中的基本操作的名字不能重新命名
            // make "b "a
            Value a = getParameter(in, namespace);
            Value b = getParameter(in, namespace);
            return Operation.make(a, b, namespace);
        } else if (cmdString.equals("print")) {
            // print <value> ：输出value，返回这个value
            return Operation.print(getParameter(in, namespace));
        } else if (cmdString.equals("erase")) {
            // erase <name> ：清除word所绑定的值，返回原绑定的值
            Value name = getParameter(in, namespace);
            return Operation.erase(name, namespace);
        } else if (cmdString.equals("run")) {
            // run <list> ：运⾏list中的代码，返回list中执⾏的最后⼀个op的返回值
            Value list = getParameter(in, namespace);
            Scanner newScanner = new Scanner(list.data);
            Value v = null;
            while (newScanner.hasNext())
                v = Command.readCommand(newScanner, namespace);
            return v;
        } else if (cmdString.equals("if")) {
            // if <bool> <list1> <list2> ：如果bool为真，则执⾏list1，否则执⾏list2
            // list均可以为空表，返回list1或list2执⾏后的结果
            // 如果被执⾏的是空表，返回空表
            // 如果被执⾏的表只有⼀项，且⾮OP，返回该项
            Value v1 = getParameter(in, namespace); // bool
            Value v2 = getParameter(in, namespace); // list1
            Value v3 = getParameter(in, namespace); // list2
            return Operation.doif(v1, v2, v3, namespace);
        } else if (cmdString.equals("return")) {
            // return <value> ：停⽌执⾏函数，设定value为返回给调⽤者的值
            Value v = getParameter(in, namespace);
            return Operation.returnValue(in, namespace, v);
        } else if (cmdString.equals("export")) {
            // export <name> ：将本地make 的变量<name> 输出到全局，返回它的值：
            // 如果全局没有这个变量，则增加⼀个全局变量
            // 如果全局已经有了同名的变量，则替换全局变量的值
            // 在函数内make 出来的函数⼀样可以被export 到全部
            Value v = getParameter(in, namespace);
            return Operation.export(namespace, v);
        } else if (cmdString.equals("save")) {
            // save <word> ：保存当前命名空间在word⽂件中，返回⽂件名
            Value v = getParameter(in, namespace);
            return Operation.save(v, namespace);
        } else if (cmdString.equals("load")) {
            // load <word> ：从word⽂件中装载内容，加⼊当前命名空间，返回true
            Value v =getParameter(in, namespace);
            return Operation.load(v,namespace);
        } else if (cmdString.equals("erall")) {
            // erall ：清除当前命名空间的全部内容，返回true
            return Operation.erall(namespace);
        } else if (cmdString.equals("poall")) {
            // poall ：返回当前命名空间的全部名字的list
            return Operation.poall(namespace);
        } else {
            // System.out.println("Not exist the operation!");
            // return null;
            return Operation.function(cmdString, namespace, in);
        }
    }

    public static Value getParameter(Scanner in, NameSpace namespace) {
        if (!in.hasNext())
            return null;
        String str = in.next();
        int valuetype = Word.typeJudge(str);
        Value a, b;
        if (valuetype == 1 || valuetype == 4) { // 1-number, 4-bool
            return new Value(valuetype, str);
        } else if (valuetype == 2) { // 2-word
            return new Value(valuetype, str.substring(1));
        } else if (valuetype == 3) { // 3-list
            String list = Operation.getList(in, str);
            return new Value(valuetype, list);
        } else if (Word.isBindValue(str)) { // :<name>
            String name = str.substring(1);
            if (namespace.existName(name)) {
                return namespace.getValue(name);
            } else {
                System.out.println("Not exist the name!");
            }
        } else if (Word.isOperator(str)) { // operator: add, sub, mul, div, mod
            int optype = 0;
            if (str.equals("add"))
                optype = 1;
            else if (str.equals("sub"))
                optype = 2;
            else if (str.equals("mul"))
                optype = 3;
            else if (str.equals("div"))
                optype = 4;
            else if (str.equals("mod"))
                optype = 5;
            else
                System.out.println("Not exist such operator!");
            a = getParameter(in, namespace);
            b = getParameter(in, namespace);
            return Operation.operator(optype, a, b);
        } else if (str.equals("isname")) { // isname <word>
            a = getParameter(in, namespace);
            return Operation.isname(a, namespace);
        } else if (str.equals("isempty")) { // isempty <word|list> : 返回word或list是否是空
            a = getParameter(in, namespace);
            return Operation.isempty(a);
        } else if (Word.isType(str)) { // 类型判断，返回true/false
            a = getParameter(in, namespace);
            switch (str) {
                case "isnumber":
                    return Operation.isnumber(a);// isnumber <value> ：返回value是否是数字
                case "isword":
                    return Operation.isword(a);// isword <value> ：返回value是否是字
                case "islist":
                    return Operation.islist(a);// islist <value> ：返回value是否是表
                case "isbool":
                    return Operation.isbool(a);// isbool <value> ：返回value是否是布尔量
                default:
                    return null;
            }
        } else if (Word.isCompare(str)) {
            // eq, gt, lt ： <operator> <number|word> <number|word>
            a = getParameter(in, namespace);
            b = getParameter(in, namespace);
            if (str.equals("eq"))
                return Operation.eq(a, b);
            else if (str.equals("gt"))
                return Operation.gt(a, b);
            else if (str.equals("lt"))
                return Operation.lt(a, b);
        } else if (str.equals("and")) {// and, or ： <operator> <bool> <bool>
            a = getParameter(in, namespace);
            b = getParameter(in, namespace);
            return Operation.and(a, b);
        } else if (str.equals("or")) {
            a = getParameter(in, namespace);
            b = getParameter(in, namespace);
            return Operation.or(a, b);
        } else if (str.equals("not")) {// not ： not <bool>
            a = getParameter(in, namespace);
            return Operation.not(a);
        } else if (Word.isExpression(str)) { // 表达式计算 +-*/%()
            // 为了⽅便识别，要求表达式的外⾯必须有括号 () 包围。
            // 中缀表达式内可以出现前缀OP调⽤
            String exp = Operation.getExpression(str, in);
            exp = Operation.adjustExpression(exp, namespace); // exp只有num和op,处理前缀op和word
            double result = Calculate.compute(exp);
            return new Value(1, String.valueOf(result));
        } else if (Word.isOp(str)) {
            return new Value(2, str);
        } else if (str.equals("read")) {// read ：返回⼀个从标准输⼊读取的数字或字
            return Operation.read(in);
        } else if (str.equals("thing")) { // print thing :b
            return Operation.thing(getParameter(in, namespace).data, namespace);
        } else if (str.equals("print")) {
            return Operation.print(getParameter(in, namespace));
        } else if (str.equals("if")) {
            // if <bool> <list1> <list2> ：如果bool为真，则执⾏list1，否则执⾏list2
            Value v1 = getParameter(in, namespace); // bool
            Value v2 = getParameter(in, namespace); // list1
            Value v3 = getParameter(in, namespace); // list2
            return Operation.doif(v1, v2, v3, namespace);
        } else if (str.equals("run")) {
            // run <list> ：运⾏list中的代码，返回list中执⾏的最后⼀个op的返回值
            Value list = getParameter(in, namespace);
            Scanner newScanner = new Scanner(list.data);
            Value v = null;
            while (newScanner.hasNext())
                v = Command.readCommand(newScanner, namespace);
            return v;
        } else if (str.equals("readlist")) {
            // readlist ：返回⼀个从标准输⼊读取的⼀⾏，构成⼀个表，⾏中每个以空格分隔的部分是list的⼀个元素，元素的类型为字
            return Operation.readList(in);
        } else if (str.equals("word")) {
            // word <word> <word|number|bool> ：将两个word合并为⼀个word，第⼆个值可以是word、number或bool
            a = getParameter(in, namespace);
            b = getParameter(in, namespace);
            return Operation.word(a, b);
        } else if (str.equals("sentence")) {
            // sentence <value1> <value2> ：将value1和value2合并成⼀个表，两个值的元素并列，value1的在value2的前⾯
            a = getParameter(in, namespace);
            b = getParameter(in, namespace);
            return Operation.sentence(a, b);
        } else if (str.equals("list")) {
            // list <value1> <value2> ：将两个值合并为⼀个表，如果值为表，则不打开这个表
            a = getParameter(in, namespace);
            b = getParameter(in, namespace);
            return Operation.list(a, b);
        } else if (str.equals("join")) {
            // join <list> <value>
            // ：将value作为list的最后⼀个元素加⼊到list中（如果value是表，则整个value成为表的最后⼀个元素）
            a = getParameter(in, namespace);
            b = getParameter(in, namespace);
            return Operation.join(a, b);
        } else if (str.equals("first")) {
            // first <word|list> ：返回word的第⼀个字符，或list的第⼀个元素
            a = getParameter(in, namespace);
            return Operation.first(a);
        } else if (str.equals("last")) {
            // last <word|list> ：返回word的最后⼀个字符，list的最后⼀个元素
            a = getParameter(in, namespace);
            return Operation.last(a);
        } else if (str.equals("butfirst")) {
            // butfirst <word|list> ：返回除第⼀个元素外剩下的表，或除第⼀个字符外剩下的字
            a = getParameter(in, namespace);
            return Operation.butfirst(a);
        } else if (str.equals("butlast")) {
            // butlast <word|list> ：返回除最后⼀个元素外剩下的表，或除最后⼀个字符外剩下的字
            a = getParameter(in, namespace);
            return Operation.butlast(a);
        } else if (str.equals("random")) {
            // random <number> ：返回[0,number)的⼀个随机数
            a = getParameter(in, namespace);
            return Operation.random(a);
        } else if (str.equals("int")) {
            // int <number> : floor the int
            a = getParameter(in, namespace);
            return Operation.Int(a);
        } else if (str.equals("sqrt")) {
            // sqrt <number> ：返回number的平⽅根
            a = getParameter(in, namespace);
            return Operation.Sqrt(a);
        } else
            return Operation.function(str, namespace, in);
        return null;
    }

}
