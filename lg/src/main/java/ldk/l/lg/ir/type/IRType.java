package ldk.l.lg.ir.type;

import ldk.l.lg.ir.base.IRNode;

public abstract sealed class IRType extends IRNode permits IRArrayType, IRDoubleType, IRFloatType, IRIntegerType, IRPointerType, IRStructureType, IRVoidType {
    @Override
    public abstract boolean equals(Object o);

    @Override
    public abstract int hashCode();

    public static IRIntegerType getByteType() {
        return new IRIntegerType(IRIntegerType.Size.OneByte, false);
    }

    public static IRIntegerType getShortType() {
        return new IRIntegerType(IRIntegerType.Size.TwoBytes, false);
    }

    public static IRIntegerType getIntType() {
        return new IRIntegerType(IRIntegerType.Size.FourBytes, false);
    }

    public static IRIntegerType getLongType() {
        return new IRIntegerType(IRIntegerType.Size.EightBytes, false);
    }

    public static IRIntegerType getUnsignedByteType() {
        return new IRIntegerType(IRIntegerType.Size.OneByte, true);
    }

    public static IRIntegerType getUnsignedShortType() {
        return new IRIntegerType(IRIntegerType.Size.TwoBytes, true);
    }

    public static IRIntegerType getUnsignedIntType() {
        return new IRIntegerType(IRIntegerType.Size.FourBytes, true);
    }

    public static IRIntegerType getUnsignedLongType() {
        return new IRIntegerType(IRIntegerType.Size.EightBytes, true);
    }

    public static IRFloatType getFloatType() {
        return IRFloatType.INSTANCE;
    }

    public static IRDoubleType getDoubleType() {
        return IRDoubleType.INSTANCE;
    }

    public static IRIntegerType getBooleanType() {
        return new IRIntegerType(IRIntegerType.Size.OneBit, false);
    }

    public static IRIntegerType getCharType() {
        return new IRIntegerType(IRIntegerType.Size.FourBytes, true);
    }

    public static IRVoidType getVoidType() {
        return IRVoidType.INSTANCE;
    }

    public static long getLength(IRType type) {
        return switch (type) {
            case IRIntegerType irIntegerType ->
                    (irIntegerType.size.size+7) / 8;
            case IRFloatType _ -> 4;
            case IRDoubleType _, IRPointerType _ -> 8;
            default -> throw new IllegalStateException("Unexpected value: " + type);
        };
    }
}
