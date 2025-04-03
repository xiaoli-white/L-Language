package ldk.l.lc.ast.file;

import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.statement.declaration.object.LCObjectDeclaration;
import ldk.l.lc.util.Position;

public abstract class LCSourceFile extends LCAstNode {
    public String filepath;

    public LCSourceFile(String filepath, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.filepath = filepath;
    }

    @Override
    public abstract LCSourceFile clone() throws CloneNotSupportedException;

    public abstract LCObjectDeclaration[] getObjectDeclarations();

    public abstract LCObjectDeclaration getObjectDeclaration(String fullName);
}
