package ldk.l.lc.ir;

import ldk.l.lc.semantic.types.NamedType;
import ldk.l.lc.semantic.types.PointerType;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRStructureType;
import ldk.l.lg.ir.type.IRType;

public final class IRUtils {
    public static IRType parseType(IRModule module, Type type) {
        if (SystemTypes.VOID.equals(type))
            return IRType.getVoidType();
        if (SystemTypes.BYTE.equals(type))
            return IRType.getByteType();
        if (SystemTypes.SHORT.equals(type))
            return IRType.getShortType();
        if (SystemTypes.INT.equals(type))
            return IRType.getIntType();
        if (SystemTypes.LONG.equals(type))
            return IRType.getLongType();
        if (SystemTypes.UNSIGNED_BYTE.equals(type))
            return IRType.getUnsignedByteType();
        if (SystemTypes.UNSIGNED_SHORT.equals(type))
            return IRType.getUnsignedShortType();
        if (SystemTypes.UNSIGNED_INT.equals(type))
            return IRType.getUnsignedIntType();
        if (SystemTypes.UNSIGNED_LONG.equals(type))
            return IRType.getUnsignedLongType();
        if (SystemTypes.FLOAT.equals(type))
            return IRType.getFloatType();
        if (SystemTypes.DOUBLE.equals(type))
            return IRType.getDoubleType();
        if (SystemTypes.CHAR.equals(type))
            return IRType.getCharType();
        if (SystemTypes.BOOLEAN.equals(type))
            return IRType.getBooleanType();
        if (type instanceof NamedType namedType)
            return new IRPointerType(new IRStructureType(module.structures.get(namedType.name)));
        if (type instanceof PointerType pointerType)
            return new IRPointerType(parseType(module, pointerType.base));
        return null;
    }
}
