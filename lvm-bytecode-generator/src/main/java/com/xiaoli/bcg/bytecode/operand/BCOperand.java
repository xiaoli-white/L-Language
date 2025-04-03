package com.xiaoli.bcg.bytecode.operand;

import com.xiaoli.bcg.bytecode.BCVisitor;

public abstract class BCOperand {
    public abstract Object visit(BCVisitor visitor, Object additional);

    @Override
    public abstract String toString();

    public abstract byte[] toByteCode();

    public abstract long getLength();
}
