package ldk.l.lc.semantic.types;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class MethodPointerType extends Type {
    public List<Type> paramTypes;
    public Type returnType;

    public MethodPointerType(List<Type>paramTypes, Type returnType) {
        super(TypeKind.MethodPointer);
        this.paramTypes = paramTypes;
        this.returnType = returnType;
    }

    @Override
    public boolean hasVoid() {
        return this.returnType.hasVoid();
    }

    @Override
    public String toString() {
        return "MethodPointerType{" +
                "paramTypes=" + paramTypes +
                ", returnType=" + returnType +
                '}';
    }

    @Override
    public Object accept(TypeVisitor visitor) {
        return visitor.visitMethodType(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodPointerType that)) return false;
        return Objects.equals(returnType, that.returnType) && Objects.deepEquals(paramTypes, that.paramTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(returnType, paramTypes);
    }

    @Override
    public String toTypeString() {
        StringBuilder string = new StringBuilder("(");
        for (Type paramType : paramTypes) {
            string.append(paramType.toTypeString()).append(", ");
        }
        if (!paramTypes.isEmpty()) string.delete(string.length() - 2, string.length());
        string.append(")");
        if (returnType != null)
            string.append(":").append(returnType.toTypeString());
        return string.toString();
    }

    @Override
    public String toTypeSignature() {
        StringBuilder stringBuilder = new StringBuilder("(");
        for (Type paramType : paramTypes) {
            stringBuilder.append(paramType.toTypeSignature());
        }
        stringBuilder.append(")").append(returnType.toTypeSignature());
        return stringBuilder.toString();
    }
}
