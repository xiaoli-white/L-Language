package ldk.l.lg.ir.base;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.operand.IROperand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
public final class IRGlobalDataSection extends IRNode {
    public final List<GlobalData> data;

    public IRGlobalDataSection() {
        this.data = new ArrayList<>();
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitGlobalDataSection(this, additional);
    }

    @Override
    public String toString() {
        return "IRGlobalDataSection{" +
                "data=" + data +
                '}';
    }

    public void add(GlobalData globalData) {
        data.add(globalData);
    }

    public boolean contains(String name) {
        for (GlobalData globalData : data)
            if (globalData.name.equals(name))
                return true;
        return false;
    }

    public static class GlobalData extends IRNode {
        public final String name;
        public final IROperand size;
        public final IROperand[] values;

        public GlobalData(String name, IROperand size) {
            this(name, size, null);
        }

        public GlobalData(String name, IROperand[] values) {
            this(name, null, values);
        }

        private GlobalData(String name, IROperand size, IROperand[] values) {
            this.name = name;
            this.size = size;
            this.values = values;
        }

        @Override
        public Object accept(IRVisitor visitor, Object additional) {
            return visitor.visitGlobalData(this, additional);
        }

        @Override
        public String toString() {
            return name + (size != null ? ", size=" + size : "") + (values != null ? ", values=" + Arrays.toString(values) : "");
        }
    }
}
