package ldk.l.lc.semantic.types;

import ldk.l.lc.token.CharStream;

import java.util.ArrayList;

public abstract sealed class Type permits ArrayType, MethodPointerType, NamedType, NullableType, PointerType, ReferenceType {
    public TypeKind kind;

    public Type(TypeKind kind) {
        this.kind = kind;
    }

    public abstract Object accept(TypeVisitor visitor);

    public abstract boolean hasVoid();

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public abstract int hashCode();

    public abstract String toTypeString();

    public abstract String toTypeSignature();

    public static Type parseTypeString(CharStream string) {
        if (string.equalsString("B")) {
            return SystemTypes.BYTE;
        } else if (string.equalsString("b")) {
            return SystemTypes.UNSIGNED_BYTE;
        } else if (string.equalsString("S")) {
            return SystemTypes.SHORT;
        } else if (string.equalsString("s")) {
            return SystemTypes.UNSIGNED_SHORT;
        } else if (string.equalsString("I")) {
            return SystemTypes.INT;
        } else if (string.equalsString("i")) {
            return SystemTypes.UNSIGNED_INT;
        } else if (string.equalsString("L")) {
            return SystemTypes.LONG;
        } else if (string.equalsString("l")) {
            return SystemTypes.UNSIGNED_LONG;
        } else if (string.equalsString("F")) {
            return SystemTypes.FLOAT;
        } else if (string.equalsString("D")) {
            return SystemTypes.DOUBLE;
        } else if (string.equalsString("C")) {
            return SystemTypes.CHAR;
        } else if (string.equalsString("Z")) {
            return SystemTypes.BOOLEAN;
        } else if (string.equalsString("V")) {
            return SystemTypes.VOID;
        } else if (string.startsWith("O") && string.endsWith(";")) {
            return new NamedType(string.substring(1, string.length() - 1));
        } else if (string.startsWith("R")) {
            string.addPos(1);
            return new ReferenceType(Type.parseTypeString(string));
        } else if (string.startsWith("P")) {
            string.addPos(1);
            return new PointerType(Type.parseTypeString(string));
        } else if (string.startsWith("N")) {
            string.addPos(1);
            return new NullableType(Type.parseTypeString(string));
        } else if (string.startsWith("(")) {
            string.addPos(1);
            ArrayList<Type> paramTypes = new ArrayList<>();
            while (!string.eof() && string.peek() != ')') {
                paramTypes.add(Type.parseTypeString(string));
            }
            if (string.peek() == ')') {
                string.addPos(1);
            }
            return new MethodPointerType(paramTypes.toArray(new Type[0]), Type.parseTypeString(string));
        } else if (string.startsWith("[")) {
            string.addPos(1);
            return new ArrayType(Type.parseTypeString(string));
        } else {
            return SystemTypes.AUTO;
        }
    }
}