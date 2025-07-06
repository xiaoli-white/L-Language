package ldk.l.lc.semantic.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class NamedType extends Type {
    public String name;
    public List<NamedType> upperTypes;
    public boolean isComplement;

    public NamedType(String name) {
        this(name, new NamedType[0], false);
    }

    public NamedType(String name, NamedType[] upperTypes) {
        this(name, upperTypes, false);
    }

    public NamedType(String name, NamedType[] upperTypes, boolean isComplement) {
        this(name, new ArrayList<>(List.of(upperTypes)), isComplement);
    }

    public NamedType(String name, List<NamedType> upperTypes, boolean isComplement) {
        super(TypeKind.Named);
        this.name = name;
        this.upperTypes = upperTypes;
        this.isComplement = isComplement;
    }

    public boolean hasVoid() {
        if (this == SystemTypes.VOID) {
            return true;
        } else {
            for (NamedType t : this.upperTypes) {
                if (t.hasVoid()) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public String toString() {
        return "NamedType{" +
                "name='" + name + '\'' +
                ", upperTypes=" + upperTypes +
                ", isComplement=" + isComplement +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamedType namedType)) return false;
        return isComplement == namedType.isComplement && Objects.equals(name, namedType.name) && Objects.equals(upperTypes, namedType.upperTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, upperTypes, isComplement);
    }

    public Object accept(TypeVisitor visitor) {
        return visitor.visitNamedType(this);
    }

    @Override
    public String toTypeString() {
        return name;
    }

    @Override
    public String toTypeSignature() {
        if (this.equals(SystemTypes.BYTE)) {
            return "B";
        } else if (this.equals(SystemTypes.UNSIGNED_BYTE)) {
            return "b";
        } else if (this.equals(SystemTypes.SHORT)) {
            return "S";
        } else if (this.equals(SystemTypes.UNSIGNED_SHORT)) {
            return "s";
        } else if (this.equals(SystemTypes.INT)) {
            return "I";
        } else if (this.equals(SystemTypes.UNSIGNED_INT)) {
            return "i";
        } else if (this.equals(SystemTypes.LONG)) {
            return "L";
        } else if (this.equals(SystemTypes.UNSIGNED_LONG)) {
            return "l";
        } else if (this.equals(SystemTypes.FLOAT)) {
            return "F";
        } else if (this.equals(SystemTypes.DOUBLE)) {
            return "D";
        } else if (this.equals(SystemTypes.CHAR)) {
            return "C";
        } else if (this.equals(SystemTypes.BOOLEAN)) {
            return "Z";
        } else if (this.equals(SystemTypes.VOID)) {
            return "V";
        } else {
            return "O" + name.replace(".", "/") + ";";
        }
    }
}