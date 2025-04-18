package ldk.l.lc.semantic.types;

import java.util.Objects;

public final class PointerType extends Type {
    public Type base;

    public PointerType(Type base) {
        super(TypeKind.Pointer);
        this.base = base;
    }

    @Override
    public Object accept(TypeVisitor visitor) {
        return visitor.visitPointerType(this);
    }

    @Override
    public boolean hasVoid() {
        return false;
    }

    @Override
    public String toString() {
        return "PointerType{" +
                "base=" + base +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PointerType that)) return false;
        return Objects.equals(base, that.base);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(base);
    }

    @Override
    public String toTypeString() {
        return base.toTypeString() + "*";
    }

    @Override
    public String toTypeSignature() {
        return "P" + base.toTypeSignature();
    }
}
