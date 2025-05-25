package ldk.l.lg.ir.attribute;

import ldk.l.lg.ir.IRVisitor;
import ldk.l.lg.ir.base.IRNode;

import java.util.Arrays;

public class IRAttributeGroupDeclaration extends IRNode {
    public final String name;
    public final String[] attributes;

    public IRAttributeGroupDeclaration(String name, String[] attributes) {
        this.name = name;
        this.attributes = attributes;
    }

    @Override
    public Object accept(IRVisitor visitor, Object additional) {
        return visitor.visitAttributeGroupDeclaration(this, additional);
    }

    @Override
    public String toString() {
        return "IRAttributeGroupDeclaration{" +
                "name='" + name + '\'' +
                ", attributes=" + Arrays.toString(attributes) +
                '}';
    }
}
