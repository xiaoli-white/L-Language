package ldk.l.lc.util;

import java.util.HashMap;

public class Context {
    private final HashMap<String, Object> name2Value = new HashMap<>();

    public Context add(String name, Object value) {
        this.name2Value.put(name, value);
        return this;
    }

    public Object get(String name) {
        return this.name2Value.get(name);
    }

    public boolean contains(String name) {
        return this.name2Value.containsKey(name);
    }

    public Context remove(String name) {
        this.name2Value.remove(name);
        return this;
    }
}
