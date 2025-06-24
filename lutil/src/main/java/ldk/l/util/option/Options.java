package ldk.l.util.option;

import java.util.List;
import java.util.Map;

public record Options(Map<String, Object> options, List<String> args, Map<String, String> helps) {
    public boolean contains(String name) {
        return options.containsKey(name);
    }

    public <T> T get(String name, Class<T> clazz) {
        return clazz.cast(options.get(name));
    }
}
