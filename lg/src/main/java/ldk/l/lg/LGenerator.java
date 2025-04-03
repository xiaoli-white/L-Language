package ldk.l.lg;

import ldk.l.util.option.OptionsParser;

public class LGenerator {
    public static void main(String[] args) {
        LGenerator.getOptionsParser().parse(args);
    }

    public static OptionsParser getOptionsParser() {
        return new OptionsParser()
                .addVar("help", "--help", false).addVar("help", "-h", false)
                .addVar("version", "--version", false).addBooleanVar("version", "-version", false)
                .addVar("verbose", "--verbose", false).addVar("verbose", "-verbose", false);
    }
}
