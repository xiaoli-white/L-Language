package ldk.l.lpm;

import cn.hutool.core.util.ZipUtil;
import ldk.l.util.option.Options;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public final class PackageManager {
    public Map<String, Map<String, Object>> listPackages() {
        Map<String, Map<String, Object>> packages = new LinkedHashMap<>();
        for (File file : Objects.requireNonNull(new File(FileUtils.getUserDirectoryPath(), ".lpm/packages").listFiles())) {
            if (!file.isDirectory())
                continue;
            Yaml yaml = new Yaml();
            Map<String, Object> map;
            try {
                map = yaml.load(Files.readString(file.toPath().resolve("manifest.yaml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            packages.put((String) map.get("name"), map);
        }
        return packages;
    }

    public void install(Options options) {
        Map<String, Object> map;
        if (options.get("local", Boolean.class)) {
            File zipFile = new File(options.args().get(1));
            Path tempDir = Paths.get(FileUtils.getTempDirectoryPath(), "lpm", "packages", zipFile.getName());
            ZipUtil.unzip(zipFile, tempDir.toFile());
            Yaml yaml = new Yaml();
            try {
                map = yaml.load(Files.readString(tempDir.resolve("manifest.yaml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Path packageDir = Paths.get(getPackagePath((String) map.get("name")));
            try {
                FileUtils.deleteDirectory(packageDir.toFile());
                Files.createDirectories(packageDir);
                FileUtils.copyDirectory(tempDir.toFile(), packageDir.toFile());
                FileUtils.deleteDirectory(tempDir.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("暂不支持远程安装");
            map = new HashMap<>();
        }
        List<?> dependencies = (List<?>) map.get("dependencies");
        if (dependencies != null) for (Object dependency : dependencies) installOnline((String) dependency);
    }

    public void uninstall(Options options) {
        String packageDir = getPackagePath(options.args().get(1));
        try {
            FileUtils.deleteDirectory(new File(packageDir));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPackagePath(String packageName) {
        return Paths.get(FileUtils.getUserDirectoryPath(), ".lpm", "packages", packageName).toString();
    }

    private void installOnline(String packageName) {
        // TODO
    }
}
