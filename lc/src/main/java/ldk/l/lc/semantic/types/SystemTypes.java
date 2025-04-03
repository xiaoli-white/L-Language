package ldk.l.lc.semantic.types;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.util.error.ErrorStream;

import java.util.List;

public class SystemTypes {
    //基础类型
    public static final NamedType UNSIGNED_BYTE = new NamedType("ubyte");
    public static final NamedType UNSIGNED_SHORT = new NamedType("ushort");
    public static final NamedType UNSIGNED_INT = new NamedType("uint");
    public static final NamedType UNSIGNED_LONG = new NamedType("ulong");
    public static final NamedType BYTE = new NamedType("byte");
    public static final NamedType SHORT = new NamedType("short");
    public static final NamedType INT = new NamedType("int");
    public static final NamedType LONG = new NamedType("long");
    public static final NamedType FLOAT = new NamedType("float");
    public static final NamedType DOUBLE = new NamedType("double");
    public static final NamedType BOOLEAN = new NamedType("boolean");
    public static final NamedType CHAR = new NamedType("char");
    public static final NamedType VOID = new NamedType("void");
    public static final NamedType AUTO = new NamedType("auto");

    public static final PointerType VOID_POINTER = new PointerType(SystemTypes.VOID);

    // Object Types
    public static NamedType Object_Type = null;
    public static NamedType String_Type = null;
    public static NamedType Class_Type = null;
    public static NamedType Throwable_Type = null;
    // Wrapper Types
    public static NamedType Byte_WrapperType = null;
    public static NamedType Short_WrapperType = null;
    public static NamedType Integer_WrapperType = null;
    public static NamedType Long_WrapperType = null;
    public static NamedType UnsignedByte_WrapperType = null;
    public static NamedType UnsignedShort_WrapperType = null;
    public static NamedType UnsignedInteger_WrapperType = null;
    public static NamedType UnsignedLong_WrapperType = null;
    public static NamedType Float_WrapperType = null;
    public static NamedType Double_WrapperType = null;
    public static NamedType Boolean_WrapperType = null;
    public static NamedType Character_WrapperType = null;

    static {
        BYTE.upperTypes.add(SystemTypes.SHORT);
        SHORT.upperTypes.add(SystemTypes.INT);
        INT.upperTypes.add(SystemTypes.LONG);
        UNSIGNED_BYTE.upperTypes.addAll(List.of(SystemTypes.UNSIGNED_SHORT, SystemTypes.SHORT));
        UNSIGNED_SHORT.upperTypes.addAll(List.of(SystemTypes.UNSIGNED_INT, SystemTypes.INT));
        UNSIGNED_INT.upperTypes.addAll(List.of(SystemTypes.UNSIGNED_LONG, SystemTypes.LONG));
    }

    public static boolean isReference(Type type) {
        if (type == null) return false;
        if (type instanceof ReferenceType referenceType) return SystemTypes.isReference(referenceType.base);
        return type instanceof ArrayType || type instanceof NullableType || (type instanceof NamedType namedType && !SystemTypes.isPrimitiveType(namedType));
    }

    public static boolean isPrimitiveType(Type type) {
        if (type == null) return false;
        return SystemTypes.isNumberType(type) || type.equals(SystemTypes.BOOLEAN) || type.equals(SystemTypes.CHAR);
    }

    public static boolean isNumberType(Type type) {
        if (type == null) return false;
        return SystemTypes.isIntegerType(type) || SystemTypes.isDecimalType(type);
    }

    public static boolean isIntegerType(Type type) {
        if (type == null) return false;
        return SystemTypes.isSignedIntegerType(type) || SystemTypes.isUnsignedIntegerType(type);
    }

    public static boolean isSignedIntegerType(Type type) {
        if (type == null) return false;
        return type.equals(SystemTypes.BYTE) || type.equals(SystemTypes.SHORT) || type.equals(SystemTypes.INT) || type.equals(SystemTypes.LONG);
    }

    public static boolean isUnsignedIntegerType(Type type) {
        if (type == null) return false;
        return type.equals(SystemTypes.UNSIGNED_BYTE) || type.equals(SystemTypes.UNSIGNED_SHORT) || type.equals(SystemTypes.UNSIGNED_INT) || type.equals(SystemTypes.UNSIGNED_LONG);
    }

    public static boolean isDecimalType(Type type) {
        if (type == null) return false;
        return type.equals(SystemTypes.FLOAT) || type.equals(SystemTypes.DOUBLE);
    }

    public static boolean isWrapperType(Type type) {
        if (type == null) return false;
        return type.equals(SystemTypes.Byte_WrapperType) || type.equals(SystemTypes.Short_WrapperType) || type.equals(SystemTypes.Integer_WrapperType) || type.equals(SystemTypes.Long_WrapperType)
                || type.equals(SystemTypes.UnsignedByte_WrapperType) || type.equals(SystemTypes.UnsignedShort_WrapperType) || type.equals(SystemTypes.UnsignedInteger_WrapperType) || type.equals(SystemTypes.UnsignedLong_WrapperType)
                || type.equals(SystemTypes.Float_WrapperType) || type.equals(SystemTypes.Double_WrapperType) || type.equals(SystemTypes.Boolean_WrapperType) || type.equals(SystemTypes.Character_WrapperType);
    }

    public static void setObjectTypes(LCAst ast, ErrorStream errorStream) {
        SystemTypes.Object_Type = ast.getType("l.lang.Object");
        SystemTypes.String_Type = ast.getType("l.lang.String");
        SystemTypes.Class_Type = ast.getType("l.lang.Class");
        SystemTypes.Throwable_Type = ast.getType("l.lang.Throwable");
        SystemTypes.Byte_WrapperType = ast.getType("l.lang.Byte");
        SystemTypes.Short_WrapperType = ast.getType("l.lang.Short");
        SystemTypes.Integer_WrapperType = ast.getType("l.lang.Integer");
        SystemTypes.Long_WrapperType = ast.getType("l.lang.Long");
        SystemTypes.UnsignedByte_WrapperType = ast.getType("l.lang.UnsignedByte");
        SystemTypes.UnsignedShort_WrapperType = ast.getType("l.lang.UnsignedShort");
        SystemTypes.UnsignedInteger_WrapperType = ast.getType("l.lang.UnsignedInteger");
        SystemTypes.UnsignedLong_WrapperType = ast.getType("l.lang.UnsignedLong");
        SystemTypes.Float_WrapperType = ast.getType("l.lang.Float");
        SystemTypes.Double_WrapperType = ast.getType("l.lang.Double");
        SystemTypes.Boolean_WrapperType = ast.getType("l.lang.Boolean");
        SystemTypes.Character_WrapperType = ast.getType("l.lang.Character");

        if (SystemTypes.Object_Type == null) {
            System.err.println("Cannot found the class 'l.lang.Object'.");
        }
        if (SystemTypes.String_Type == null) {
            System.err.println("Cannot found the class 'l.lang.String'.");
        }
        if (SystemTypes.Class_Type == null) {
            System.err.println("Cannot found the class 'l.lang.Class'.");
        }
        if (SystemTypes.Throwable_Type == null) {
            System.err.println("Cannot found the class 'l.lang.Throwable'.");
        }
        if (SystemTypes.Byte_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.Byte'.");
        }
        if (SystemTypes.Short_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.Short'.");
        }
        if (SystemTypes.Integer_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.Integer'.");
        }
        if (SystemTypes.Long_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.Long'.");
        }
        if (SystemTypes.UnsignedByte_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.UnsignedByte'.");
        }
        if (SystemTypes.UnsignedShort_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.UnsignedShort'.");
        }
        if (SystemTypes.UnsignedInteger_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.UnsignedInteger'.");
        }
        if (SystemTypes.UnsignedLong_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.UnsignedLong'.");
        }
        if (SystemTypes.Float_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.Float'.");
        }
        if (SystemTypes.Double_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.Double'.");
        }
        if (SystemTypes.Boolean_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.Boolean'.");
        }
        if (SystemTypes.Character_WrapperType == null) {
            System.err.println("Cannot found the class 'l.lang.Character'.");
        }
    }

    public static boolean checkObjectTypes() {
        return SystemTypes.Object_Type != null || SystemTypes.String_Type != null || SystemTypes.Class_Type != null || SystemTypes.Throwable_Type != null
                || SystemTypes.Byte_WrapperType != null || SystemTypes.Short_WrapperType != null || SystemTypes.Integer_WrapperType != null || SystemTypes.Long_WrapperType != null
                || SystemTypes.UnsignedByte_WrapperType != null || SystemTypes.UnsignedShort_WrapperType != null || SystemTypes.UnsignedInteger_WrapperType != null || SystemTypes.UnsignedLong_WrapperType != null
                || SystemTypes.Float_WrapperType != null || SystemTypes.Double_WrapperType != null || SystemTypes.Boolean_WrapperType != null || SystemTypes.Character_WrapperType != null;
    }

    public static NamedType getWrapperTypeByPrimitiveType(Type primitiveType) {
        if (primitiveType.equals(SystemTypes.BYTE)) {
            return SystemTypes.Byte_WrapperType;
        } else if (primitiveType.equals(SystemTypes.SHORT)) {
            return SystemTypes.Short_WrapperType;
        } else if (primitiveType.equals(SystemTypes.INT)) {
            return SystemTypes.Integer_WrapperType;
        } else if (primitiveType.equals(SystemTypes.LONG)) {
            return SystemTypes.Long_WrapperType;
        } else if (primitiveType.equals(SystemTypes.UNSIGNED_BYTE)) {
            return SystemTypes.UnsignedByte_WrapperType;
        } else if (primitiveType.equals(SystemTypes.UNSIGNED_SHORT)) {
            return SystemTypes.UnsignedShort_WrapperType;
        } else if (primitiveType.equals(SystemTypes.UNSIGNED_INT)) {
            return SystemTypes.UnsignedInteger_WrapperType;
        } else if (primitiveType.equals(SystemTypes.UNSIGNED_LONG)) {
            return SystemTypes.UnsignedLong_WrapperType;
        } else if (primitiveType.equals(SystemTypes.FLOAT)) {
            return SystemTypes.Float_WrapperType;
        } else if (primitiveType.equals(SystemTypes.DOUBLE)) {
            return SystemTypes.Double_WrapperType;
        } else if (primitiveType.equals(SystemTypes.BOOLEAN)) {
            return SystemTypes.Boolean_WrapperType;
        } else if (primitiveType.equals(SystemTypes.CHAR)) {
            return SystemTypes.Character_WrapperType;
        } else {
            return null;
        }
    }

    public static NamedType getUnsignedType(Type type) {
        if (SystemTypes.BYTE.equals(type)) return SystemTypes.UNSIGNED_BYTE;
        if (SystemTypes.SHORT.equals(type)) return SystemTypes.UNSIGNED_SHORT;
        else if (SystemTypes.INT.equals(type)) return SystemTypes.UNSIGNED_INT;
        else if (SystemTypes.LONG.equals(type)) return SystemTypes.UNSIGNED_LONG;
        else if (type instanceof NamedType namedType && SystemTypes.isUnsignedIntegerType(namedType)) return namedType;
        else return null;
    }
}