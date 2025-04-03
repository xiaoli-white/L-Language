package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCParameterList extends LCAstNode {
    public LCVariableDeclaration[] parameters;

    public LCParameterList(LCVariableDeclaration[] parameters, Position pos) {
        this(parameters, pos, false);
    }

    public LCParameterList(LCVariableDeclaration[] parameters, Position pos, boolean isErrorNode) {
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
                "parameters=" + Arrays.toString(parameters) +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCParameterList clone() throws CloneNotSupportedException {
        return new LCParameterList(Arrays.copyOf(this.parameters, this.parameters.length), this.position.clone(), this.isErrorNode);
    }
}