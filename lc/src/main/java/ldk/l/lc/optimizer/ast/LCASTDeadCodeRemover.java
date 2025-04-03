package ldk.l.lc.optimizer.ast;

import ldk.l.lc.ast.base.LCBlock;
import ldk.l.lc.ast.base.LCStatement;
import ldk.l.lc.semantic.LiveAnalyzer;
import ldk.l.lc.util.error.ErrorStream;

import java.util.ArrayList;
import java.util.List;

public class LCASTDeadCodeRemover extends LiveAnalyzer {
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

        ArrayList<LCStatement> arrayList = new ArrayList<>(List.of(lcBlock.statements));
        for (LCStatement stmt : deadCodes) {
            arrayList.remove(stmt);
        }
        lcBlock.statements = arrayList.toArray(new LCStatement[0]);

        return alive;
    }
}