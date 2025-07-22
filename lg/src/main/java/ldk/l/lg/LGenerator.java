package ldk.l.lg;

import ldk.l.util.option.OptionsParser;
import ldk.l.util.option.Type;

import java.util.List;

public class LGenerator {
    public static void main(String[] args) {
        LGenerator.getOptionsParser().parse(List.of(args));
    }

    public static OptionsParser getOptionsParser() {
        return new OptionsParser()
                .add(List.of("--help", "-h"), "help", Type.Boolean, false)
                .add(List.of("--version", "-v"), "version", Type.Boolean, false)
                .add(List.of("--verbose", "-verbose"), "verbose", Type.Boolean, false)
                .add(List.of("--output", "-o"), "output", Type.String, "a.out")
                .add(List.of("--platform"), "platform", Type.String, "lvm");
    }
}
