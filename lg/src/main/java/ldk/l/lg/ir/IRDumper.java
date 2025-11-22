package ldk.l.lg.ir;

import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.function.IRLocalVariable;
import ldk.l.lg.ir.instruction.*;

import java.io.PrintStream;

public final class IRDumper extends IRVisitor {
    private final PrintStream out;

    public IRDumper(PrintStream out) {
        this.out = out;
    }

    public IRDumper() {
        this(System.out);
    }

    @Override
    public Object visitFunction(IRFunction irFunction, Object prefix) {
        StringBuilder sb = new StringBuilder(prefix.toString());
        sb.append("function ").append(irFunction.returnType).append(" ").append(irFunction.name).append("(");
        for (int i = 0; i < irFunction.args.size(); i++) {
            sb.append(irFunction.args.get(i).toString());
            if (i != irFunction.args.size() - 1) sb.append(", ");
        }
        sb.append(") {");
        out.println(sb);
        for (int i = 0; i < irFunction.locals.size(); i++) {
            out.println(prefix + "\t" + irFunction.locals.get(i).toString()+ (i<irFunction.locals.size()-1?",":""));
        }
        out.println(prefix + "} {");
        if (irFunction.controlFlowGraph != null) {
            for (IRBasicBlock block : irFunction.controlFlowGraph.basicBlocks.values()) {
                out.println(prefix + "\t" + block.name + ":");
                for (IRInstruction instruction : block.instructions) this.visit(instruction, prefix + "\t\t");
            }
        }
        out.println(prefix + "}");
        return null;
    }

    @Override
    public Object visitNop(IRNop irNop, Object prefix) {
        out.println(prefix + irNop.toString());
        return null;
    }

    @Override
    public Object visitReturn(IRReturn irReturn, Object prefix) {
        out.println(prefix + irReturn.toString());
        return null;
    }

    @Override
    public Object visitGoto(IRGoto irGoto, Object prefix) {
        out.println(prefix + irGoto.toString());
        return null;
    }

    @Override
    public Object visitConditionalJump(IRConditionalJump irConditionalJump, Object prefix) {
        out.println(prefix + irConditionalJump.toString());
        return null;
    }

    @Override
    public Object visitCompare(IRCompare irCompare, Object additional) {
        out.println(additional + irCompare.toString());
        return null;
    }

    @Override
    public Object visitStore(IRStore irStore, Object prefix) {
        out.println(prefix + irStore.toString());
        return null;
    }
    @Override
    public Object visitLoad(IRLoad irLoad, Object prefix) {
        out.println(prefix + irLoad.toString());
        return null;
    }

    @Override
    public Object visitBinaryOperates(IRBinaryOperates irBinaryOperates, Object additional) {
        out.println(additional + irBinaryOperates.toString());
        return null;
    }

    @Override
    public Object visitUnaryOperates(IRUnaryOperates irUnaryOperates, Object additional) {
        out.println(additional + irUnaryOperates.toString());
        return null;
    }

    @Override
    public Object visitStackAllocate(IRStackAllocate irStackAllocate, Object additional) {
        out.println(additional + irStackAllocate.toString());
        return null;
    }

    @Override
    public Object visitSetRegister(IRSetRegister irSetRegister, Object additional) {
        out.println(additional + irSetRegister.toString());
        return null;
    }

    @Override
    public Object visitTypeCast(IRTypeCast irTypeCast, Object additional) {
        out.println(additional + irTypeCast.toString());
        return null;
    }

    @Override
    public Object visitInvoke(IRInvoke irInvoke, Object additional) {
        out.println(additional + irInvoke.toString());
        return null;
    }

    @Override
    public Object visitGetElementPointer(IRGetElementPointer irGetElementPointer, Object additional) {
        out.println(additional + irGetElementPointer.toString());
        return null;
    }
}
