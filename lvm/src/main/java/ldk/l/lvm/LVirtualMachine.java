package ldk.l.lvm;

import ldk.l.lvm.module.Module;
import ldk.l.lvm.vm.VirtualMachine;
import ldk.l.util.option.Options;
import ldk.l.util.option.OptionsParser;
import ldk.l.util.option.Type;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public final class LVirtualMachine {
    public static final long DEFAULT_STACK_SIZE = 4 * 1024 * 1024;

    public static void main(String[] args) {
        Options options = LVirtualMachine.getOptionsParser().parse(List.of(args));
        VirtualMachine virtualMachine = new VirtualMachine(options.get("stackSize", Long.class));
        String file = options.args().getFirst();
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
                .add(List.of("--help", "-h"), "help", Type.Boolean, false)
                .add(List.of("--version", "-v"), "version", Type.Boolean, false)
                .add(List.of("--verbose", "-verbose"), "verbose", Type.Boolean, false)
                .add(List.of("--stackSize", "--s"), "stackSize", Type.Integer, DEFAULT_STACK_SIZE);
    }
}