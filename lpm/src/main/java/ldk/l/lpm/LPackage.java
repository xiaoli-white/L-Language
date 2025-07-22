package ldk.l.lpm;

import java.util.Map;

public record LPackage(String name, String version) {
    public static LPackage fromMap(Map<String, Object> map) {
        return new LPackage((String) map.get("name"), (String) map.get("version"));
    }
}
