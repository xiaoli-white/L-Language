package ldk.l.lpm;

import ldk.l.lg.ir.IRModule;
import ldk.l.lpm.pass.AbstractPass;
import ldk.l.util.option.Options;

import java.util.LinkedList;
import java.util.List;

public final class PassManager {
    private final List<AbstractPass> passes = new LinkedList<>();
    public PassManager addPass(AbstractPass pass){
        passes.add(pass);
        return this;
    }
    public void run(IRModule module, Options options) {
        for (AbstractPass pass : passes) {
            pass.run(module,options);
        }
    }
}
