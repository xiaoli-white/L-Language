package ldk.l.lg.ir.structure;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.type.IRType;

public final class IRField extends IRNode {
    // public final String[] attributes;
    public IRType type;
    public String name;

    public IRField(IRType type, String name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitField(this, additional);
    }

    @Override
    public String toString() {
        return "IRField{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
