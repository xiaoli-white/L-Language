package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;

public final class IRBinaryOperates extends IRInstruction{
    public final Operator operator;
    public final IRValue operand1;
    public final IRValue operand2;
    public final IRRegister target;
    public IRBinaryOperates(Operator  operator, IRValue operand1, IRValue operand2, IRRegister target) {
        this.operator = operator;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.target = target;
        target.type = operand1.getType();
        target.def = this;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitBinaryOperates(this, additional);
    }

    @Override
    public String toString() {
        return "%"+ target.name + " = " + operator.text + " "  + operand1 + ", " + operand2;
    }

    public enum Operator {
        ADD("add"),
        SUB("sub"),
        MUL("mul"),
        DIV("div"),
        MOD("mod"),
        AND("and"),
        OR("or"),
        XOR("xor"),
        SHL("shl"),
        SHR("shr"),
        USHR("ushr");
        public final String text;

        Operator(String text) {
            this.text = text;
        }
    }
}
