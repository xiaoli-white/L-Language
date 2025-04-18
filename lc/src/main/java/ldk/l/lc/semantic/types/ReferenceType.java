package ldk.l.lc.semantic.types;

import java.util.Objects;

public final class ReferenceType extends Type {
    public Type base;

    public ReferenceType(Type base) {
        super(TypeKind.Reference);
        this.base = base;
    }

    @Override
    public Object accept(TypeVisitor visitor) {
        return visitor.visitReferenceType(this);
    }

    @Override
    public boolean hasVoid() {
        return this.base.hasVoid();
    }

    @Override
    public String toString() {
        return "ReferenceType{" +
                "base=" + base +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReferenceType that)) return false;
        return Objects.equals(base, that.base);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(base);
    }

    @Override
    public String toTypeString() {
        return base.toTypeString() + "&";
    }

    @Override
    public String toTypeSignature() {
        return "R" + base.toTypeSignature();
    }
}
