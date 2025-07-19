package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

public final class LCImport extends LCStatement {
    public LCImport.LCImportKind kind;
    public String name;
    public String alias;

    public LCImport(LCImport.LCImportKind kind, String name, Position pos, boolean isErrorNode) {
        this(kind, name, null, pos, isErrorNode);
    }

    public LCImport(LCImport.LCImportKind kind, String name, String alias, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.kind = kind;

        this.name = name;
        this.alias = alias;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitImport(this, additional);
    }

    @Override
    public String toString() {
        return "LCImport{" +
                "kind=" + kind +
                ", name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCImport clone() throws CloneNotSupportedException {
        return new LCImport(kind, name, alias, position.clone(), isErrorNode);
    }

    public String getPackageName() {
        if (this.kind != LCImportKind.Normal)
            throw new RuntimeException();

        int index = this.name.lastIndexOf(".");
        if (index != -1)
            return this.name.substring(0, index);
        else
            return this.name;
    }

    public String getName() {
        if (this.kind != LCImportKind.Normal)
            throw new RuntimeException();

        int index = this.name.lastIndexOf(".");
        if (index != -1)
            return this.name.substring(index + 1);
        else
            return this.name;
    }

    public enum LCImportKind {
        Normal,
        Dynamic,
        Static,
        Native
    }
}
