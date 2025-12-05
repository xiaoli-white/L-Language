package ldk.l.lc.ir;

import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.type.IRType;

public final class IRUtils {
    public static IRType parseType(IRModule module, Type type) {
        if (SystemTypes.INT.equals(type)) {
            return IRType.getIntType();
        }
        return null;
    }
}
