package ldk.l.lg.ir.operand;

import ldk.l.lg.ir.base.IRNode;

public abstract sealed class IROperand extends IRNode permits IRConstant, IRInterfaceTable, IRMacro, IRPhi, IRVirtualRegister, IRVirtualTable {
}
