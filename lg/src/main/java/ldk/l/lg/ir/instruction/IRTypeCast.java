package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;

public final class IRTypeCast extends IRInstruction {
    public Kind kind;
    @Deprecated
    public final IRType originalType;
    @Deprecated
    public final IROperand ssource;
    @Deprecated
    public final IRVirtualRegister ttarget;
    public IRValue source;
    public IRType targetType;
    public IRRegister target;

    @Deprecated
    public IRTypeCast(Kind kind, IRType originalType, IROperand source, IRType targetType, IRVirtualRegister target) {
        this.kind = kind;
        this.originalType = originalType;
        this.ssource = source;
        this.targetType = targetType;
        this.ttarget = target;
        this.source = null;
        this.target = null;
    }

    public IRTypeCast(Kind kind, IRValue source, IRType targetType, IRRegister target) {
        this.kind = kind;
        this.originalType = null;
        this.ssource = null;
        this.ttarget = null;
        this.source = source;
        this.targetType = targetType;
        this.target = target;
        target.def = this;
        target.type = targetType;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitTypeCast(this, additional);
    }

    @Override
    public String toString() {
        return "%"+target.name + " = " + kind.name + " "  + source + " to " + targetType;
    }

    public enum Kind {
        ZeroExtend("zext"),
        SignExtend("sext"),
        Truncate("trunc"),
        IntToFloat("itof"),
        FloatToInt("ftoi"),
        IntToPtr("itop"),
        PtrToInt("ptoi"),
        PtrToPtr("ptop"),
        FloatExtend("fext"),
        FloatTruncate("ftrunc");
        public final String name;

        Kind(String name) {
            this.name = name;
        }
    }
}
