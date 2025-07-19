package ldk.l.lc.ast.statement;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCAstNode;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.ast.base.LCStatementWithScope;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.util.Position;
import ldk.l.lc.util.scope.Scope;

import java.util.ArrayList;
import java.util.List;

public final class LCTry extends LCStatementWithScope {
    public List<LCStatement> resources;
    public LCStatement base;
    public List<LCCatch> catchers;
    public LCStatement _finally;

    public LCTry(List<LCStatement> resources, LCStatement base, List<LCCatch> catchers, LCStatement _finally, Position pos, boolean isErrorNode) {
        super(pos, isErrorNode);
        this.resources = resources;
        for (LCStatement resource : this.resources) resource.parentNode = this;

        this.base = base;
        this.base.parentNode = this;

        this.catchers = catchers;
        for (LCCatch catcher : this.catchers) catcher.parentNode = this;

        this._finally = _finally;
        if (this._finally != null) this._finally.parentNode = this;
    }

    @Override
    public Object accept(LCAstVisitor visitor, Object additional) {
        return visitor.visitTry(this, additional);
    }

    @Override
    public String toString() {
        return "LCTry{" +
                "resources=" + resources +
                ", base=" + base +
                ", catchers=" + catchers +
                ", _finally=" + _finally +
                ", annotations=" + annotations +
                ", position=" + position +
                ", isErrorNode=" + isErrorNode +
                '}';
    }

    @Override
    public LCTry clone() throws CloneNotSupportedException {
        return new LCTry(new ArrayList<>(resources), base.clone(), new ArrayList<>(catchers), _finally != null ? _finally.clone() : null, position.clone(), isErrorNode);
    }

    public static final class LCCatch extends LCAstNode {
        public Scope scope = null;
        public LCVariableDeclaration exceptionVariableDeclaration;
        public LCStatement then;

        public LCCatch(LCVariableDeclaration exceptionVariableDeclaration, LCStatement then, Position pos, boolean isErrorNode) {
            super(pos, isErrorNode);

            this.exceptionVariableDeclaration = exceptionVariableDeclaration;
            this.exceptionVariableDeclaration.parentNode = this;

            this.then = then;
            this.then.parentNode = this;
        }

        @Override
        public Object accept(LCAstVisitor visitor, Object additional) {
            return visitor.visitCatch(this, additional);
        }

        @Override
        public String toString() {
            return "LCCatch{" +
                    "scope=" + scope +
                    ", exceptionVariableDeclaration=" + exceptionVariableDeclaration +
                    ", then=" + then +
                    ", position=" + position +
                    ", isErrorNode=" + isErrorNode +
                    '}';
        }

        @Override
        public LCCatch clone() throws CloneNotSupportedException {
            return new LCCatch(exceptionVariableDeclaration.clone(), then.clone(), position.clone(), isErrorNode);
        }
    }
}
