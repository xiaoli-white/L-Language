package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRCondition;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;

public final class IRCompare extends IRInstruction {
    @Deprecated
    public final boolean isAtomic;
    @Deprecated
    public final IRType type;
    public IRCondition condition;
    @Deprecated
    public final IROperand ooperand1;
    @Deprecated
    public final IROperand ooperand2;
    @Deprecated
    public final IRVirtualRegister ttarget;
    public IRValue operand1;
    public IRValue operand2;
    public IRRegister target;

    @Deprecated
    public IRCompare(IRType type, IRCondition condition, IROperand operand1, IROperand operand2, IRVirtualRegister target) {
        this(false, type, condition, operand1, operand2, target);
    }
@Deprecated
    public IRCompare(boolean isAtomic, IRType type, IRCondition condition, IROperand operand1, IROperand operand2, IRVirtualRegister target) {
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
    public IRCompare(IRCondition condition, IRValue operand1, IRValue operand2, IRRegister target) {
        this.type = null;
        this.isAtomic = false;
        this.condition = condition;
        this.ooperand1 = null;
        this.ooperand2 = null;
        this.ttarget = null;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.target = target;
        target.type = IRType.getBooleanType();
        target.def = this;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitCompare(this, additional);
    }

    @Override
    public String toString() {
        return "%"+ target.name + " = cmp " + condition.text + ", " + operand1 + ", " + operand2;
    }
}
