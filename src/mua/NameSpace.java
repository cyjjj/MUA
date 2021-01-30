package mua;

import java.util.HashMap;

public class NameSpace {
    private HashMap<String, Value> NameValueMap;

    public NameSpace() {
        NameValueMap = new HashMap<>();
        bindName("pi", new Value(1, "3.14159"));// pi ：3.14159
    }

    // 绑定，返回value
    public Value bindName(String name, Value value) {
        NameValueMap.put(name, value);
        return value;
    }

    // 清除绑定的值，返回原绑定的值
    public Value unbindName(String name) {
        if (NameValueMap.containsKey(name)) {
            Value v = NameValueMap.get(name);
            NameValueMap.remove(name);
            return v;
        }
        return null;
    }

    public Value getValue(String name) {
        // return NameValueMap.get(name);
        if (NameValueMap.get(name) != null)
            return NameValueMap.get(name); // local
        else
            return Main.nameSpace.NameValueMap.get(name); // global
    }

    public HashMap<String, Value> getNameValueMap() {
        return NameValueMap;
    }
    
    public boolean existName(String name) {
        // return NameValueMap.containsKey(name);
        return NameValueMap.containsKey(name) || Main.nameSpace.NameValueMap.containsKey(name);
    }

    public boolean isName(String data) {
        return NameValueMap.containsKey(data) || Main.nameSpace.NameValueMap.containsKey(data);
    }

    // erall ：清除当前命名空间的全部内容，返回true
    public Value erall() {
        NameValueMap.clear();
        return new Value(4, "true");
    }

    // poall ：返回当前命名空间的全部名字的list
    public Value poall() {
        String listdata = null;
        for (String name : NameValueMap.keySet()) {
            if (listdata == null)
                listdata = name;
            else
                listdata += " " + name;
        }
        return new Value(3, listdata);
    }
}