package ldk.l.lvm;

import ldk.l.lvm.module.Module;
import ldk.l.lvm.vm.VirtualMachine;
import ldk.l.util.option.Options;
import ldk.l.util.option.OptionsParser;

import java.io.FileInputStream;
import java.io.IOException;

public final class LVirtualMachine {
    public static final long DEFAULT_STACK_SIZE = 4 * 1024 * 1024;

    public static void main(String[] args) {
        Options options = LVirtualMachine.getOptionsParser().parse(OptionsParser.OptionsParserMode.Skip, args);
        VirtualMachine virtualMachine = new VirtualMachine(options.getIntVar("stackSize"));
        String file = options.getArgs()[0];
        byte[] raw;
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            raw = fileInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Module module = Module.fromRaw(raw);
        int init = virtualMachine.init(module);
        int ret = virtualMachine.run();
    }

    public static OptionsParser getOptionsParser() {
        return new OptionsParser()
                .addVar("help", "--help", false).addVar("help", "-h", false)
                .addVar("version", "--version", false).addBooleanVar("version", "-version", false)
                .addVar("verbose", "--verbose", false).addVar("verbose", "-verbose", false)
                .addVar("stackSize", "--stackSize", DEFAULT_STACK_SIZE).addVar("stackSize", "-ss", DEFAULT_STACK_SIZE);
    }
}