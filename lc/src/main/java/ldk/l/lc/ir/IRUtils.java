package ldk.l.lc.ir;

import ldk.l.lc.semantic.types.*;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;
import ldk.l.lg.ir.type.*;

import java.util.ArrayList;
import java.util.List;

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
        if (type instanceof ReferenceType referenceType)
            return new IRPointerType(parseType(module, referenceType.base));
        if (type instanceof NullableType nullableType)
            return parseType(module, nullableType.base);
        if (type instanceof ArrayType arrayType) {
            String structureName = "<array<" + arrayType.base.toTypeString() + ">>";
            if (!module.structures.containsKey(structureName)) {
                List<IRField> fields = new ArrayList<>();
                fields.add(new IRField(new IRPointerType(new IRStructureType(module.structures.get("l.lang.Class"))), "<class_ptr>"));
                fields.add(new IRField(IRType.getUnsignedLongType(), "<ref_count>"));
                fields.add(new IRField(IRType.getUnsignedLongType(), "length"));
                fields.add(new IRField(new IRArrayType(parseType(module, arrayType.base), 0), "data"));
                IRStructure structure = new IRStructure(structureName, fields);
                module.putStructure(structure);
            }
            return new IRPointerType(new IRStructureType(module.structures.get(structureName)));
        }
        MethodPointerType methodPointerType = (MethodPointerType) type;
        List<IRType> paramTypes = new ArrayList<>(methodPointerType.paramTypes.size());
        for (Type paramType : methodPointerType.paramTypes) {
            paramTypes.add(parseType(module, paramType));
        }
        return new IRFunctionReferenceType(parseType(module, methodPointerType.returnType), paramTypes, false);
    }
}
