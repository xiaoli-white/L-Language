package ldk.l.lc.ir;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.ast.statement.declaration.object.LCClassDeclaration;
import ldk.l.lc.ast.statement.declaration.object.LCInterfaceDeclaration;
import ldk.l.lc.util.symbol.VariableSymbol;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.base.IRGlobalVariable;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.function.IRLocalVariable;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRStructureType;
import ldk.l.lg.ir.type.IRType;

import java.util.ArrayList;
import java.util.List;

import static ldk.l.lc.ir.IRUtils.parseType;

public final class DefinitionCreator extends LCAstVisitor {
    private IRModule module;

    public DefinitionCreator(IRModule module) {
        this.module = module;
    }

    @Override
    public Object visitSourceCodeFile(LCSourceCodeFile lcSourceCodeFile, Object additional) {
        super.visitSourceCodeFile(lcSourceCodeFile, additional);
        if (!module.functions.containsKey("malloc")) {
            module.putFunction(new IRFunction(List.of(), new IRPointerType(IRType.getVoidType()), "malloc", List.of(new IRLocalVariable(IRType.getUnsignedLongType(), "size")), false));
        }
        if (!module.functions.containsKey("free")) {
            module.putFunction(new IRFunction(List.of(), IRType.getVoidType(), "free", List.of(new IRLocalVariable(new IRPointerType(IRType.getVoidType()), "ptr")), false));
        }
        if (!module.functions.containsKey("realloc")) {
            module.putFunction(new IRFunction(List.of(), new IRPointerType(IRType.getVoidType()), "realloc", List.of(new IRLocalVariable(new IRPointerType(IRType.getVoidType()), "ptr"), new IRLocalVariable(IRType.getUnsignedLongType(), "size")), false));
        }
        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        IRStructure structure = module.structures.get(lcClassDeclaration.getFullName());
        structure.fields.add(new IRField(new IRPointerType(new IRStructureType(module.structures.get("l.lang.Class"))), "<class_ptr>"));
        structure.fields.add(new IRField(IRType.getUnsignedLongType(), "<ref_count>"));
        for (VariableSymbol variableSymbol : lcClassDeclaration.symbol.getAllProperties()) {
            structure.fields.add(new IRField(parseType(module, variableSymbol.theType), variableSymbol.name));
        }
        module.putGlobalVariable(new IRGlobalVariable(List.of(), false, "<class_instance " + lcClassDeclaration.getFullName() + ">", new IRStructureType(module.structures.get("l.lang.Class")), null));
        module.putFunction(new IRFunction(List.of(), IRType.getVoidType(), lcClassDeclaration.getFullName() + ".<__static_init__>()V", List.of(), false));
        module.putFunction(new IRFunction(List.of(), IRType.getVoidType(), lcClassDeclaration.getFullName() + ".<__init__>()V", List.of(new IRLocalVariable(new IRPointerType(new IRStructureType(module.structures.get(lcClassDeclaration.getFullName()))), "<this_ptr>")), false));
        return super.visitClassDeclaration(lcClassDeclaration, additional);
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        module.putGlobalVariable(new IRGlobalVariable(List.of(), false, "<class_instance " + lcInterfaceDeclaration.getFullName() + ">", new IRStructureType(module.structures.get("l.lang.Class")), null));
        return super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        if (LCFlags.hasExtern(lcMethodDeclaration.modifier.flags)) {
            List<IRLocalVariable> args = new ArrayList<>();
            for (LCVariableDeclaration variableDeclaration : lcMethodDeclaration.parameterList.parameters) {
                args.add(new IRLocalVariable(parseType(module, variableDeclaration.theType), variableDeclaration.name + "_0"));
            }
            module.putFunction(new IRFunction(List.of(), parseType(module, lcMethodDeclaration.returnType), lcMethodDeclaration.getName(), args, false));
        } else if (!LCFlags.hasAbstract(lcMethodDeclaration.modifier.flags)) {
            List<IRLocalVariable> args = new ArrayList<>();
            if (!LCFlags.hasStatic(lcMethodDeclaration.modifier.flags)) {
                args.add(new IRLocalVariable(parseType(module, lcMethodDeclaration.symbol.objectSymbol.theType), "<this_ptr>"));
            }
            for (LCVariableDeclaration variableDeclaration : lcMethodDeclaration.parameterList.parameters) {
                args.add(new IRLocalVariable(parseType(module, variableDeclaration.theType), variableDeclaration.name + "_0"));
            }
            module.putFunction(new IRFunction(List.of(), parseType(module, lcMethodDeclaration.returnType), lcMethodDeclaration.getName(), args, false));
        }
        return null;
    }

    @Override
    public Object visitVariableDeclaration(LCVariableDeclaration lcVariableDeclaration, Object additional) {
        if (LCFlags.hasStatic(lcVariableDeclaration.modifier.flags) && getEnclosingMethodDeclaration(lcVariableDeclaration) == null && getEnclosingInit(lcVariableDeclaration) == null) {
            module.putGlobalVariable(new IRGlobalVariable(List.of(), lcVariableDeclaration.isVal, lcVariableDeclaration.getName(), parseType(module, lcVariableDeclaration.theType)));
        }
        return null;
    }
}
