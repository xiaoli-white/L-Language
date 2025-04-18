package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCParameterList;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.expression.LCBinary;
import ldk.l.lc.ast.statement.LCReturn;
import ldk.l.lc.ast.statement.loops.LCFor;
import ldk.l.lc.ast.expression.LCIf;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.ast.expression.LCVariable;
import ldk.l.lc.token.Token;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.symbol.VariableSymbol;

import java.util.HashMap;

public final class AssignAnalyzer extends LCAstVisitor {
    private final ErrorStream errorStream;
    private HashMap<VariableSymbol, Boolean> assignMode;

    public AssignAnalyzer(ErrorStream errorStream) {
        this.errorStream = errorStream;
        this.assignMode = new HashMap<>();
    }

    private HashMap<VariableSymbol, Boolean> cloneMap(HashMap<VariableSymbol, Boolean> map1) {
        HashMap<VariableSymbol, Boolean> map2 = new HashMap<>();
        for (VariableSymbol symbol : map1.keySet()) {
            Boolean value = map1.get(symbol);
            map2.put(symbol, value);
        }
        return map2;
    }

    private HashMap<VariableSymbol, Boolean> merge(HashMap<VariableSymbol, Boolean> map1, HashMap<VariableSymbol, Boolean> map2) {
        HashMap<VariableSymbol, Boolean> map = new HashMap<>();
        for (VariableSymbol symbol : map1.keySet()) {
            Boolean value1 = map1.get(symbol);
            Boolean value2 = map2.get(symbol);
            map.put(symbol, value1 && value2);
        }
        return map;
    }

    public Object visitAst(LCAst ast, Object additional) {
        this.assignMode = new HashMap<>();

        super.visitAst(ast, additional);

        return this.assignMode;
    }

    public Object visitParameterList(LCParameterList lcParameterList, Object additional) {
        for (LCVariableDeclaration lcVariableDeclaration : lcParameterList.parameters) {
            this.assignMode.put(lcVariableDeclaration.symbol, true);
        }

        return null;
    }

    public Object visitBlock(LCBlock lcBlock, Object additional) {
        for (LCStatement stmt : lcBlock.statements) {
            Object alive = this.visit(stmt, additional);
            if (alive instanceof Boolean && ((boolean) alive)) {
                break;
            }
        }
        return null;
    }

    public Object visitReturn(LCReturn lcReturn, Object additional) {
        if (lcReturn.returnedValue != null)
            this.visit(lcReturn.returnedValue, additional);
        return false;
    }

    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        if (lcVariableDeclaration.init != null)
            this.visit(lcVariableDeclaration.init, additional);
        this.assignMode.put(lcVariableDeclaration.symbol, lcVariableDeclaration.init != null);

        return null;
    }

    @Override
    public Object visitVariable(LCVariable lcVariable, Object additional) {
        if (lcVariable.symbol != null) {
            if (this.assignMode.containsKey(lcVariable.symbol)) {
                boolean assigned = this.assignMode.get(lcVariable.symbol);
                if (!assigned) {
                    System.err.println("Variable '" + lcVariable.name + "' is used before being assigned.");
                }
            } else {
//                console.log("whoops,不可能到这里@semantic.ts/visitVariable");
            }
        }

        return null;
    }

    @Override
    public Object visitBinary(LCBinary lcBinary, Object additional) {
        if (Token.isAssignOperator(lcBinary._operator)) {
            this.visit(lcBinary.expression2, additional);
            if (lcBinary.expression1 instanceof LCVariable lcVariable) {
                VariableSymbol variableSymbol = lcVariable.symbol;
                this.assignMode.put(variableSymbol, true);
            }
        } else {
            super.visitBinary(lcBinary, additional);
        }

        return null;
    }

    @Override
    public Object visitIf(LCIf lcIf, Object additional) {
        // TODO if条件有没有常量的值，是否为常真或常假
        if (lcIf.condition.constValue != null) {
            if (lcIf._else == null) {

            } else {

            }
        } else {
            HashMap<VariableSymbol, Boolean> oldMode = this.cloneMap(this.assignMode);
            this.visit(lcIf.then, additional);
            HashMap<VariableSymbol, Boolean> mode1 = this.assignMode;
            this.assignMode = this.cloneMap(oldMode);
            if (lcIf._else != null)
                this.visit(lcIf._else, additional);

            HashMap<VariableSymbol, Boolean> mode2 = this.assignMode;
            this.assignMode = this.merge(mode1, mode2);
        }

        return null;
    }

    @Override
    public Object visitFor(LCFor lcFor, Object additional) {
        if (lcFor.init != null)
            super.visit(lcFor.init, additional);

        boolean skipLoop = lcFor.condition != null && lcFor.condition.constValue.value instanceof Boolean boolean_value
                && !boolean_value;
        if (!skipLoop) {
            this.visit(lcFor.body, additional);
            if (lcFor.increment != null)
                this.visit(lcFor.increment, additional);
        }

        return null;
    }
}