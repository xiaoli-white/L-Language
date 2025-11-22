package ldk.l.lpm;

import ldk.l.util.PlatformUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public record LGPackage(String name, String path, Map<String, Object> info) {
    public void load() {
        String key;
        if (PlatformUtils.isWindows()) {
            key = "win-dynlibs";
        } else if (PlatformUtils.isMac()) {
            key = "mac-dynlibs";
        } else if (PlatformUtils.isLinux()) {
            key = "linux-dynlibs";
        } else {
            throw new RuntimeException("Unknown platform");
        }
        List<Object> dynLibs = Collections.singletonList(info.get(key));
        for (Object dynLib : dynLibs) {
            if (dynLib == null) continue;
            System.load(Path.of(path) + File.separator + dynLib);
        }
    }
}
