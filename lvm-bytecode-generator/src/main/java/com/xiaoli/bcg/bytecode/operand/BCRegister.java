package com.xiaoli.bcg.bytecode.operand;

import com.xiaoli.bcg.bytecode.BCVisitor;

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
