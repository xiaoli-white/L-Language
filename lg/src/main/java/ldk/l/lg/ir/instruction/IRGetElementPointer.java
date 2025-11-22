package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.type.IRArrayType;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRStructureType;
import ldk.l.lg.ir.type.IRType;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;
import ldk.l.lg.ir.value.constant.IRIntegerConstant;

import java.util.List;

public final class IRGetElementPointer extends IRInstruction {
    public final IRValue ptr;
    public final List<IRIntegerConstant> indices;
    public final IRRegister target;
    public IRGetElementPointer(IRValue ptr, List<IRIntegerConstant> indices, IRRegister target) {
        this.ptr = ptr;
        this.indices = indices;
        this.target = target;
        target.def = this;
        IRType ty = ptr.getType();
        for (IRIntegerConstant index : indices) {
            ty = switch (ty) {
                case IRPointerType pointerType -> pointerType.base;
                case IRArrayType arrayType -> arrayType.base;
                case IRStructureType structureType -> structureType.structure.fields.get((int) index.value).type;
                case null, default -> throw new RuntimeException("Invalid type");
            };
        }
        target.type = new IRPointerType(ty);
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitGetElementPointer(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("%");
        sb.append(target.name).append(" = getelementptr ").append(ptr);
        for (IRIntegerConstant index : indices) {
            sb.append(", ").append(index);
        }
        return sb.toString();
    }
}
