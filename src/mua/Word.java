package mua;

public class Word {
    // type judge
    public static int typeJudge(String str) {
        String reg = "^-?[0-9]+(.[0-9]+)?$";
        if (str.matches(reg))
            return 1; // number
        else if (str.charAt(0) == '\"')
            return 2; // word
        else if (str.charAt(0) == '[')
            return 3; // list
        else if (str.equals("true") || str.equals("false"))
            return 4; // bool
        return 0;
    }

    // :<name>
    public static boolean isBindValue(String str) {
        return str.charAt(0) == ':';
    }

    // operator : add, sub, mul, div, mod
    public static boolean isOperator(String str) {
        return (str.equals("add") || str.equals("sub") || str.equals("mul") || str.equals("div") || str.equals("mod"));
    }

    public static boolean isExpression(String str) {
        return str.charAt(0) == '(';
    }

    // isnumber, isword, islist, isbool
    public static boolean isType(String str) {
        return (str.equals("isnumber") || str.equals("isword") || str.equals("islist") || str.equals("isbool"));
    }

    // eq, gt, lt
    public static boolean isCompare(String str) {
        return (str.equals("eq") || str.equals("gt") || str.equals("lt"));
    }

    public static boolean isOp(String s) {
        String match = "[\\+\\-\\*\\/\\(\\)\\%]";
        return s.matches(match);
    }
}
