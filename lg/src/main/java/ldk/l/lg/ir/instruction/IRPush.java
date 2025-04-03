package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.type.IRType;

public class IRPush extends IRInstruction {
    public final IRType type;
    public final IROperand operand;

    public IRPush(IRType type, IROperand operand) {
        this.type = type;
        this.operand = operand;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitPush(this, additional);
    }

    @Override
    public String toString() {
        return "push " + type + " " + operand;
    }
}
