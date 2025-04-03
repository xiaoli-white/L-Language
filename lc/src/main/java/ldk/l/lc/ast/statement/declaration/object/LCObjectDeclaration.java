package ldk.l.lc.ast.statement.declaration.object;

import ldk.l.lc.ast.LCAstUtil;
import ldk.l.lc.ast.base.*;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.file.LCSourceFile;
import ldk.l.lc.ast.statement.declaration.LCDeclaration;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;

public abstract class LCObjectDeclaration extends LCDeclaration {
    public Scope scope = null;
    public LCModifier modifier = null;
    public String name;
    public LCTypeParameter[] typeParameters;
    public LCBlock body;

    public LCObjectDeclaration(String name, LCTypeParameter[] typeParameters, LCBlock body, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.name = name;

        this.typeParameters = typeParameters;
        for (LCTypeParameter typeParameter : this.typeParameters) typeParameter.parentNode = this;

        this.body = body;
        if (this.body != null) this.body.parentNode = this;
    }

    @Override
    public abstract LCObjectDeclaration clone() throws CloneNotSupportedException;

    public final void setModifier(LCModifier modifier) {
        this.modifier = modifier;
        if (this.modifier != null) this.modifier.parentNode = this;
    }

    public String getFullName() {
        String packageName = this.getPackageName();
        return packageName != null ? packageName + "." + this.getRealName() : this.getRealName();
    }

    public String getRealName() {
        StringBuilder name = new StringBuilder(this.name);
        LCAstNode node = this.parentNode;
        while (node != null && !(node instanceof LCSourceFile)) {
            if (node instanceof LCObjectDeclaration objectDeclaration)
                name.insert(0, objectDeclaration.name + ".");
            node = node.parentNode;
        }
        return name.toString();
    }

    public String getPackageName() {
        return LCAstUtil.getSourceFile(this) instanceof LCSourceCodeFile sourceCodeFile ? sourceCodeFile.packageName : null;
    }

    public String getRealPackageName() {
        String fullName = this.getFullName();
        int index = fullName.lastIndexOf(".");
        if (index != -1)
            return fullName.substring(0, index);
        else
            return fullName;
    }
}
