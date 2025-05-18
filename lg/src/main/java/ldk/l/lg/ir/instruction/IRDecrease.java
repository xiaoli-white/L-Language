package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;

public final class IRDecrease extends IRInstruction {
    public final IRType type;
    public final IROperand operand;
    public final IRVirtualRegister target;

    // Atomic
    public IRDecrease(IRType type, IROperand operand) {
        this(type, operand, null);
    }

    // Non-atomic
    public IRDecrease(IRType type, IROperand operand, IRVirtualRegister target) {
        this.type = type;
        this.operand = operand;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitDecrease(this, additional);
    }

    @Override
    public String toString() {
        if (isAtomic())
            return "atomic_decrease " + type + " " + operand;
        else
            return target + " = decrease " + type + " " + operand;
    }

    public boolean isAtomic() {
        return target == null;
    }
}
