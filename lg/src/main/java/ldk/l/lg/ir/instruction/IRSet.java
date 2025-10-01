package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.type.IRType;

public final class IRSet extends IRInstruction {
    public final IRType type;
    public final IROperand address;
    public final IROperand value;

    public IRSet(IRType type, IROperand address, IROperand value) {
        this.type = type;
        this.address = address;
        this.value = value;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitSet(this, additional);
    }

    @Override
    public String toString() {
        return "set " + type + " " + address + ", " + value;
    }
}
