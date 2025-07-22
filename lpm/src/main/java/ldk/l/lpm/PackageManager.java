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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PackageManager {
    public List<LPackage> listPackages(Options options) {
        List<LPackage> packages = new ArrayList<>();
        for (File file : Objects.requireNonNull(new File(FileUtils.getUserDirectoryPath(), ".lpm/packages").listFiles())) {
            if (!file.isDirectory())
                continue;
            Yaml yaml = new Yaml();
            Map<String, Object> map;
            try {
                map = yaml.load(Files.readString(file.toPath().resolve("package-info.yaml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            LPackage lPackage = LPackage.fromMap(map);
            packages.add(lPackage);
        }
        return packages;
    }

    public void install(Options options) {
        if (options.get("local", Boolean.class)) {
            File zipFile = new File(options.args().get(1));
            Path tempDir = Paths.get(FileUtils.getTempDirectoryPath(), "lpm", "packages", zipFile.getName());
            ZipUtil.unzip(zipFile, tempDir.toFile());
            Yaml yaml = new Yaml();
            Map<String, Object> map;
            try {
                map = yaml.load(Files.readString(tempDir.resolve("package-info.yaml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Path packageDir = Paths.get(FileUtils.getUserDirectoryPath(), ".lpm", "packages", (String) map.get("name"));
            try {
                FileUtils.copyDirectory(tempDir.toFile(), packageDir.toFile());
                FileUtils.deleteDirectory(tempDir.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.err.println("暂不支持远程安装");
        }
    }

    public void uninstall(Options options) {
        Path packageDir = Paths.get(FileUtils.getUserDirectoryPath(), ".lpm", "packages", options.args().get(1));
        try {
            FileUtils.deleteDirectory(packageDir.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
