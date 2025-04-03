package ldk.l.util.option;

import java.util.ArrayList;
import java.util.HashMap;

public class Options {
    public final HashMap<String, Options.VarType> varTypes = new HashMap<>();
    public final HashMap<String, Boolean> booleanVars = new HashMap<>();
    public final HashMap<String, String> stringVars = new HashMap<>();
    public final HashMap<String, Long> intVars = new HashMap<>();
    public final HashMap<String, Double> floatVars = new HashMap<>();
    public final ArrayList<String> args = new ArrayList<>();

    public Options setVar(String name, boolean value) {
        return this.setBooleanVar(name, value);
    }

    public Options setVar(String name, String value) {
        return this.setStringVar(name, value);
    }

    public Options setVar(String name, long value) {
        return this.setIntVar(name, value);
    }

    public Options setVar(String name, double value) {
        return this.setFloatVar(name, value);
    }

    public Options setBooleanVar(String name, boolean value) {
        this.varTypes.put(name, VarType.Boolean);
        this.booleanVars.put(name, value);
        return this;
    }

    public Options setStringVar(String name, String value) {
        this.varTypes.put(name, VarType.String);
        this.stringVars.put(name, value);
        return this;
    }

    public Options setIntVar(String name, long value) {
        this.varTypes.put(name, VarType.Int);
        this.intVars.put(name, value);
        return this;
    }

    public Options setFloatVar(String name, double value) {
        this.varTypes.put(name, VarType.Float);
        this.floatVars.put(name, value);
        return this;
    }

    public boolean containsVar(String name) {
        return this.varTypes.containsKey(name);
    }

    public String[] getArgs() {
        return this.args.toArray(new String[0]);
    }

    public boolean getBooleanVar(String name) {
        return this.booleanVars.get(name);
    }

    public String getStringVar(String name) {
        return this.stringVars.get(name);
    }

    public long getIntVar(String name) {
        return this.intVars.get(name);
    }

    public double getFloatVar(String name) {
        return this.floatVars.get(name);
    }

    public enum VarType {
        Boolean,
        String,
        Int,
        Float,
    }
}
