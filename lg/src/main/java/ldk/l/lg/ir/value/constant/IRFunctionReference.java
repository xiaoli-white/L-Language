package ldk.l.lg.ir.value.constant;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.function.IRLocalVariable;
import ldk.l.lg.ir.type.IRFunctionReferenceType;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRValue;

public final class IRFunctionReference extends IRValue {
    public IRFunction function;

    public IRFunctionReference(IRFunction function) {
        this.function = function;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        return "funcref \"" + function.name + "\"";
    }

    @Override
    public IRType getType() {
        return new IRFunctionReferenceType(function.returnType, function.args.stream().map(local -> local.type).toList(), function.isVarArg);
    }
}