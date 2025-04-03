package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.type.IRType;

public class IRSet extends IRInstruction {
    public final IRType type;
    public final IROperand source;
    public final IROperand address;

    public IRSet(IRType type, IROperand source, IROperand address) {
        this.type = type;
        this.source = source;
        this.address = address;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitSet(this, additional);
    }

    @Override
    public String toString() {
        return "set " + type + ", " + source + ", " + address;
    }
}
