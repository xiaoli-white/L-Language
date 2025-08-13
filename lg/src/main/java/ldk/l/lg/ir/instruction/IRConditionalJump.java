package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRCondition;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.type.IRType;

public final class IRConditionalJump extends IRInstruction {
    public final boolean isAtomic;
    public final IRType type;
    public final IRCondition condition;
    public final IROperand operand1;
    public final IROperand operand2;
    public String target;

    public IRConditionalJump(IRType type, IRCondition condition, IROperand operand, String target) {
        this(false, type, condition, operand, null, target);
    }

    public IRConditionalJump(IRType type, IRCondition condition, IROperand operand1, IROperand operand2, String target) {
        this(false, type, condition, operand1, operand2, target);
    }

    public IRConditionalJump(boolean isAtomic, IRType type, IRCondition condition, IROperand operand1, IROperand operand2, String target) {
        this.isAtomic = isAtomic;
        this.type = type;
        this.condition = condition;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.target = target;
    }

    @Override

    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitConditionalJump(this, additional);
    }

    @Override
    public String toString() {
        return (isAtomic ? "atomic_" : "") + "conditional_jump " + type + " " + condition.text + ", " + operand1 + (operand2 != null ? ", " + operand2 : "") + ", #" + target;
    }
}
