package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;

public final class IRUnaryOperates extends IRInstruction{
    public final Operator operator;
    public final IRValue operand;
    public final IRRegister target;
    public IRUnaryOperates(Operator operator, IRValue operand, IRRegister target) {
        this.operator = operator;
        this.operand = operand;
        this.target = target;
        target.def = this;
        if (operator == Operator.INC || operator == Operator.DEC) {
            target.type = ((IRPointerType)operand.getType()).base;
        } else {
            target.type = operand.getType();
        }
    }
    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitUnaryOperates(this, additional);
    }

    @Override
    public String toString() {
        return "%"+target.name+" = "+operator.text+" "+operand;
    }
    public enum Operator{
        INC("inc"),
        DEC("dec"),
        NEGATE("neg"),
        NOT("not");
        public final String text;
        Operator(String text) {
            this.text = text;
        }
    }
}
