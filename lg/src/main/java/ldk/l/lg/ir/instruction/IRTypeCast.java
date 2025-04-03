package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;

public class IRTypeCast extends IRInstruction {
    public final Kind kind;
    public final IRType originalType;
    public final IROperand source;
    public final IRType targetType;
    public final IRVirtualRegister target;

    public IRTypeCast(Kind kind, IRType originalType, IROperand source, IRType targetType, IRVirtualRegister target) {
        this.kind = kind;
        this.originalType = originalType;
        this.source = source;
        this.targetType = targetType;
        this.target = target;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitTypeCast(this, additional);
    }

    @Override
    public String toString() {
        return target + " = " + kind.name + " " + originalType + " " + source + " to " + targetType;
    }

    public enum Kind {
        ZeroExtend("zext"),
        SignExtend("sext"),
        IntToFloat("itof"),
        FloatToInt("ftoi"),
        FloatExtend("fext");
        public final String name;

        Kind(String name) {
            this.name = name;
        }
    }
}
