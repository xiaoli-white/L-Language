package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.value.IRRegister;
import ldk.l.lg.ir.value.IRValue;

import java.util.Map;

public final class IRPhi extends IRInstruction {
    public Map<IRBasicBlock, IRValue> values;
    public IRRegister target;

    public IRPhi(Map<IRBasicBlock, IRValue> values, IRRegister target) {
        this.values = values;
        this.target = target;
        target.def = this;
        target.type = values.values().stream().findFirst().orElseThrow().getType();
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitPhi(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("%");
        sb.append(target.name).append(" = phi ");
        for (Map.Entry<IRBasicBlock, IRValue> entry : values.entrySet()) {
            sb.append(", [label ").append(entry.getKey().name).append(", ").append(entry.getValue()).append("]");
        }
        return sb.toString();
    }
}
