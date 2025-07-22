package ldk.l.lpm;

import ldk.l.util.option.Options;
import ldk.l.util.option.OptionsParser;
import ldk.l.util.option.Type;

import java.util.List;

public class LPackageManager {
    public static void main(String[] args) {
        OptionsParser optionsParser = getOptionsParser();
        Options options = optionsParser.parse(List.of(args));
        if (options.args().isEmpty()) {

        } else {
            PackageManager packageManager = new PackageManager();
            switch (options.args().getFirst()) {
                case "install" -> packageManager.install(options);
                case "uninstall" -> packageManager.uninstall(options);
            }
        }
    }

    public static OptionsParser getOptionsParser() {
        return new OptionsParser()
                .add(List.of("--help", "-h"), "help", Type.Boolean, false)
                .add(List.of("--version", "-v"), "version", Type.Boolean, false)
                .add(List.of("--verbose", "-verbose"), "verbose", Type.Boolean, false)
                .add("install",new OptionsParser().add(List.of("--local"), "local", Type.Boolean, false));
    }
}
