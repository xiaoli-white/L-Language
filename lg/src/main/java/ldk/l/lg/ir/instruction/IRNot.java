package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;

public final class IRNot extends IRInstruction {
    public final boolean isAtomic;
    public final IRType type;
    public final IROperand operand;
    public final IRVirtualRegister target;

    public IRNot(boolean isAtomic, IRType type, IROperand operand, IRVirtualRegister target) {
        this.isAtomic = isAtomic;
        this.type = type;
        this.operand = operand;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitNot(this, additional);
    }

    @Override
    public String toString() {
        return target + " = " + (isAtomic ? "atomic_" : "") + "not " + type + " " + operand;
    }
}
