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
    public Object visitStructure(IRStructure irStructure, Object prefix) {
        StringBuilder sb = new StringBuilder(prefix.toString());
        for (String attribute : irStructure.attributes) {
            sb.append("__attribute__(\"").append(attribute).append("\") ");
        }
        sb.append("structure ").append(irStructure.name).append(" {");
        out.println(sb);
        for (int i = 0; i < irStructure.fields.size(); i++) {
            IRField field = irStructure.fields.get(i);
            out.println(prefix + "\t" + field.type + " " + field.name + (i < irStructure.fields.size() - 1 ? "," : ""));
        }
        out.println(prefix + "}");
        return null;
    }

    @Override
    public Object visitGlobalVariable(IRGlobalVariable globalVariable, Object prefix) {
        out.println(prefix + "global " + globalVariable.toString());
        return null;
    }

    @Override
    public Object visitFunction(IRFunction irFunction, Object prefix) {
        StringBuilder sb = new StringBuilder(prefix.toString());
        for (String attribute : irFunction.attributes) {
            sb.append("__attribute__(\"").append(attribute).append("\") ");
        }
        if (irFunction.isExtern) sb.append("extern ");
        sb.append("function ").append(irFunction.returnType).append(" ").append(irFunction.name).append("(");
        for (int i = 0; i < irFunction.args.size(); i++) {
            sb.append(irFunction.args.get(i).toString());
            if (irFunction.isVarArg || (i != irFunction.args.size() - 1)) sb.append(", ");
        }
        if (irFunction.isVarArg) sb.append("...");
        sb.append(")");
        if (irFunction.isExtern) {
            out.println(sb);
        } else {
            sb.append(" {");
            out.println(sb);
            for (int i = 0; i < irFunction.locals.size(); i++) {
                out.println(prefix + "\t" + irFunction.locals.get(i).toString() + (i < irFunction.locals.size() - 1 ? "," : ""));
            }
            out.println(prefix + "} {");
            if (irFunction.controlFlowGraph != null) {
                for (IRBasicBlock block : irFunction.controlFlowGraph.basicBlocks.values()) {
                    out.println(prefix + "\t" + block.name + ":");
                    for (IRInstruction instruction : block.instructions)
                        out.println(prefix + "\t\t" + instruction.toString());
                }
            }
            out.println(prefix + "}");
        }
        return null;
    }
}
