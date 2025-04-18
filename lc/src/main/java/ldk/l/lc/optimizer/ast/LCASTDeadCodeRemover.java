package ldk.l.lc.optimizer.ast;

import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.semantic.LiveAnalyzer;
import ldk.l.lc.util.error.ErrorStream;

import java.util.ArrayList;
import java.util.List;

public final class LCASTDeadCodeRemover extends LiveAnalyzer {
    public LCASTDeadCodeRemover(ErrorStream errorStream) {
        super(errorStream, true);
    }

    public Object visitBlock(LCBlock lcBlock, Object additional) {
        boolean alive = true;
        ArrayList<LCStatement> deadCodes = new ArrayList<>();
        for (LCStatement stmt : lcBlock.statements) {
            if (alive) {
                Object ret = this.visit(stmt, additional);
                alive = ret instanceof Boolean && (boolean) ret;
            } else {
                deadCodes.add(stmt);
            }
        }

        for (LCStatement stmt : deadCodes) {
            lcBlock.statements.remove(stmt);
        }

        return alive;
    }
}