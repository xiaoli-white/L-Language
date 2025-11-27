package ldk.l.lg.ir.function;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.type.IRType;

public final class IRLocalVariable extends IRNode {
    public IRType type;
    public String name;
    public IRLocalVariable(IRType type, String name) {
        this.type = type;
        this.name = name;
    }
    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        return type+" "+name;
    }
}
