package ldk.l.lc.ast.base;

public class LCFlags {
    public static final long PRIVATE = 1;
    public static final long PROTECTED = 1 << 1;
    public static final long PUBLIC = 1 << 2;
    public static final long STATIC = 1 << 3;
    public static final long CONST = 1 << 4;
    public static final long READONLY = 1 << 5;
    public static final long FINAL = 1 << 6;
    public static final long ABSTRACT = 1 << 7;
    public static final long OVERRIDE = 1 << 8;
    public static final long DEFAULT = 1 << 9;
    public static final long VOLATILE = 1 << 10;
    public static final long VARARG = 1 << 11;
    public static final long SYNCHRONIZED = 1 << 12;
    public static final long SYNTHETIC = 1 << 13;
    public static final long BRIDGE = 1 << 14;
    public static final long OPERATOR = 1 << 15;
    public static final long SEALED = 1 << 16;
    public static final long NON_SEALED = 1 << 17;
    public static final long INTERNAl = 1 << 18;
    public static final long EXTERN = 1 << 19;
    public static final long LATEINIT = 1 << 20;
    public static final long THIS_READONLY = 1 << 22;
    public static final long INTERFACE = 1 << 24;
    public static final long ANNOTATION = 1 << 25;
    public static final long ENUM = 1 << 26;
    public static final long RECORD = 1 << 27;

    public static String toFlagsString(long flags) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(LCFlags.hasPrivate(flags) ? ", PRIVATE" : "").append(LCFlags.hasProtected(flags) ? ", PROTECTED" : "").append(LCFlags.hasPublic(flags) ? ", PUBLIC" : "");
        stringBuilder.append(LCFlags.hasStatic(flags) ? ", STATIC" : "").append(LCFlags.hasConst(flags) ? ", CONST" : "").append(LCFlags.hasFinal(flags) ? ", FINAL" : "").append(LCFlags.hasReadonly(flags) ? ", READONLY" : "");
        stringBuilder.append(LCFlags.hasAbstract(flags) ? ", ABSTRACT" : "").append(LCFlags.hasOverride(flags) ? ", OVERRIDE" : "").append(LCFlags.hasDefault(flags) ? ", DEFAULT" : "");
        stringBuilder.append(LCFlags.hasVolatile(flags) ? ", VOLATILE" : "").append(LCFlags.hasVararg(flags) ? ", VARARG" : "");
        stringBuilder.append(LCFlags.hasSynchronized(flags) ? ", SYNCHRONIZED" : "").append(LCFlags.hasSynthetic(flags) ? ", SYNTHETIC" : "").append(LCFlags.hasBridge(flags) ? ", BRIDGE" : "");
        stringBuilder.append(LCFlags.hasOperator(flags) ? ", OPERATOR" : "").append(LCFlags.hasSealed(flags) ? ", SEALED" : "").append(LCFlags.hasNonSealed(flags) ? ", NON_SEALED" : "").append(LCFlags.hasInternal(flags) ? ", INTERNAL" : "");
        stringBuilder.append(LCFlags.hasExtern(flags) ? ", EXTERN" : "").append(LCFlags.hasLateinit(flags) ? ", LATEINIT" : "").append(LCFlags.hasThisReadonly(flags) ? ", THIS_READONLY" : "");
        stringBuilder.append(LCFlags.hasInterface(flags) ? ", INTERFACE" : "").append(LCFlags.hasAnnotation(flags) ? ", ANNOTATION" : "").append(LCFlags.hasEnum(flags) ? ", ENUM" : "").append(LCFlags.hasRecord(flags) ? ", RECORD" : "");

        return stringBuilder.isEmpty() ? "" : stringBuilder.substring(2);
    }

    public static String toString(long flags) {
        return "(0x" + Long.toHexString(flags) + ")" + LCFlags.toFlagsString(flags);
    }

    public static boolean hasPrivate(long flags) {
        return (flags & LCFlags.PRIVATE) != 0;
    }

    public static boolean hasProtected(long flags) {
        return (flags & LCFlags.PROTECTED) != 0;
    }

    public static boolean hasPublic(long flags) {
        return (flags & LCFlags.PUBLIC) != 0;
    }

    public static boolean hasStatic(long flags) {
        return (flags & LCFlags.STATIC) != 0;
    }

    public static boolean hasConst(long flags) {
        return (flags & LCFlags.CONST) != 0;
    }

    public static boolean hasReadonly(long flags) {
        return (flags & LCFlags.READONLY) != 0;
    }

    public static boolean hasFinal(long flags) {
        return (flags & LCFlags.FINAL) != 0;
    }

    public static boolean hasAbstract(long flags) {
        return (flags & LCFlags.ABSTRACT) != 0;
    }

    public static boolean hasOverride(long flags) {
        return (flags & LCFlags.OVERRIDE) != 0;
    }

    public static boolean hasDefault(long flags) {
        return (flags & LCFlags.DEFAULT) != 0;
    }

    public static boolean hasVolatile(long flags) {
        return (flags & LCFlags.VOLATILE) != 0;
    }

    public static boolean hasVararg(long flags) {
        return (flags & LCFlags.VARARG) != 0;
    }

    public static boolean hasSynchronized(long flags) {
        return (flags & LCFlags.SYNCHRONIZED) != 0;
    }

    public static boolean hasSynthetic(long flags) {
        return (flags & LCFlags.SYNTHETIC) != 0;
    }

    public static boolean hasBridge(long flags) {
        return (flags & LCFlags.BRIDGE) != 0;
    }

    public static boolean hasOperator(long flags) {
        return (flags & LCFlags.OPERATOR) != 0;
    }

    public static boolean hasSealed(long flags) {
        return (flags & LCFlags.SEALED) != 0;
    }

    public static boolean hasNonSealed(long flags) {
        return (flags & LCFlags.NON_SEALED) != 0;
    }

    public static boolean hasInternal(long flags) {
        return (flags & LCFlags.INTERNAl) != 0;
    }

    public static boolean hasExtern(long flags) {
        return (flags & LCFlags.EXTERN) != 0;
    }

    public static boolean hasLateinit(long flags) {
        return (flags & LCFlags.LATEINIT) != 0;
    }

    public static boolean hasThisReadonly(long flags) {
        return (flags & LCFlags.THIS_READONLY) != 0;
    }

    public static boolean hasInterface(long flags) {
        return (flags & LCFlags.INTERFACE) != 0;
    }

    public static boolean hasAnnotation(long flags) {
        return (flags & LCFlags.ANNOTATION) != 0;
    }

    public static boolean hasEnum(long flags) {
        return (flags & LCFlags.ENUM) != 0;
    }

    public static boolean hasRecord(long flags) {
        return (flags & LCFlags.RECORD) != 0;
    }

}
