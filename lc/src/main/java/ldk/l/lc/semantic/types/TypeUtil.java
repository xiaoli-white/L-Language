package ldk.l.lc.semantic.types;

import java.util.LinkedHashSet;
import java.util.Set;

public class TypeUtil {
    public static boolean LE(Type t1, Type t2) {
        if (t1 == null || t2 == null) return false;
        if (t1.equals(t2)) return true;

        if (t1 instanceof NullableType nullableType) {
            if (t2 instanceof NullableType nullableType2)
                return TypeUtil.LE(nullableType.base, nullableType2.base);
            else
                return false;
        }
        if (t2 instanceof NullableType nullableType) return TypeUtil.LE(t1, nullableType.base);
        if (t1 instanceof ReferenceType referenceType) return TypeUtil.LE(referenceType.base, t2);
        if (t2 instanceof ReferenceType referenceType) return TypeUtil.LE(t1, referenceType.base);

        return switch (t1) {
            case NamedType namedType -> t2 instanceof NamedType namedType2 && TypeUtil.LE_N_N(namedType, namedType2);
            case MethodPointerType methodPointerType ->
                    t2 instanceof MethodPointerType methodPointerType2 && TypeUtil.LE_M_M(methodPointerType, methodPointerType2);
            case ArrayType arrayType -> t2 instanceof ArrayType arrayType2 && TypeUtil.LE_A_A(arrayType, arrayType2);
            case PointerType pointerType ->
                    t2 instanceof PointerType pointerType2 && TypeUtil.LE_P_P(pointerType, pointerType2);
            default -> false;
        };
    }

    private static boolean LE_A_A(ArrayType t1, ArrayType t2) {
        return TypeUtil.LE(t1.base, t2.base);
    }

    private static boolean LE_M_M(MethodPointerType t1, MethodPointerType t2) {
        if (t1.paramTypes.length != t2.paramTypes.length) return false;
        if (TypeUtil.LE(t1.returnType, t2.returnType)) return false;

        for (int i = 0; i < t1.paramTypes.length; i++) {
            if (!TypeUtil.LE(t1.paramTypes[i], t2.paramTypes[i])) {
                return false;
            }
        }
        return true;
    }

    private static boolean LE_N_N(NamedType t1, NamedType t2) {
        if (t1.upperTypes.contains(t2))
            return true;

        for (NamedType upperType : t1.upperTypes) {
            if (TypeUtil.LE(upperType, t2))
                return true;
        }
        return false;
    }

    private static boolean LE_P_P(PointerType t1, PointerType t2) {
        if (t2.equals(SystemTypes.VOID_POINTER)) return true;
        return TypeUtil.LE(t1.base, t2.base);
    }

    public static boolean overlap(Type t1, Type t2) {
        if (t1 == SystemTypes.Object_Type || t2 == SystemTypes.Object_Type) {
            return true;
        } else if (t1 == t2) {
            return true;
        }

        return switch (t1.kind) {
            case Named -> switch (t2.kind) {
                case Named -> TypeUtil.overlap_N_N((NamedType) t1, (NamedType) t2);
                default -> false;
            };
            default -> false;
        };
    }

    private static boolean overlap_N_N(NamedType t1, NamedType t2) {
        return TypeUtil.LE_N_N(t1, t2) || TypeUtil.LE_N_N(t2, t1);
    }

    public static Type getComplementType(Type t) {
        if (t instanceof NamedType namedType) {
            return new NamedType(namedType.name, namedType.upperTypes, !namedType.isComplement);
        } else {
            return t;
        }
    }

    public static NamedType getNamedTypeByValue(Object v) {
        return switch (v) {
            case Byte b -> SystemTypes.INT;
            case Short i -> SystemTypes.SHORT;
            case Integer i -> SystemTypes.INT;
            case Long l -> SystemTypes.LONG;
            case Double d -> SystemTypes.DOUBLE;
            case Float f -> SystemTypes.FLOAT;
            case Boolean b -> SystemTypes.BOOLEAN;
            case String s -> SystemTypes.String_Type;
            case null, default -> SystemTypes.Object_Type;
        };
    }

    public static NamedType getNamedType(String str) {
        return switch (str) {
            case "byte" -> SystemTypes.BYTE;
            case "short" -> SystemTypes.SHORT;
            case "int" -> SystemTypes.INT;
            case "long" -> SystemTypes.LONG;
            case "float" -> SystemTypes.FLOAT;
            case "double" -> SystemTypes.DOUBLE;
            case "boolean" -> SystemTypes.BOOLEAN;
            case "char" -> SystemTypes.CHAR;
            case "String" -> SystemTypes.String_Type;
            case "void" -> SystemTypes.VOID;
            case "null", "Object" -> SystemTypes.Object_Type;
            default -> SystemTypes.AUTO;
        };
    }

    public static String evaluateTypeOf(Type t) {
        String typeStr;
        if (t instanceof NamedType) {
            if (t == SystemTypes.BYTE) {
                typeStr = "byte";
            } else if (t == SystemTypes.SHORT) {
                typeStr = "short";
            } else if (t == SystemTypes.INT) {
                typeStr = "int";
            } else if (t == SystemTypes.LONG) {
                typeStr = "long";
            } else if (t == SystemTypes.FLOAT) {
                typeStr = "float";
            } else if (t == SystemTypes.DOUBLE) {
                typeStr = "double";
            } else if (t == SystemTypes.BOOLEAN) {
                typeStr = "boolean";
            } else if (t == SystemTypes.CHAR) {
                typeStr = "char";
            } else if (t == SystemTypes.String_Type) {
                typeStr = "String";
            } else if (t == SystemTypes.VOID) {
                typeStr = "void";
            } else {
                typeStr = "object";
            }
        } else if (t instanceof ArrayType) {
            typeStr = "object";
        } else {
            typeStr = "object";
        }

        return typeStr;
    }

    private static boolean isSame(Type t1, Type t2) {
        if (t1 == t2) {
            return true;
        } else if (t1.kind != t2.kind) {
            return false;
        } else {
            return false;
        }
    }

    private static boolean contains(Type[] types, Type t) {
        for (Type t1 : types) {
            if (TypeUtil.isSame(t1, t)) {
                return true;
            }
        }
        return false;
    }

    public static Type getUpperBound(Type t1, Type t2) {
        if (t1 == SystemTypes.AUTO || t2 == SystemTypes.AUTO) {
            return SystemTypes.AUTO;
        } else if (TypeUtil.LE(t1, t2)) {
            return t2;
        } else if (TypeUtil.LE(t2, t1)) {
            return t1;
        } else if (t1 instanceof PointerType && SystemTypes.isIntegerType(t2)) {
            return t1;
        } else {
            Set<NamedType> t1Supers = collectAllSupers(t1);
            Set<NamedType> t2Supers = collectAllSupers(t2);

            Set<NamedType> commonSupers = new LinkedHashSet<>(t1Supers);
            commonSupers.retainAll(t2Supers);

            return findMostSpecificCommonSuper(commonSupers);
        }
    }

    private static Set<NamedType> collectAllSupers(Type type) {
        Set<NamedType> supers = new LinkedHashSet<>();
        if (type instanceof NamedType namedType) {
            supers.add(namedType);
            for (NamedType upper : namedType.upperTypes) {
                supers.addAll(collectAllSupers(upper));
            }
        }
        return supers;
    }

    private static Type findMostSpecificCommonSuper(Set<NamedType> types) {
        if (types.isEmpty()) {
            return SystemTypes.AUTO;
        }

        for (NamedType candidate : types) {
            boolean isMostSpecific = true;
            for (NamedType other : types) {
                if (candidate != other && TypeUtil.LE(candidate, other)) {
                    isMostSpecific = false;
                    break;
                }
            }
            if (isMostSpecific) {
                return candidate;
            }
        }
        return SystemTypes.AUTO;
    }
}