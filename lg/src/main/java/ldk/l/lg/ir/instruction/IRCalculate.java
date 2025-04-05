package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;

public final class IRCalculate extends IRInstruction {
    public final boolean isAtomic;
    public final Operator operator;
    public final IRType type;
    public final IROperand operand1;
    public final IROperand operand2;
    public final IRVirtualRegister target;

    public IRCalculate(Operator operator, IRType type, IROperand operand1, IROperand operand2, IRVirtualRegister target) {
        this(false, operator, type, operand1, operand2, target);
    }

    public IRCalculate(boolean isAtomic, Operator operator, IRType type, IROperand operand1, IROperand operand2, IRVirtualRegister target) {
        this.isAtomic = isAtomic;
        this.operator = operator;
        this.type = type;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitCalculate(this, additional);
    }

    @Override
    public String toString() {
        return target + " = " + (isAtomic ? "atomic_" : "") + operator.text + " " + type + " " + operand1 + ", " + operand2;
    }

    public enum Operator {
        Add("add"), Sub("sub"), Mul("mul"), Div("div"), Mod("mod"), And("and"), Or("or"), Xor("xor"), Shl("shl"), Shr("shr"), UShr("ushr");
        public final String text;

        Operator(String text) {
            this.text = text;
        }
    }
}
