package ldk.l.lg;

import ldk.l.lg.ir.IRModule;
import ldk.l.util.option.Options;

public abstract class AbstractIROptimizer {
    public abstract void optimize(IRModule module, Options options);
}
