package ldk.l.lg.ir.structure;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.type.IRType;

import java.util.Arrays;

public class IRStructure extends IRNode {
    // public final String[] attributes;
    public final String name;
    public final IRField[] fields;

    public IRStructure(String name, IRField[] fields) {
        this.name = name;
        this.fields = fields;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitStructure(this, additional);
    }

    @Override
    public String toString() {
        return "IRStructure{" +
                "name='" + name + '\'' +
                ", fields=" + Arrays.toString(fields) +
                '}';
    }

    public long getLength() {
        long length = 0;
        for (IRField field : this.fields) length += IRType.getLength(field.type);
        return length;
    }
}
