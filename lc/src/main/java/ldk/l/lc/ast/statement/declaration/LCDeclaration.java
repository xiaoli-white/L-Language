package ldk.l.lc.ast.statement.declaration;

import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

public abstract class LCDeclaration extends LCStatement {
    public LCDeclaration(Position position, boolean isErrorNode) {
        super(position, isErrorNode);
    }
}
