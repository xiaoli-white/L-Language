package ldk.l.util.option;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class OptionsParser {
    public final HashMap<String, String> varName2Name = new HashMap<>();
    public final HashMap<String, Options.VarType> varTypes = new HashMap<>();
    public final HashMap<String, Boolean> booleanVars = new HashMap<>();
    public final HashMap<String, String> stringVars = new HashMap<>();
    public final HashMap<String, Long> intVars = new HashMap<>();
    public final HashMap<String, Double> floatVars = new HashMap<>();

    public OptionsParser addVar(String name, String varName, boolean _default) {
        return this.addBooleanVar(name, varName, _default);
    }

    public OptionsParser addVar(String name, String varName, String _default) {
        return this.addStringVar(name, varName, _default);
    }

    public OptionsParser addVar(String name, String varName, long _default) {
        return this.addIntVar(name, varName, _default);
    }

    public OptionsParser addVar(String name, String varName, double _default) {
        return this.addFloatVar(name, varName, _default);
    }

    public OptionsParser addBooleanVar(String name, String varName, boolean _default) {
        this.checkVarName(varName);

        this.varName2Name.put(varName, name);
        this.varTypes.put(varName, Options.VarType.Boolean);
        this.booleanVars.put(varName, _default);
        return this;
    }

    public OptionsParser addStringVar(String name, String varName, String _default) {
        this.checkVarName(varName);

        this.varName2Name.put(varName, name);
        this.varTypes.put(varName, Options.VarType.String);
        this.stringVars.put(varName, _default);
        return this;
    }

    public OptionsParser addIntVar(String name, String varName, long _default) {
        this.checkVarName(varName);

        this.varName2Name.put(varName, name);
        this.varTypes.put(varName, Options.VarType.Int);
        this.intVars.put(varName, _default);
        return this;
    }

    public OptionsParser addFloatVar(String name, String varName, double _default) {
        this.checkVarName(varName);

        this.varName2Name.put(varName, name);
        this.varTypes.put(varName, Options.VarType.Float);
        this.floatVars.put(varName, _default);
        return this;
    }

    public OptionsParser reset() {
        this.varTypes.clear();
        this.booleanVars.clear();
        this.stringVars.clear();
        this.intVars.clear();
        this.floatVars.clear();
        return this;
    }

    public Options parse(String[] args) {
        return this.parse(OptionsParserMode.Normal, args);
    }

    public Options parse(OptionsParserMode mode, String[] args) {
        Options options = new Options();

        Map<String, Options.VarType> additionalVarsKeys = varTypes.entrySet().stream()
                .filter(entry -> entry.getValue() != Options.VarType.Boolean)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        Set<String> booleanVarsKeys = varTypes.entrySet().stream()
                .filter(entry -> entry.getValue() == Options.VarType.Boolean)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        int i;
        for (i = 0; i < args.length; i++) {
            String arg = args[i];

            if (booleanVarsKeys.contains(arg)) {
                if (i + 1 < args.length) {
                    if (args[i + 1].equals("true")) {
                        options.setVar(this.varName2Name.get(arg), true);
                        i++;
                        continue;
                    } else if (args[i + 1].equals("false")) {
                        options.setVar(this.varName2Name.get(arg), false);
                        i++;
                        continue;
                    }
                }
                options.setVar(this.varName2Name.get(arg), true);
            } else if (additionalVarsKeys.containsKey(arg)) {
                try {
                    switch (additionalVarsKeys.get(arg)) {
                        case String -> {
                            if (i + 1 < args.length) {
                                options.setVar(this.varName2Name.get(arg), args[++i]);
                            }
                        }
                        case Int -> {
                            if (i + 1 < args.length) {
                                options.setVar(this.varName2Name.get(arg), Long.parseLong(args[++i]));
                            }
                        }
                        case Float -> {
                            if (i + 1 < args.length) {
                                options.setVar(this.varName2Name.get(arg), Double.parseDouble(args[++i]));
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    System.err.println("格式化异常: " + e.getMessage());
                }
            } else {
                options.args.add(arg);
                if (mode == OptionsParserMode.Skip) {
                    i++;
                    break;
                }
            }
        }
        for (; i < args.length; i++) options.args.add(args[i]);
        for (Map.Entry<String, Boolean> entry : this.booleanVars.entrySet()) {
            String key = this.varName2Name.get(entry.getKey());
            if (!options.containsVar(key)) options.setVar(key, entry.getValue());
        }
        for (Map.Entry<String, String> entry : this.stringVars.entrySet()) {
            String key = this.varName2Name.get(entry.getKey());
            if (!options.containsVar(key)) options.setVar(key, entry.getValue());
        }
        for (Map.Entry<String, Long> entry : this.intVars.entrySet()) {
            String key = this.varName2Name.get(entry.getKey());
            if (!options.containsVar(key)) options.setVar(key, entry.getValue());
        }
        for (Map.Entry<String, Double> entry : this.floatVars.entrySet()) {
            String key = this.varName2Name.get(entry.getKey());
            if (!options.containsVar(key)) options.setVar(key, entry.getValue());
        }
        return options;
    }

    public void checkVarName(String varName) {
        if (this.varTypes.containsKey(varName)) {
            throw new IllegalArgumentException("Duplicate argument name: " + varName);
        }
        if (!varName.startsWith("-")) {
            throw new IllegalArgumentException("Invalid argument name: " + varName);
        }
    }

    public enum OptionsParserMode {
        Normal,
        Skip
    }
}
