package ldk.l.lc.util.scope;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.statement.LCTry;
import ldk.l.lc.ast.expression.LCIf;
import ldk.l.lc.ast.expression.LCLambda;
import ldk.l.lc.ast.expression.LCSynchronized;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.*;
import ldk.l.lc.ast.statement.loops.*;
import ldk.l.lc.util.symbol.Symbol;
import ldk.l.lc.util.symbol.SymbolDumper;

public class ScopeDumper extends LCAstVisitor {
    private final SymbolDumper symbolDumper = new SymbolDumper();

    @Override
    public Object visitAst(LCAst ast, Object prefix) {
        System.out.println(prefix + "Scope of LCAst");
        super.visitAst(ast, prefix + "\t");
        return null;
    }

    @Override
    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object prefix) {
        System.out.println(prefix + "Scope of SourceCodeFile(filepath: '" + lcSourceCodeFile.filepath + "')");
        this.dumpScope(lcSourceCodeFile.scope, prefix + "\t");
        super.visitSourceCodeFile(lcSourceCodeFile, prefix + "\t");
        return null;
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object prefix) {
        System.out.println(prefix + "Scope of " + lcMethodDeclaration.methodKind + ": '" + lcMethodDeclaration.symbol.getFullName() + "'");

        if (lcMethodDeclaration.scope != null) {
            this.dumpScope(lcMethodDeclaration.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitMethodDeclaration(lcMethodDeclaration, prefix + "\t");

        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object prefix) {
        System.out.println(prefix + "Scope of LCClassDecl: " + lcClassDeclaration.getFullName());

        if (lcClassDeclaration.scope != null && lcClassDeclaration.body.scope != null) {
            this.dumpScope(lcClassDeclaration.scope, prefix + "\t");
            this.dumpScope(lcClassDeclaration.body.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitBlock(lcClassDeclaration.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object prefix) {
        System.out.println(prefix + "Scope of LCInterfaceDecl: " + lcInterfaceDeclaration.getFullName());

        if (lcInterfaceDeclaration.body.scope != null) {
            this.dumpScope(lcInterfaceDeclaration.body.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitBlock(lcInterfaceDeclaration.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitAnnotationDeclaration(LCAnnotationDeclaration lcAnnotationDeclaration, Object prefix) {
        System.out.println(prefix + "Scope of LCAnnotationDecl: " + lcAnnotationDeclaration.getFullName());

        if (lcAnnotationDeclaration.annotationBody.scope != null) {
            this.dumpScope(lcAnnotationDeclaration.annotationBody.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        return null;
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object prefix) {
        System.out.println(prefix + "Scope of LCEnumDecl: " + lcEnumDeclaration.getFullName());

        if (lcEnumDeclaration.scope != null && lcEnumDeclaration.body.scope != null) {
            this.dumpScope(lcEnumDeclaration.scope, prefix + "\t");
            this.dumpScope(lcEnumDeclaration.body.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitBlock(lcEnumDeclaration.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object prefix) {
        System.out.println(prefix + "Scope of LCRecordDecl: " + lcRecordDeclaration.getFullName());

        if (lcRecordDeclaration.scope != null && lcRecordDeclaration.body.scope != null) {
            this.dumpScope(lcRecordDeclaration.scope, prefix + "\t");
            this.dumpScope(lcRecordDeclaration.body.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitBlock(lcRecordDeclaration.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitStructDeclaration(LCStructDeclaration lcStructDeclaration, Object prefix) {
        System.out.println(prefix + "Scope of LCStructDecl: " + lcStructDeclaration.getFullName());

        if (lcStructDeclaration.body.scope != null) {
            this.dumpScope(lcStructDeclaration.body.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitBlock(lcStructDeclaration.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitBlock(LCBlock lcBlock, Object prefix) {
        System.out.println(prefix + "Scope of LCBlock");

        if (lcBlock.scope != null) {
            this.dumpScope(lcBlock.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitBlock(lcBlock, prefix + "\t");

        return null;
    }

    @Override
    public Object visitFor(LCFor lcFor, Object prefix) {
        System.out.println(prefix + "Scope of LCFor");

        if (lcFor.scope != null) {
            this.dumpScope(lcFor.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        this.visit(lcFor.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitForeach(LCForeach lcForeach, Object prefix) {
        System.out.println(prefix + "Scope of LCForeach");

        if (lcForeach.scope != null) {
            this.dumpScope(lcForeach.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        this.visit(lcForeach.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitWhile(LCWhile lcWhile, Object prefix) {
        System.out.println(prefix + "Scope of LCWhile");

        if (lcWhile.scope != null) {
            this.dumpScope(lcWhile.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        this.visit(lcWhile.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitDoWhile(LCDoWhile lcDoWhile, Object prefix) {
        System.out.println(prefix + "Scope of LCDoWhile");

        if (lcDoWhile.scope != null) {
            this.dumpScope(lcDoWhile.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        this.visit(lcDoWhile.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitLoop(LCLoop lcLoop, Object prefix) {
        System.out.println(prefix + "Scope of LCFor");

        if (lcLoop.scope != null) {
            this.dumpScope(lcLoop.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        this.visit(lcLoop.body, prefix + "\t");

        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object prefix) {
        System.out.println(prefix + "Scope of LCIf");

        if (lcIf.scope != null) {
            this.dumpScope(lcIf.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        this.visit(lcIf.then, prefix + "\t");

        if (lcIf._else != null)
            this.visit(lcIf._else, prefix + "\t");

        return null;
    }

    @Override
    public Object visitLambda(LCLambda lcLambda, Object prefix) {
        System.out.println(prefix + "Scope of LCLambda");

        if (lcLambda.scope != null) {
            this.dumpScope(lcLambda.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitLambda(lcLambda, prefix + "\t");

        return null;
    }

    @Override
    public Object visitSynchronized(LCSynchronized lcSynchronized, Object prefix) {
        System.out.println(prefix + "Scope of LCSynchronized");

        if (lcSynchronized.scope != null) {
            this.dumpScope(lcSynchronized.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitSynchronized(lcSynchronized, prefix + "\t");

        return null;
    }

    @Override
    public Object visitTry(LCTry lcTry, Object prefix) {
        System.out.println(prefix + "Scope of LCTry");

        if (lcTry.scope != null) {
            this.dumpScope(lcTry.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitTry(lcTry, prefix + "\t");

        return null;
    }

    @Override
    public Object visitCatch(LCTry.LCCatch lcCatch, Object prefix) {
        System.out.println(prefix + "Scope of LCCatch");

        if (lcCatch.scope != null) {
            this.dumpScope(lcCatch.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitCatch(lcCatch, prefix + "\t");

        return null;
    }

    @Override
    public Object visitWith(LCWith lcWith, Object prefix) {
        System.out.println(prefix + "Scope of LCWith");

        if (lcWith.scope != null) {
            this.dumpScope(lcWith.scope, prefix + "\t");
        } else {
            System.out.println(prefix + "\t{null}");
        }

        super.visitWith(lcWith, prefix + "\t");

        return null;
    }

    public void dumpScope(Scope scope, String prefix) {
        if (!scope.name2symbol.isEmpty()) {
            for (Symbol symbol : scope.name2symbol.values())
                this.symbolDumper.visit(symbol, prefix);
        } else {
            System.out.println(prefix + "{empty}");
        }
    }
}