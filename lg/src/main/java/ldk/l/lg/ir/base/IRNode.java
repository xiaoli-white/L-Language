package ldk.l.lg.ir.base;

import ldk.l.lg.ir.IRVisitor;

public abstract class IRNode {
    public abstract Object accept(IRVisitor visitor, Object additional);

    @Override
    public abstract String toString();
}