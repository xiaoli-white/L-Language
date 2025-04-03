package com.xiaoli.bcg.bytecode.operand.immediate;

import com.xiaoli.bcg.bytecode.BCVisitor;

public class BCImmediate1 extends BCImmediate {
    public byte value;

    public BCImmediate1(byte value) {
        this(value, null);
    }

    public BCImmediate1(byte value, String comment) {
        super(comment);
        this.value = value;
    }

    @Override
    public Object visit(BCVisitor visitor, Object additional) {
        return visitor.visitImmediate1(this, additional);
    }

    @Override
    public String toString() {
        return value + (comment != null ? "(" + comment + ")" : "");
    }

    @Override
    public byte[] toByteCode() {
        return new byte[]{value};
    }

    @Override
    public long getLength() {
        return 1;
    }
}
