package ldk.l.lg.ir.type;

import ldk.l.lg.ir.IRVisitor;

import java.util.List;
import java.util.Objects;

public final class IRFunctionReferenceType extends IRType {
    public IRType returnType;
    public List<IRType> parameterTypes;
    public boolean isVarArg;

    public IRFunctionReferenceType(IRType returnType, List<IRType> parameterTypes, boolean isVarArg) {
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.isVarArg = isVarArg;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IRFunctionReferenceType that)) return false;
        return isVarArg == that.isVarArg && Objects.equals(returnType, that.returnType) && Objects.equals(parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnType, parameterTypes, isVarArg);
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("(");
        for (int i = 0; i < parameterTypes.size(); i++) {
            builder.append(parameterTypes.get(i));
            if (isVarArg || i != parameterTypes.size() - 1) builder.append(", ");
        }
        if (isVarArg) builder.append("...");
        builder.append(") -> ").append(returnType);
        return builder.toString();
    }

    @Override
    public long getLength() {
        return 8;
    }
}
