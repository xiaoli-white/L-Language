package ldk.l.lg.ir.operand;

import ldk.l.lg.ir.IRVisitor;

import java.util.Arrays;

public final class IRVirtualTable extends IROperand {
    public final String[] functions;

    public IRVirtualTable(String[] functions) {
        this.functions = functions;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitVirtualTable(this, additional);
    }

    @Override
    public String toString() {
        return "IRVirtualTable{" +
                "functions=" + Arrays.toString(functions) +
                '}';
    }
}
