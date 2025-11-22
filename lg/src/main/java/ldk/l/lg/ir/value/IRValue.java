package ldk.l.lg.ir.value;

import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.constant.IRConstant;

public abstract class IRValue extends IRNode{
    public abstract IRType getType();
}
