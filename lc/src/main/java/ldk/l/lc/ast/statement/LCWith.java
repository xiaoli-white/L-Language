package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

import java.util.ArrayList;
import java.util.List;

public final class LCWith extends LCStatementWithScope {
    public List<LCVariableDeclaration> resources;
    public LCStatement body;
    public MethodSymbol methodSymbol = null;

    public LCWith(List<LCVariableDeclaration> resources, LCStatement body, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.resources = resources;
        for (LCStatement resource : resources) resource.parentNode = this;
        this.body = body;
        this.body.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitWith(this, additional);
    }

    @Override
    public String toString() {
        return "LCWith{" +
                "resources=" + resources +
                ", body=" + body +
                ", scope=" + scope +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}
