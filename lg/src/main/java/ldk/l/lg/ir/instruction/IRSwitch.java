package ldk.l.lg.ir.instruction;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.value.IRValue;
import ldk.l.lg.ir.value.constant.IRIntegerConstant;

import java.util.Map;

public final class IRSwitch extends IRInstruction {
    public IRValue value;
    public IRBasicBlock defaultCase;
    public Map<IRIntegerConstant, IRBasicBlock> cases;

    public IRSwitch(IRValue value, IRBasicBlock defaultCase, Map<IRIntegerConstant, IRBasicBlock> cases) {
        this.value = value;
        this.defaultCase = defaultCase;
        this.cases = cases;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitSwitch(this, additional);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("switch ").append(value).append(", label ").append(defaultCase.name);
        for (Map.Entry<IRIntegerConstant, IRBasicBlock> entry : cases.entrySet()) {
            sb.append(", [").append(entry.getKey()).append(", label ").append(entry.getValue().name).append("]");
        }
        return sb.toString();
    }
}
