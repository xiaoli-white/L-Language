package ldk.l.lg.ir.operand;

import ldk.l.lg.ir.base.IRNode;
@Deprecated
public abstract sealed class IROperand extends IRNode permits IRConstant, IRInterfaceTable, IRMacro, IRPhi, IRVirtualRegister, IRVirtualTable {
}
