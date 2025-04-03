package ldk.l.lc.ast.base;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.util.Position;

public abstract class LCAstNode implements Cloneable {
    public Position position;
    public boolean isErrorNode;
    public LCAstNode parentNode = null;

    protected LCAstNode(Position position, boolean isErrorNode) {
        this.position = position;
        this.isErrorNode = isErrorNode;
    }

    public abstract Object accept(LCAstVisitor visitor, Object additional);

    @Override
    public abstract String toString();

    @Override
    public abstract LCAstNode clone() throws CloneNotSupportedException;
}