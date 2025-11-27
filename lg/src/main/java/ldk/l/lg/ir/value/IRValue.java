package ldk.l.lg.ir.value;

import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.type.IRType;

public abstract class IRValue extends IRNode{
    public abstract IRType getType();
}
