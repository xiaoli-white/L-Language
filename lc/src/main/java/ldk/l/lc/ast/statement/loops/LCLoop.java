package ldk.l.lc.ast.statement.loops;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.util.Position;

public class LCLoop extends LCAbstractLoop {
    public LCStatement body;

    public LCLoop(LCStatement body, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.body = body;
        this.body.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitLoop(this, additional);
    }

    @Override
    public String toString() {
        return "LCLoop{" +
                "body=" + body +
                ", isErrorNode=" + isErrorNode +
                ", position=" + position +
                '}';
    }

    @Override
    public LCLoop clone() throws CloneNotSupportedException {
        return new LCLoop(body.clone(), position.clone(), isErrorNode);
    }
}
