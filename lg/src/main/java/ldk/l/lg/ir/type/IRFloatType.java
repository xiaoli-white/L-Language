package ldk.l.lg.ir.type;

import ldk.l.lg.ir.IRVisitor;

import java.util.Objects;

public final class IRFloatType extends IRType {
    public static final IRFloatType INSTANCE = new IRFloatType();

    private IRFloatType() {
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitFloatType(this, additional);
    }

    @Override
    public String toString() {
        return "float";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IRFloatType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(IRFloatType.class);
    }

    @Override
    public long getLength() {
        return 4;
    }
}
