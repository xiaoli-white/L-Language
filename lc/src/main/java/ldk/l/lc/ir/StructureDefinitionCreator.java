package ldk.l.lc.ir;

import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.expression.type.LCArrayTypeExpression;
import ldk.l.lc.ast.statement.declaration.object.LCClassDeclaration;
import ldk.l.lc.ast.statement.declaration.object.LCInterfaceDeclaration;
import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.structure.IRStructure;

import java.util.ArrayList;

public final class StructureDefinitionCreator extends LCAstVisitor {
    private IRModule module;

    public StructureDefinitionCreator(IRModule module) {
        this.module = module;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        module.putStructure(new IRStructure(lcClassDeclaration.modifier.attributes, lcClassDeclaration.getFullName(), new ArrayList<>()));
        super.visitClassDeclaration(lcClassDeclaration, additional);
        return null;
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        module.putStructure(new IRStructure(lcInterfaceDeclaration.modifier.attributes, lcInterfaceDeclaration.getFullName(), new ArrayList<>()));
        return super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);
    }
}
