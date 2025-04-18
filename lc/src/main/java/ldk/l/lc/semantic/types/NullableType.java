package ldk.l.lc.semantic.types;

import java.util.Objects;

public final class NullableType extends Type {
    public Type base;

    public NullableType(Type base) {
        super(TypeKind.Nullable);
        this.base = base;
    }

    @Override
    public Object accept(TypeVisitor visitor) {
        return visitor.visitNullableType(this);
    }

    @Override
    public boolean hasVoid() {
        return this.base.hasVoid();
    }

    @Override
    public String toString() {
        return "NullableType{" +
                "base=" + base +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NullableType that)) return false;
        return Objects.equals(base, that.base);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(base);
    }

    @Override
    public String toTypeString() {
        return base.toTypeString() + "?";
    }

    @Override
    public String toTypeSignature() {
        return "N" + base.toTypeSignature();
    }
}
