package ldk.l.lg.ir.operand;

import ldk.l.lg.ir.IRVisitor;

import java.util.Arrays;

public final class IRInterfaceTable extends IROperand {
    public final Entry[] entries;

    public IRInterfaceTable(Entry[] entries) {
        this.entries = entries;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitInterfaceTable(this, additional);
    }

    @Override
    public String toString() {
        return "IRInterfaceTable{" +
                "entries=" + Arrays.toString(entries) +
                '}';
    }

    public record Entry(String name, String[] functions) {
    }
}
