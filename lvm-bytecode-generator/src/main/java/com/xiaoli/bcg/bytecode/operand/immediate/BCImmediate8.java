package com.xiaoli.bcg.bytecode.operand.immediate;

import com.xiaoli.bcg.bytecode.BCVisitor;

public final class BCImmediate8 extends BCImmediate {
    public long value;

    public BCImmediate8(long value) {
        this(value, null);
    }

    public BCImmediate8(long value, String comment) {
        super(comment);
        this.value = value;
    }

    @Override
    public Object visit(BCVisitor visitor, Object additional) {
        return visitor.visitImmediate8(this, additional);
    }

    @Override
    public String toString() {
        return value + (comment != null ? "(" + comment + ")" : "");
    }

    @Override
    public byte[] toByteCode() {
        return new byte[]{(byte) value, (byte) (value >> 8), (byte) (value >> 16), (byte) (value >> 24), (byte) (value >> 32), (byte) (value >> 40), (byte) (value >> 48), (byte) (value >> 56)};
    }

    @Override
    public long getLength() {
        return 8;
    }
}
