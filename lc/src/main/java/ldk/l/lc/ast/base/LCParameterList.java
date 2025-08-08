package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.util.Position;

import java.util.ArrayList;
import java.util.List;

public final class LCParameterList extends LCAstNode {
    public List<LCVariableDeclaration> parameters;

    public LCParameterList(List<LCVariableDeclaration> parameters, Position pos) {
        this(parameters, pos, false);
    }

    public LCParameterList(List<LCVariableDeclaration> parameters, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.parameters = parameters;
        for (LCVariableDeclaration p : this.parameters) p.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitParameterList(this, additional);
    }

    @Override
    public String toString() {
        return "LCParameterList{" +
                "parameters=" + parameters +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }
}