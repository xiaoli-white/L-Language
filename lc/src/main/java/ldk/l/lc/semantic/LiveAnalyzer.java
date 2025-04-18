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

import java.util.ArrayList;
import java.util.List;

public class LiveAnalyzer extends LCAstVisitor {
    private final ErrorStream errorStream;
    private final boolean isDeadCodeRemover;

    public LiveAnalyzer(ErrorStream errorStream) {
        this(errorStream, false);
    }

    public LiveAnalyzer(ErrorStream errorStream, boolean isDeadCodeRemover) {
        this.errorStream = errorStream;
        this.isDeadCodeRemover = isDeadCodeRemover;
    }

    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        boolean alive = (boolean) this.visitBlock(lcMethodDeclaration.body, additional);

        if (alive) {
            if (lcMethodDeclaration.returnType == SystemTypes.VOID) {
                Position pos = new Position(lcMethodDeclaration.body.position.endPos(), lcMethodDeclaration.body.position.endPos(), lcMethodDeclaration.body.position.endLine(), lcMethodDeclaration.body.position.endLine(), lcMethodDeclaration.body.position.endCol(), lcMethodDeclaration.body.position.endCol());
                lcMethodDeclaration.body.statements.add(new LCReturn(null, pos));
            } else {
                if (!this.isDeadCodeRemover) {
//                this.addError("Function lacks ending return LCStatement and return type does not include 'undefined'.", functionDecl);
                }
            }
        }
        return true;
    }

    public Object visitBlock(LCBlock lcBlock, Object additional) {
        boolean alive = true;
        for (LCStatement stmt : lcBlock.statements) {
            if (alive) {
                Object ret = this.visit(stmt, additional);
                alive = ret instanceof Boolean && (boolean) ret;
            } else {
//                this.addWarning("Unreachable code detected.",stmt);
            }
        }
        return alive;
    }

    public Object visitReturn(LCReturn lcReturn, Object additional) {
        return false;
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        return true;
    }

    public Object visitExpressionStatement(LCExpressionStatement lcExpressionStatement, Object additional) {
        return true;
    }

    public Object visitIf(LCIf lcIf, Object additional) {
        boolean alive;

        if (lcIf.condition.constValue.value instanceof Boolean boolean_value) {
            if (boolean_value) {
                alive = (boolean) this.visit(lcIf.then, additional);
            } else {
                if (lcIf._else == null) {
                    alive = true;
                } else {
                    alive = (boolean) this.visit(lcIf.then, additional);
                }
            }
        } else {
            boolean alive1 = (boolean) this.visit(lcIf.then, additional);
            boolean alive2 = lcIf._else == null || (boolean) this.visit(lcIf._else, additional);
            alive = alive1 || alive2;
        }
        return alive;
    }

    public Object visitFor(LCFor lcFor, Object additional) {
        return this.visit(lcFor.body, additional);
    }
}
