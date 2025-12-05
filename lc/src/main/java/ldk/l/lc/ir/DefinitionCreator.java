package ldk.l.lc.ir;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.statement.declaration.LCMethodDeclaration;
import ldk.l.lc.ast.statement.declaration.LCVariableDeclaration;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.semantic.types.Type;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.function.IRFunction;
import ldk.l.lg.ir.function.IRLocalVariable;
import ldk.l.lg.ir.type.IRType;

import java.util.ArrayList;
import java.util.List;

import static ldk.l.lc.ir.IRUtils.parseType;

class DefinitionCreator extends LCAstVisitor {
    private IRModule module;

    public DefinitionCreator(IRModule module) {
        this.module = module;
    }

    @Override
    public Object visitMethodDeclaration(LCMethodDeclaration lcMethodDeclaration, Object additional) {
        List<IRLocalVariable> localVariables = new ArrayList<>();
        for (LCVariableDeclaration variableDeclaration : lcMethodDeclaration.parameterList.parameters) {
            localVariables.add(new IRLocalVariable(parseType(module, variableDeclaration.theType), variableDeclaration.name));
        }
        module.putFunction(new IRFunction(List.of(), parseType(module, lcMethodDeclaration.returnType), lcMethodDeclaration.symbol.getFullName(), localVariables, false));
        return null;
    }


}
