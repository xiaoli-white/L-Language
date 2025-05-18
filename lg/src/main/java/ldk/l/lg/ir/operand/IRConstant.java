package ldk.l.lg.ir.operand;

import ldk.l.lg.ir.IRVisitor;

public final class IRConstant extends IROperand {
    public final int index;

    public IRConstant(int index) {
        this.index = index;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitConstant(this, additional);
    }

    @Override
    public String toString() {
        return "$" + index;
    }
}
