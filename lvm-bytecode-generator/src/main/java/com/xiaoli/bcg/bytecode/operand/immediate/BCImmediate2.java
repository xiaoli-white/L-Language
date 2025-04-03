package com.xiaoli.bcg.bytecode.operand.immediate;

import com.xiaoli.bcg.bytecode.BCVisitor;

public class BCImmediate2 extends BCImmediate {
    public short value;

    public BCImmediate2(short value) {
        this(value, null);
    }

    public BCImmediate2(short value, String comment) {
        super(comment);
        this.value = value;
    }

    @Override
    public Object visit(BCVisitor visitor, Object additional) {
        return visitor.visitImmediate2(this, additional);
    }

    @Override
    public String toString() {
        return value + (comment != null ? "(" + comment + ")" : "");
    }

    @Override
    public byte[] toByteCode() {
        return new byte[]{(byte) value, (byte) (value >> 8)};
    }

    @Override
    public long getLength() {
        return 2;
    }
}
