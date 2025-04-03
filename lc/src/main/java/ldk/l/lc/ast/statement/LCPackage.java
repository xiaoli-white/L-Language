package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCPackage extends LCStatement {
    public String name;

    public LCPackage(String name, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.name = name;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitPackage(this, additional);
    }

    @Override
    public String toString() {
        return "LCPackage{" +
                "name='" + name + '\'' +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCPackage clone() throws CloneNotSupportedException {
        return new LCPackage(name, position.clone(), isErrorNode);
    }
}
