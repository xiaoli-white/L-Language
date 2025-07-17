package ldk.l.lg.ir.type;

import ldk.l.lg.ir.IRVisitor;

import java.util.Objects;

public final class IRDoubleType extends IRType {
    public static final IRDoubleType INSTANCE = new IRDoubleType();

    private IRDoubleType() {
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitDoubleType(this, additional);
    }

    @Override
    public String toString() {
        return "double";
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof IRDoubleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(IRDoubleType.class);
    }
}
