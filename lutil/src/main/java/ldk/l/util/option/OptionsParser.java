package ldk.l.util.option;

import java.util.*;

public final class OptionsParser {
    private boolean skip;
    private final Map<String, OptionsParser> name2SubOptionsParser = new HashMap<>();
    private final Map<String, Type> name2Type = new HashMap<>();
    private final Map<String, String> flags2Name = new HashMap<>();
    private final Map<String, Object> defaults = new HashMap<>();
    private final Map<String, String> helps = new HashMap<>();

    public OptionsParser() {
        this(false);
    }

    public OptionsParser(boolean skip) {
        this.skip = skip;
    }

    public OptionsParser add(String name, OptionsParser subOptionsParser) {
        if (!subOptionsParser.name2SubOptionsParser.isEmpty())
            throw new IllegalArgumentException("Sub options parser is not empty");
        name2SubOptionsParser.put(name, subOptionsParser);
        return this;
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

    public Options parse(List<String> args) {
        if (!name2SubOptionsParser.isEmpty()) {
            int i = 0;
            for (; i < args.size(); i++) {
                String arg = args.get(i);
                if (!arg.startsWith("-")) break;
                if (name2Type.get(arg) == Type.Boolean) {
                    if (i + 1 < args.size()) {
                        String next = args.get(i + 1);
                        if ("true".equals(next) || "false".equals(next)) ++i;
                    }
                } else {
                    ++i;
                }
            }
            OptionsParser subOptionsParser = name2SubOptionsParser.get(args.get(i));
            subOptionsParser.skip = skip;
            subOptionsParser.name2Type.putAll(name2Type);
            subOptionsParser.flags2Name.putAll(flags2Name);
            subOptionsParser.defaults.putAll(defaults);
            subOptionsParser.helps.putAll(helps);
            return name2SubOptionsParser.get(args.get(i)).parse(args);
        } else {
            Map<String, Object> options = new HashMap<>();
            List<String> others = new ArrayList<>();
            int i = 0;
            for (; i < args.size(); i++) {
                String arg = args.get(i);
                if (!arg.startsWith("-")) {
                    others.add(arg);
                    continue;
                }
                int assignIndex = arg.indexOf('=');
                String value;
                if (assignIndex != -1) {
                    String original = arg;
                    arg = original.substring(0, assignIndex);
                    value = original.substring(assignIndex + 1);
                } else {
                    value = i + 1 < args.size() ? args.get(++i) : null;
                }
                String name = flags2Name.get(arg);
                Type type = name2Type.get(name);
                if (type == Type.Boolean) {
                    options.put(name, "true".equals(value));
                } else {
                    if (value == null) throw new IllegalArgumentException("Missing value for option: " + arg);
                    switch (type) {
                        case String -> options.put(name, value);
                        case Integer -> options.put(name, Long.parseLong(value));
                        case Decimal -> options.put(name, Double.parseDouble(value));
                    }
                }
                if (skip) break;
            }
            for (; i < args.size(); i++) others.add(args.get(i));
            defaults.forEach((name, value) -> {
                if (!options.containsKey(name)) options.put(name, value);
            });
            return new Options(options, others, helps);
        }
    }
}
