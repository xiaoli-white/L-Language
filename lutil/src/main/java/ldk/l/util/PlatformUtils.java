package ldk.l.util;

public class PlatformUtils {
    private static final String OS = System.getProperty("os.name").toLowerCase();
    private static final String ARCH = System.getProperty("os.arch").toLowerCase();

    public static boolean isWindows() {
        return OS.contains("windows");
    }

    public static boolean isMac() {
        return OS.contains("mac") || OS.contains("darwin");
    }

    public static boolean isLinux() {
        return OS.contains("linux")|| OS.contains("nix") || OS.contains("nux");
    }

    public static boolean is64Bit() {
        return ARCH.contains("64");
    }

    public static String getPlatformString() {
        if (isWindows()) {
            return "windows";
        } else if (isMac()) {
            return "mac";
        } else if (isLinux()) {
            return "linux";
        } else {
            return "unknown";
        }
    }
}