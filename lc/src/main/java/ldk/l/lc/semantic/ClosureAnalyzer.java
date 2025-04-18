package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.expression.LCVariable;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.symbol.Closure;
import ldk.l.lc.util.symbol.MethodSymbol;
import ldk.l.lc.util.symbol.VariableSymbol;
import ldk.l.util.option.Options;

import java.util.ArrayList;
import java.util.List;

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
        if (this.options.getBooleanVar("verbose")) {
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

        if (this.options.getBooleanVar("verbose")) {
            if (this.getCurrentClosure().vars.length > 0) {
                System.out.println("    Closure of method:" + (lcMethodDeclaration.symbol != null ? lcMethodDeclaration.symbol.name : "Unknown"));
                System.out.println("    " + this.getCurrentClosure().toString());
            }
        }

        this.closures.removeLast();
        this.methodSymbols.removeLast();

        return null;
    }

    public Object visitVariable(LCVariable lcVariable) {
        // TODO 变量如果引用的是内部的函数，就不管了。但有没有可能引用的是外部的函数呢？
        // 如果引用消解不成功，这里也不管
        if (lcVariable.aSymbol instanceof VariableSymbol && this.getCurrentMethodSymbol() != null) {
            if (!List.of(this.getCurrentMethodSymbol().vars).contains((VariableSymbol) lcVariable.aSymbol)) {
                // 查找变量所在的函数
                boolean found = false;
                for (int i = this.methodSymbols.size() - 1; i >= 0; i--) {
                    MethodSymbol methodSymbol = this.methodSymbols.get(i);
                    if (methodSymbol != null &&
                            List.of(methodSymbol.vars).contains((VariableSymbol) lcVariable.aSymbol)) {
                        ArrayList<VariableSymbol> tempArrayList1 = new ArrayList<>(List.of(this.getCurrentClosure().vars));
                        tempArrayList1.add((VariableSymbol) lcVariable.aSymbol);
                        this.getCurrentClosure().vars = tempArrayList1.toArray(new VariableSymbol[0]);

                        ArrayList<MethodSymbol> tempArrayList2 = new ArrayList<>(List.of(this.getCurrentClosure().methods));
                        tempArrayList2.add(methodSymbol);
                        this.getCurrentClosure().methods = tempArrayList2.toArray(new MethodSymbol[0]);

                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // 理论上不可能发生
//                    this.addError("Cannot find VarSymbol: '" + lcVariable.sym.name + "' in Closure Analysis.", lcVariable);
                }
            }
        }

        return null;
    }
}
