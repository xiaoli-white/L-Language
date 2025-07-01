package ldk.l.lpm.pass;

import ldk.l.lg.ir.IRModule;
import ldk.l.util.option.Options;

public abstract class AbstractPass {
    public abstract void run(IRModule module, Options options);
}
