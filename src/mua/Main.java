package mua;

import java.util.Scanner;

public class Main {
    public static Scanner in = new Scanner(System.in);
    public static NameSpace nameSpace = new NameSpace();

    public static void main(String[] args) {
        Command cmd = new Command();
        Value v;
        while (in.hasNext()) {
            v = Command.readCommand(in, nameSpace);
        }
    }
}