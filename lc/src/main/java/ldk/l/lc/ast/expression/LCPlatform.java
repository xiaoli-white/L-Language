package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.ConstValue;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.util.Position;

import java.util.Objects;

public class LCPlatform extends LCExpression {
    public LCPlatform(String _platform, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.constValue = new ConstValue(_platform);
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitPlatform(this, additional);
    }

    @Override
    public String toString() {
        return "LCPlatform{" +
                "theType=" + theType +
                ", constValue=" + constValue +
                ", isLeftValue=" + isLeftValue +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCPlatform clone() throws CloneNotSupportedException {
        return new LCPlatform(constValue.toString(), position.clone(), isErrorNode);
    }
}
