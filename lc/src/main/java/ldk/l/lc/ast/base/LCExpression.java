package ldk.l.lc.ast.base;

import ldk.l.lc.util.ConstValue;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lc.util.Position;

public abstract class LCExpression extends LCAstNode {
    public Type theType = null;
    public boolean shouldBeLeftValue = false;
    public boolean isLeftValue = false;
    public ConstValue constValue = null;

    protected LCExpression(Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
    }
}