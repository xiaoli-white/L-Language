package ldk.l.lg.ir.type;

import ldk.l.lg.ir.IRVisitor;

import java.util.Objects;

public final class IRIntegerType extends IRType {
    public Size size;
    public boolean unsigned;

    public IRIntegerType(Size size, boolean unsigned) {
        this.size = size;
        this.unsigned = unsigned;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitIntegerType(this, additional);
    }

    @Override
    public String toString() {
        return (unsigned ? "u" : "i") + size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IRIntegerType that)) return false;
        return size == that.size && unsigned == that.unsigned;
    }

    @Override
    public int hashCode() {
        return Objects.hash(size, unsigned);
    }

    @Override
    public long getLength() {
        if (size == Size.OneBit) return 1;
        return size.size / 8;
    }

    public enum Size {
        OneBit(1),
        OneByte(8),
        TwoBytes(16),
        FourBytes(32),
        EightBytes(64);
        public final long size;

        Size(long size) {
            this.size = size;
        }

        @Override
        public String toString() {
            return Long.toString(size);
        }
    }
}
