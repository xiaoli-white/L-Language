package ldk.l.lc.ast.file;

import ldk.l.lc.ast.LCAstUtil;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.statement.declaration.object.LCObjectDeclaration;
import ldk.l.lc.ast.statement.LCImport;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LCSourceCodeFile extends LCSourceFile {
    public String packageName = null;
    public Scope scope = null;
    public LCBlock body;
    public LCSourceFileProxy[] proxies = new LCSourceFileProxy[0];

    public LCSourceCodeFile(String filename, LCBlock body, Position pos, boolean isErrorNode) {
        super(filename, pos, isErrorNode);
        this.body = body;
        this.body.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitSourceCodeFile(this, additional);
    }

    @Override
    public String toString() {
        return "LCSourceCodeFile{" +
                "packageName='" + packageName + '\'' +
                ", body=" + body +
                ", proxies=" + Arrays.toString(proxies) +
                ", filepath='" + filepath + '\'' +
                ", isErrorNode=" + isErrorNode +
                ", position=" + position +
                '}';
    }

    @Override
    public LCSourceCodeFile clone() throws CloneNotSupportedException {
        return new LCSourceCodeFile(this.filepath, this.body.clone(), this.position.clone(), this.isErrorNode);
    }

    @Override
    public LCObjectDeclaration[] getObjectDeclarations() {
        ArrayList<LCObjectDeclaration> objectDeclarations = new ArrayList<>();
        for (LCStatement LCStatement : this.body.statements) {
            if (LCStatement instanceof LCObjectDeclaration objectDeclaration) {
                objectDeclarations.add(objectDeclaration);
                objectDeclarations.addAll(List.of(LCAstUtil.getObjectDeclarations(objectDeclaration)));
            }
        }
        return objectDeclarations.toArray(new LCObjectDeclaration[0]);
    }

    @Override
    public LCObjectDeclaration getObjectDeclaration(String fullName) {
        for (LCObjectDeclaration objectDecl : this.getObjectDeclarations()) {
            if (objectDecl.getFullName().equals(fullName)) return objectDecl;
        }
        return null;
    }

    public LCObjectDeclaration getObjectDeclarationByName(String name) {
        for (LCStatement LCStatement : this.body.statements) {
            if (LCStatement instanceof LCObjectDeclaration objectDecl && objectDecl.name.equals(name))
                return objectDecl;
        }
        return null;
    }

    public LCImport[] getImportStatements() {
        ArrayList<LCImport> lcImports = new ArrayList<>();
        for (LCStatement LCStatement : this.body.statements) {
            if (LCStatement instanceof LCImport lcImport) lcImports.add(lcImport);
        }
        return lcImports.toArray(new LCImport[0]);
    }
}
