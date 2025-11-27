package ldk.l.lg.ir.structure;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRNode;
import ldk.l.lg.ir.type.IRType;

import java.util.Arrays;
import java.util.List;

public final class IRStructure extends IRNode {
    public List<String> attributes;
    public String name;
    @Deprecated
    public final IRField[] ffields;
    public List<IRField> fields;

    @Deprecated
    public IRStructure(String name, IRField[] fields) {
        this.attributes = null;
        this.name = name;
        this.ffields = fields;
        this.fields = Arrays.asList(fields);
    }

    public IRStructure(String name, List<IRField> fields) {
        this(List.of(), name, fields);
    }

    public IRStructure(List<String> attributes, String name, List<IRField> fields) {
        this.ffields = null;
        this.attributes = attributes;
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
                "attributes=" + attributes +
                ", name='" + name + '\'' +
                ", fields=" + fields +
                '}';
    }

    public long getLength() {
        long length = 0;
        for (IRField field : this.ffields) length += IRType.getLength(field.type);
        return length;
    }
}
