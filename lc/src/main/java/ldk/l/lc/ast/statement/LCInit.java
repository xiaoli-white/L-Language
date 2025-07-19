package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.VariableSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCInit extends LCStatement {
    public boolean isStatic;
    public LCBlock body;
    public List<VariableSymbol> vars;

    public LCInit(boolean isStatic, LCBlock body, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.isStatic = isStatic;
        this.body = body;
        this.body.parentNode = this;
        this.vars = new ArrayList<>();
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitInit(this, additional);
    }

    @Override
    public String toString() {
        return "LCInit{" +
                "isStatic=" + isStatic +
                ", body=" + body +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCInit clone() throws CloneNotSupportedException {
        return new LCInit(isStatic, body.clone(), position.clone(), isErrorNode);
    }
}
