package ldk.l.lc.ast.statement.loops;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.util.Position;

public final class LCForeach extends LCAbstractLoop {
    public LCVariableDeclaration init;
    public LCExpression source;
    public LCStatement body;

    public LCForeach(LCVariableDeclaration init, LCExpression source, LCStatement body, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.init = init;
        this.init.parentNode = this;

        this.source = source;
        this.source.parentNode = this;

        this.body = body;
        this.body.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitForeach(this, additional);
    }

    @Override
    public String toString() {
        return "LCForeach{" +
                "init=" + init +
                ", source=" + source +
                ", body=" + body +
                ", scope=" + scope +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}