package ldk.l.lg.ir;

import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.type.IRType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class IRConstantPool extends IRNode {
    public final List<Entry> entries = new ArrayList<>();

    public IRConstantPool() {
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitConstantPool(this, additional);
    }

    @Override
    public String toString() {
        return "IRConstantPool{" +
                "entries=" + entries +
                '}';
    }

    public int put(Entry entry) {
        if (this.entries.contains(entry)) return this.entries.indexOf(entry);

        this.entries.add(entry);
        return this.entries.size() - 1;
    }

    public Entry get(int index) {
        if (index < 0 || index >= this.entries.size()) return null;
        return this.entries.get(index);
    }

    public static final class Entry extends IRNode {
        public final IRType type;
        public final Object value;

        public Entry(IRType type, Object value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public Object accept(IRVisitor visitor, Object additional) {
            return visitor.visitConstantPoolEntry(this, additional);
        }

        @Override
        public String toString() {
            return "Entry{" +
                    "type=" + type +
                    ", value=" + value +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Entry entry)) return false;
            return Objects.equals(type, entry.type) && Objects.equals(value, entry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, value);
        }
    }
}
