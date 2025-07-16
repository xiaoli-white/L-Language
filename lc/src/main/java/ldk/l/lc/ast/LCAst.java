package ldk.l.lc.ast;

import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.file.LCSourceFile;
import ldk.l.lc.ast.statement.declaration.object.LCObjectDeclaration;
import ldk.l.lc.semantic.types.NamedType;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.symbol.MethodSymbol;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LCAst extends LCAstNode {
    public LCObjectDeclaration mainObjectDeclaration = null;
    public MethodSymbol mainMethod = null;
    public final ArrayList<LCSourceFile> sourceFiles = new ArrayList<>();
    public final HashMap<String, NamedType> name2Type = new HashMap<>();

    public LCAst() {
        super(Position.origin, false);
    }

    public NamedType getType(String typeName) {
        return this.name2Type.get(typeName);
    }

    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitAst(this, additional);
    }

    @Override
    public String toString() {
        return "LCAst{" +
                "mainObjectDeclaration=" + mainObjectDeclaration +
                ", mainMethod=" + mainMethod +
                ", sourceFiles=" + sourceFiles +
                ", name2Type=" + name2Type +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCAst clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("'" + this.getClass().getName() + "' not supported clone.");
    }

    public void addSourceFile(LCSourceFile lcSourceFile) {
        lcSourceFile.parentNode = this;
        this.sourceFiles.add(lcSourceFile);
    }

    public boolean containsSourceFile(String filepath) {
        for (LCSourceFile lcSourceFile : this.sourceFiles) {
            if (lcSourceFile.filepath.equals(filepath)) {
                return true;
            }
        }
        return false;
    }

    public LCSourceFile getSourceFile(String filepath) {
        for (LCSourceFile lcSourceFile : this.sourceFiles) {
            if (lcSourceFile.filepath.equals(filepath)) {
                return lcSourceFile;
            }
        }
        return null;
    }

    public LCSourceFile[] getSourceFileByParent(String parent) {
        String p = new File(parent != null ? parent : "").getPath();
        ArrayList<LCSourceFile> lcSourceFiles = new ArrayList<>();
        for (LCSourceFile lcSourceFile : this.sourceFiles) {
            String sourceFileParent = new File(lcSourceFile.filepath).getParent();
            if ((sourceFileParent != null ? sourceFileParent : "").equals(p)) {
                lcSourceFiles.add(lcSourceFile);
            }
        }
        return lcSourceFiles.toArray(new LCSourceFile[0]);
    }

    public LCObjectDeclaration getObjectDeclaration(String _package, String name) {
        return this.getObjectDeclaration(_package + "." + name);
    }

    public LCObjectDeclaration getObjectDeclaration(String fullName) {
        for (LCSourceFile lcSourceFile : this.sourceFiles) {
            LCObjectDeclaration objectDeclaration = lcSourceFile.getObjectDeclaration(fullName);
            if (objectDeclaration != null) return objectDeclaration;
        }
        return null;
    }

    public LCObjectDeclaration[] getObjectDeclarations() {
        ArrayList<LCObjectDeclaration> objectDecls = new ArrayList<>();
        for (LCSourceFile lcSourceFile : this.sourceFiles) {
            objectDecls.addAll(List.of(lcSourceFile.getObjectDeclarations()));
        }
        return objectDecls.toArray(new LCObjectDeclaration[0]);
    }
}