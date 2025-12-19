package ldk.l.lg.ir.type;

import ldk.l.lg.ir.IRVisitor;

import java.util.Objects;

public final class IRVoidType extends IRType {
    public static final IRVoidType INSTANCE = new IRVoidType();

    private IRVoidType() {
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IRVoidType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(IRVoidType.class);
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitVoidType(this, additional);
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public long getLength() {
        return 0;
    }
}
