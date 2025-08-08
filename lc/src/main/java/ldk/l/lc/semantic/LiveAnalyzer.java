package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCExpressionStatement;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.LCReturn;
import ldk.l.lc.ast.statement.loops.LCFor;
import ldk.l.lc.ast.expression.LCIf;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.error.ErrorStream;

public class LiveAnalyzer extends LCAstVisitor {
    private final ErrorStream errorStream;

    public LiveAnalyzer(ErrorStream errorStream) {
        this.errorStream = errorStream;
    }

    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        if (lcMethodDeclaration.body == null) return true;
        boolean alive = (boolean) this.visitBlock(lcMethodDeclaration.body, additional);

        if (alive) {
            if (SystemTypes.VOID.equals(lcMethodDeclaration.returnType)) {
                Position pos = new Position(lcMethodDeclaration.body.position.endPos(), lcMethodDeclaration.body.position.endPos(), lcMethodDeclaration.body.position.endLine(), lcMethodDeclaration.body.position.endLine(), lcMethodDeclaration.body.position.endCol(), lcMethodDeclaration.body.position.endCol());
                LCReturn returnStatement = new LCReturn(null, pos);
                lcMethodDeclaration.body.statements.add(returnStatement);
                returnStatement.parentNode = lcMethodDeclaration.body;
            } else {
                System.err.println("Method '" + lcMethodDeclaration.symbol.getFullName() + "'locks ending return statement and return type isn't 'void'.");
            }
        }
        return true;
    }

    public Object visitBlock(LCBlock lcBlock, Object additional) {
        boolean alive = true;
        for (LCStatement stmt : lcBlock.statements) {
            Object ret = this.visit(stmt, additional);
            if (alive) {
                alive = !(ret instanceof Boolean booleanValue) || booleanValue;
            } else {
//                this.addWarning("Unreachable code detected.",stmt);
            }
        }
        return alive;
    }

    public Object visitReturn(LCReturn lcReturn, Object additional) {
        if (lcReturn.returnedValue != null) this.visit(lcReturn.returnedValue, additional);
        return false;
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        if (lcVariableDeclaration.init != null) this.visit(lcVariableDeclaration.init, additional);
        return true;
    }

    public Object visitExpressionStatement(LCExpressionStatement lcExpressionStatement, Object additional) {
        return this.visit(lcExpressionStatement.expression, additional);
    }

    public Object visitIf(LCIf lcIf, Object additional) {
        boolean alive;

        if (lcIf.condition.constValue != null && lcIf.condition.constValue.value instanceof Boolean booleanValue) {
            if (booleanValue) {
                alive = (boolean) this.visit(lcIf.then, additional);
            } else {
                if (lcIf._else == null) {
                    alive = true;
                } else {
                    alive = (boolean) this.visit(lcIf.then, additional);
                }
            }
        } else {
            boolean alive1 = !(this.visit(lcIf.then, additional) instanceof Boolean booleanValue) || booleanValue;
            boolean alive2 = lcIf._else == null || (!(this.visit(lcIf._else, additional) instanceof Boolean booleanValue) || booleanValue);
            alive = alive1 || alive2;
        }
        return alive;
    }

    public Object visitFor(LCFor lcFor, Object additional) {
        return this.visit(lcFor.body, additional);
    }
}
