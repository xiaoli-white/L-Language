package ldk.l.lg.ir.value;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRGlobalVariable;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;

public final class IRGlobalVariableReference extends IRValue {
    public IRType type;
    public IRGlobalVariable variable;
    public IRGlobalVariableReference(IRGlobalVariable variable) {
        this.variable = variable;
        this.type = new IRPointerType(variable.type);
    }
    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        return "globalref "+variable.name;
    }

    @Override
    public IRType getType() {
        return type;
    }
}
