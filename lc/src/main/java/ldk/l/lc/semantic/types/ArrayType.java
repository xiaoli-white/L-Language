package ldk.l.lc.semantic.types;

import java.util.Objects;

public final class ArrayType extends Type {
    public Type base;

    public ArrayType(Type base) {
        super(TypeKind.Array);
        this.base = base;
    }

    @Override
    public String toString() {
        return "ArrayType{" +
                "base=" + base +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArrayType arrayType)) return false;
        return Objects.equals(base, arrayType.base);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(base);
    }

    @Override
    public boolean hasVoid() {
        return false;
    }

    @Override
    public Object accept(TypeVisitor visitor) {
        return visitor.visitArrayType(this);
    }

    @Override
    public String toTypeString() {
        return base.toTypeString() + "[]";
    }

    @Override
    public String toTypeSignature() {
        return "[" + base.toTypeSignature();
    }
}
