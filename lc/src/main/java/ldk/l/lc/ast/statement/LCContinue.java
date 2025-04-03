package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCContinue extends LCStatement {
    public String label;

    public LCContinue(String label, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.label = label;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitContinue(this, additional);
    }

    @Override
    public String toString() {
        return "LCContinue{" +
                "label='" + label + '\'' +
                ", annotations=" + Arrays.toString(annotations) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCContinue clone() throws CloneNotSupportedException {
        return new LCContinue(label, position.clone(), isErrorNode);
    }
}
