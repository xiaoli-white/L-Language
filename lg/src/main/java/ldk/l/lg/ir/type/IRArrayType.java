package ldk.l.lg.ir.type;

import ldk.l.lg.ir.IRVisitor;

import java.util.Objects;

public final class IRArrayType extends IRType{
    public final IRType base;
    public final long length;
    public IRArrayType(IRType base, long length) {
        this.base = base;
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IRArrayType that)) return false;
        return length == that.length && Objects.equals(base, that.base);
    }

    @Override
    public int hashCode() {
        return Objects.hash(base, length);
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitArrayType(this, additional);
    }

    @Override
    public String toString() {
        return "["+length+" x "+base+"]";
    }
}
