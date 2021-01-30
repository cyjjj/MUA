package mua;

public class Value {
    public int type = 0; // 1-number，2-word，3-list，4-bool
    public String data;

    public Value(int type, String data) {
        this.type = type;
        this.data = data;
    }

    public Value(Value v) {
        this.type = v.type;
        this.data = v.data;
    }

}