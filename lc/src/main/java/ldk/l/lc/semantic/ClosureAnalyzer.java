package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.symbol.Closure;
import ldk.l.lc.util.symbol.MethodSymbol;
import ldk.l.util.option.Options;

import java.util.ArrayList;

public final class ClosureAnalyzer extends LCAstVisitor {
    private final ErrorStream errorStream;
    private final Options options;
    private final ArrayList<Closure> closures = new ArrayList<>();
    private final ArrayList<MethodSymbol> methodSymbols = new ArrayList<>();

    public ClosureAnalyzer(ErrorStream errorStream, Options options) {
        this.errorStream = errorStream;
        this.options = options;
    }

    private Closure getCurrentClosure() {
        return this.closures.getLast();
    }

    private MethodSymbol getCurrentMethodSymbol() {
        return this.methodSymbols.getLast();
    }

    public Object visitAst(LCAst ast, Object additional) {
        if (this.options.get("verbose",boolean.class)) {
            System.out.println("闭包分析...");
        }

        this.closures.add(new Closure());
        this.methodSymbols.add(ast.mainMethod);

        super.visitAst(ast, additional);

        if (ast.mainMethod != null)
            ast.mainMethod.closure = this.getCurrentClosure();

        this.closures.removeLast();
        this.methodSymbols.removeLast();

        return null;
    }

    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        this.closures.add(new Closure());
        this.methodSymbols.add(lcMethodDeclaration.symbol);

        super.visitMethodDeclaration(lcMethodDeclaration, additional);

        if (lcMethodDeclaration.symbol != null)
            lcMethodDeclaration.symbol.closure = this.getCurrentClosure();

        if (this.options.get("verbose",boolean.class)) {
            if (this.getCurrentClosure().vars.length > 0) {
                System.out.println("    Closure of method:" + (lcMethodDeclaration.symbol != null ? lcMethodDeclaration.symbol.name : "Unknown"));
                System.out.println("    " + this.getCurrentClosure().toString());
            }
        }

        this.closures.removeLast();
        this.methodSymbols.removeLast();

        return null;
    }
}
