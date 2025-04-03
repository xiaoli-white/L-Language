package ldk.l.lc.ast.expression;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCExpression;
import ldk.l.lc.ast.base.LCExpressionWithScope;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.util.Position;

import java.util.Arrays;
import java.util.Objects;

public class LCCase extends LCExpressionWithScope {
    public LCCaseKind kind;
    public LCCaseLabel[] labels;
    public LCExpression guard;
    public LCStatement[] statements;
    public boolean completesNormally;

    public LCCase(LCCaseKind kind, LCCaseLabel[] labels, LCExpression guard, LCStatement[] statements, boolean completesNormally, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.kind = kind;

        this.labels = labels;
        for (LCCaseLabel label : this.labels) label.parentNode = this;

        this.guard = guard;
        if (this.guard != null) this.guard.parentNode = this;

        this.statements = statements;
        for (LCStatement statement : this.statements) statement.parentNode = this;

        this.completesNormally = completesNormally;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitCase(this, additional);
    }

    @Override
    public String toString() {
        return "LCCase{" +
                "kind=" + kind +
                ", labels=" + Arrays.toString(labels) +
                ", guard=" + guard +
                ", statements=" + Arrays.toString(statements) +
                ", completesNormally=" + completesNormally +
                ", scope=" + scope +
                ", theType=" + theType +
                ", shouldBeLeftValue=" + shouldBeLeftValue +
                ", isLeftValue=" + isLeftValue +
                ", constValue=" + constValue +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCCase clone() throws CloneNotSupportedException {
        return new LCCase(kind, Arrays.copyOf(labels, labels.length), guard != null ? guard.clone() : null, Arrays.copyOf(statements, statements.length), completesNormally, position.clone(), isErrorNode);
    }

    public enum LCCaseKind {
        STATEMENT,
        RULE
    }
}
