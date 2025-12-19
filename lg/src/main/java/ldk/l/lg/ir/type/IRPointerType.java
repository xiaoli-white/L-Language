package ldk.l.lg.ir.type;

import ldk.l.lg.ir.IRVisitor;

import java.util.Objects;

public final class IRPointerType extends IRType {
    public IRType base;

    public IRPointerType(IRType base) {
        this.base = base;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitPointerType(this, additional);
    }

    @Override
    public String toString() {
        return base.toString() + "*";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IRPointerType that)) return false;
        return Objects.equals(base, that.base);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(base);
    }

    @Override
    public long getLength() {
        return 8;
    }
}
