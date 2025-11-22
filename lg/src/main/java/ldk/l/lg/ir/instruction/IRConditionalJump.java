package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.base.IRCondition;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRValue;

public final class IRConditionalJump extends IRInstruction {
    @Deprecated
    public final boolean isAtomic;
    public final IRType type;
    public final IRCondition condition;
    public final IRValue operand1;
    public final IRValue operand2;
    public final IRBasicBlock target;
    @Deprecated
    public final IROperand ooperand1;
    @Deprecated
    public final IROperand ooperand2;
    @Deprecated
    public String ttarget;

    @Deprecated
    public IRConditionalJump(IRType type, IRCondition condition, IROperand operand, String target) {
        this(false, type, condition, operand, null, target);
    }

    @Deprecated
    public IRConditionalJump(IRType type, IRCondition condition, IROperand operand1, IROperand operand2, String target) {
        this(false, type, condition, operand1, operand2, target);
    }
@Deprecated
    public IRConditionalJump(boolean isAtomic, IRType type, IRCondition condition, IROperand operand1, IROperand operand2, String target) {
        this.isAtomic = isAtomic;
        this.type = type;
        this.condition = condition;
        this.ooperand1 = operand1;
        this.ooperand2 = operand2;
        this.ttarget = target;
        this.operand1 = null;
        this.operand2 = null;
        this.target = null;
    }
    public IRConditionalJump(IRCondition condition, IRValue operand, IRBasicBlock target) {
        this(condition, operand,null, target);
    }
    public IRConditionalJump(IRCondition condition, IRValue operand1, IRValue operand2, IRBasicBlock target) {
        this.type = null;
        this.condition = condition;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.target = target;
        this.ttarget = null;
        this.ooperand1 = null;
        this.ooperand2 = null;
        this.isAtomic = false;
    }

    @Override

    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitConditionalJump(this, additional);
    }

    @Override
    public String toString() {
        return "conditional_jump " + condition.text + ", " + operand1 + (operand2 != null ? ", " + operand2 : "") + ", label " + target.name;
    }
}
