package ldk.l.lg.ir.value;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.function.IRLocalVariable;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;

public final class IRLocalVariableReference extends IRValue {
    public IRType type;
    public IRLocalVariable variable;

    public IRLocalVariableReference(IRLocalVariable variable) {
        this.variable = variable;
        this.type = new IRPointerType(variable.type);
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        return "localref \"" + variable.name + "\"";
    }

    @Override
    public IRType getType() {
        return type;
    }
}
