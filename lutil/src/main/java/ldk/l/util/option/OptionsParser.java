package ldk.l.util.option;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class OptionsParser {
    private final boolean skip;
    private final Map<String, String> flags2Name = new HashMap<>();
    private final Map<String, Type> name2Type = new HashMap<>();
    private final Map<String, Object> defaults = new HashMap<>();
    private final Map<String, String> helps = new HashMap<>();

    public OptionsParser() {
        this(false);
    }

    public OptionsParser(boolean skip) {
        this.skip = skip;
    }

    public OptionsParser add(List<String> flags, String name, Type type) {
        return add(flags, name, type, null, null);
    }

    public OptionsParser add(List<String> flags, String name, Type type, Object defaultValue) {
        return add(flags, name, type, defaultValue, null);
    }

    public OptionsParser add(List<String> flags, String name, Type type, Object defaultValue, String help) {
        for (String flag : flags) {
            if (!flag.startsWith("-")) throw new IllegalArgumentException("Invalid flag: " + flag);
            flags2Name.put(flag, name);
        }
        name2Type.put(name, type);
        defaults.put(name, defaultValue);
        helps.put(name, help);
        return this;
    }

    public Options parse(String[] args) {
        Map<String, Object> options = new HashMap<>();
        List<String> others = new ArrayList<>();
        int i = 0;
        for (; i < args.length; i++) {
            String arg = args[i];
            if (!flags2Name.containsKey(arg)) {
                others.add(arg);
                continue;
            }
            String name = flags2Name.get(arg);
            Type type = name2Type.get(name);
            switch (type) {
                case Boolean -> {
                    if (i + 1 < args.length) {
                        String next = args[i + 1];
                        if ("true".equals(next) || "false".equals(next)) {
                            options.put(name, Boolean.parseBoolean(next));
                            i++;
                            break;
                        }
                    }
                    options.put(name, true);
                }
                case String -> {
                    if (i + 1 < args.length)
                        options.put(name, args[++i]);
                    else
                        throw new IllegalArgumentException("Missing argument for " + name);

                }
                case Integer -> {
                    if (i + 1 < args.length)
                        options.put(name, Long.parseLong(args[++i]));
                    else
                        throw new IllegalArgumentException("Missing argument for " + name);
                }
                case Decimal -> {
                    if (i + 1 < args.length)
                        options.put(name, Double.parseDouble(args[++i]));
                    else
                        throw new IllegalArgumentException("Missing argument for " + name);
                }
            }
            if (skip) break;
        }
        for (; i < args.length; i++) others.add(args[i]);
        defaults.forEach((name, value) -> {
            if (!options.containsKey(name)) options.put(name, value);
        });
        return new Options(options, others,  helps);
    }
}
