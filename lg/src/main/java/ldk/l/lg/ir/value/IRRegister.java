package ldk.l.lg.ir.value;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.instruction.IRInstruction;
import ldk.l.lg.ir.type.IRType;

public final class IRRegister extends IRValue {
    public IRInstruction def  = null;
    public IRType type = null;
    public String name;

    public IRRegister(String name) {
        this.name = name;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        return type + " %\"" + name+"\"";
    }

    @Override
    public IRType getType() {
        return type;
    }
}
