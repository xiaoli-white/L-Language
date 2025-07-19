package ldk.l.lc.ast.expression.type;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.token.Tokens;
import ldk.l.lc.util.Position;

public final class LCPredefinedTypeExpression extends LCTypeExpression {
    public Tokens.Type keyword;

    public LCPredefinedTypeExpression(Tokens.Type keyword, Position pos) {
        this(keyword, pos, false);
    }

    public LCPredefinedTypeExpression(Tokens.Type keyword, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.keyword = keyword;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitPredefinedTypeExpression(this, additional);
    }

    @Override
    public String toString() {
        return "LCPredefinedTypeExpression{" +
                "keyword=" + keyword +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", theType=" + theType +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public String toTypeString() {
        return keyword.getCode();
    }

    @Override
    public LCPredefinedTypeExpression clone() throws CloneNotSupportedException {
        return new LCPredefinedTypeExpression(this.keyword, this.position.clone(), this.isErrorNode);
    }
}
