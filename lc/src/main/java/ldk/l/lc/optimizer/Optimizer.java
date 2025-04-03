package ldk.l.lc.optimizer;

import ldk.l.lc.ast.LCAst;
import ldk.l.lc.optimizer.ast.LCASTDeadCodeRemover;
import ldk.l.lc.util.error.ErrorStream;

public class Optimizer {
    private final LCAst ast;
    private final LCASTDeadCodeRemover ASTDeadCodeRemover;
    public Optimizer(LCAst ast, ErrorStream errorStream) {
        this.ast = ast;

        this.ASTDeadCodeRemover = new LCASTDeadCodeRemover(errorStream);
    }

    public void optimizeAST(int level) {
        switch (level){
            case 1->this.optimizeASTLevel1();
            case 2->this.optimizeASTLevel2();
            case 3->this.optimizeASTLevel3();
        }
    }
    public void optimizeASTLevel1(){
        this.ASTDeadCodeRemover.visitAst(this.ast, null);
    }
    public void optimizeASTLevel2() {
        this.optimizeASTLevel1();
    }
    public void optimizeASTLevel3() {
        this.optimizeASTLevel1();
        this.optimizeASTLevel2();
    }
}
