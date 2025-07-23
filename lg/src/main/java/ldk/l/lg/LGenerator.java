package ldk.l.lg;

import ldk.l.lg.ir.IRModule;
import ldk.l.lpm.PackageManager;
import ldk.l.util.option.Options;
import ldk.l.util.option.OptionsParser;
import ldk.l.util.option.Type;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class LGenerator {
    public static void main(String[] args) {
        Options options = LGenerator.getOptionsParser().parse(List.of(args));
    }

    public static OptionsParser getOptionsParser() {
        return new OptionsParser()
                .add(List.of("--help", "-h"), "help", Type.Boolean, false)
                .add(List.of("--version", "-v"), "version", Type.Boolean, false)
                .add(List.of("--verbose", "-verbose"), "verbose", Type.Boolean, false)
                .add(List.of("--platform"), "platform", Type.String, "lvm")
                .add(List.of("--output", "-o"), "output", Type.String, "");
    }

    public static void generate(IRModule irModule, Options options) {
        String platform = options.get("platform", String.class);
        PackageManager packageManager = new PackageManager();
        Map<String, Map<String, Object>> packages = packageManager.listPackages();
        Map<String, Object> packageInfo = packages.values().stream().filter(map -> "lg-plugin".equals(map.get("type"))).filter(map -> ((List<?>) map.get("platforms")).contains(platform)).toList().getFirst();
        String jarFilePath = Paths.get(packageManager.getPackagePath((String) packageInfo.get("name")), (String) packageInfo.get("main-jar")).toString();
        URL jarUrl;
        try {
            jarUrl = new File(jarFilePath).toURI().toURL();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarUrl}, LGenerator.class.getClassLoader())) {
            Class<?> clazz = classLoader.loadClass((String) packageInfo.get("main-class"));
            Generator generator = (Generator) clazz.getDeclaredConstructor().newInstance();
            generator.generate(irModule, options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
