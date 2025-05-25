package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;

public final class IRNot extends IRInstruction {
    public boolean isAtomic;
    public IRType type;
    public IROperand operand;
    public IRVirtualRegister result;

    public IRNot(boolean isAtomic, IRType type, IROperand operand, IRVirtualRegister result) {
        this.isAtomic = isAtomic;
        this.type = type;
        this.operand = operand;
        this.result = result;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitNot(this, additional);
    }

    @Override
    public String toString() {
        return result + " = " + (isAtomic ? "atomic_" : "") + "not " + type + " " + operand;
    }
}
