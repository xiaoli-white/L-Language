package ldk.l.lg.ir.structure;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.type.IRType;

import java.util.Objects;

public class IRField extends IRNode {
    // public final String[] attributes;
    public final String name;
    public final IRType type;

    public IRField(String name, IRType type) {
        this.name = name;
        this.type = type;
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
