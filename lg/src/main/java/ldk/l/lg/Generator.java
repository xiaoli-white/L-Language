package ldk.l.lg;

import ldk.l.lg.ir.IRModule;
import ldk.l.util.option.Options;

public abstract class Generator {
    public abstract void generate(IRModule module, Options options);
}
