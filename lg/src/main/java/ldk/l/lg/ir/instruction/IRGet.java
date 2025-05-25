package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;

public final class IRGet extends IRInstruction {
    public final IRType type;
    public final IROperand address;
    public final IRVirtualRegister target;

    public IRGet(IRType type, IROperand address, IRVirtualRegister target) {
        this.type = type;
        this.address = address;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitGet(this, additional);
    }

    @Override
    public String toString() {
        return target + " = get " + type + " " + address;
    }
}
