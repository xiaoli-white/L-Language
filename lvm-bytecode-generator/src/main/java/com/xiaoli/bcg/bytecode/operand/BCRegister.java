package com.xiaoli.bcg.bytecode.operand;

import com.xiaoli.bcg.bytecode.BCVisitor;

import java.util.Objects;

public final class BCRegister extends BCOperand {
    public byte register;
    public long virtualRegister;

    public BCRegister(byte register) {
        this.register = register;
        this.virtualRegister = -1;
    }

    public BCRegister(long virtualRegister) {
        this.register = -1;
        this.virtualRegister = virtualRegister;
    }

    public BCRegister(BCRegister register) {
        this.register = register.register;
        this.virtualRegister = register.virtualRegister;
    }

    @Override
    public Object visit(BCVisitor visitor, Object additional) {
        return visitor.visitRegister(this, additional);
    }

    @Override
    public String toString() {
        if (register == -1)
            return "v" + virtualRegister;
        else
            return "%" + register;
    }

    @Override
    public byte[] toByteCode() {
        return new byte[]{register};
    }

    @Override
    public long getLength() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof BCRegister that)) return false;
        return register == that.register && virtualRegister == that.virtualRegister;
    }

    @Override
    public int hashCode() {
        return Objects.hash(register, virtualRegister);
    }

    public static class Interval {
        public long start;
        public long end;

        public Interval(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return start + "~" + end;
        }

        public boolean overlaps(Interval other) {
            return this.start < other.end && this.end > other.start;
        }
    }
}
