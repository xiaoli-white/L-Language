package ldk.l.lg.ir;

import ldk.l.lg.ir.base.IRBasicBlock;
import ldk.l.lg.ir.base.IRGlobalVariable;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.function.IRLocalVariable;
import ldk.l.lg.ir.instruction.*;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;

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
    public Object visitStructure(IRStructure irStructure, Object additional) {
        out.println(additional + "structure " + irStructure.name+" {");
        for (int i = 0; i < irStructure.fields.size(); i++) {
            IRField field = irStructure.fields.get(i);
            out.println(additional + "\t" + field.type + " " + field.name+(i < irStructure.fields.size()-1?",":""));
        }
        out.println(additional + "}");
        return null;
    }

    @Override
    public Object visitGlobalVariable(IRGlobalVariable globalVariable, Object additional) {
        out.println(additional + "global " + globalVariable.toString());
        return null;
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
                for (IRInstruction instruction : block.instructions) out.println(prefix + "\t\t" + instruction.toString());
            }
        }
        out.println(prefix + "}");
        return null;
    }
}
