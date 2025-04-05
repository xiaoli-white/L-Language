package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.type.IRType;

public class IRSet extends IRInstruction {
    public final IRType type;
    public final IROperand address;
    public final IROperand source;

    public IRSet(IRType type, IROperand address, IROperand source) {
        this.type = type;
        this.address = address;
        this.source = source;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitSet(this, additional);
    }

    @Override
    public String toString() {
        return "set " + type + ", " + address + ", " + source;
    }
}
