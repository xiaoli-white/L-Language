package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAstUtil;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.expression.type.LCTypeReferenceExpression;
import ldk.l.lc.ast.statement.declaration.object.LCClassDeclaration;
import ldk.l.lc.ast.statement.declaration.object.LCEnumDeclaration;
import ldk.l.lc.ast.statement.declaration.object.LCInterfaceDeclaration;
import ldk.l.lc.ast.statement.declaration.object.LCRecordDeclaration;
import ldk.l.lc.semantic.types.SystemTypes;
import ldk.l.lc.util.error.ErrorStream;
import ldk.l.lc.util.symbol.object.ClassSymbol;
import ldk.l.lc.util.symbol.object.InterfaceSymbol;
import ldk.l.lc.util.symbol.object.ObjectSymbol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class ObjectSymbolResolver extends LCAstVisitor {
    private final ErrorStream errorStream;

    public ObjectSymbolResolver(ErrorStream errorStream) {
        this.errorStream = errorStream;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        if (lcClassDeclaration.extended != null) {
            ObjectSymbol objectSymbol = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByName(lcClassDeclaration, lcClassDeclaration.extended.toTypeString())));
            if (objectSymbol instanceof ClassSymbol classSymbol)
                lcClassDeclaration.symbol.extended = classSymbol;
        } else if (!lcClassDeclaration.modifier.attributes.contains("no_extend")) {
            lcClassDeclaration.symbol.extended = ((LCClassDeclaration) Objects.requireNonNull(this.getAST(lcClassDeclaration).getObjectDeclaration(SystemTypes.Object_Type.name))).symbol;
            this.getAST(lcClassDeclaration).name2Type.get(lcClassDeclaration.getFullName()).upperTypes.add(SystemTypes.Object_Type);
        }

        List<InterfaceSymbol> implementedInterfaces = new ArrayList<>();
        for (LCTypeReferenceExpression implementedInterface : lcClassDeclaration.implementedInterfaces) {
            ObjectSymbol objectSymbol = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByName(lcClassDeclaration, implementedInterface.theType.toTypeString())));
            if (objectSymbol instanceof InterfaceSymbol interfaceSymbol)
                implementedInterfaces.add(interfaceSymbol);
        }
        lcClassDeclaration.symbol.implementedInterfaces = implementedInterfaces;

        List<ClassSymbol> permittedClasses = new ArrayList<>();
        for (LCTypeReferenceExpression permittedClass : lcClassDeclaration.permittedClasses) {
            ObjectSymbol objectSymbol = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByName(lcClassDeclaration, permittedClass.theType.toTypeString())));
            if (objectSymbol instanceof ClassSymbol classSymbol)
                permittedClasses.add(classSymbol);
        }
        lcClassDeclaration.symbol.permittedClasses = permittedClasses;

        return super.visitClassDeclaration(lcClassDeclaration, additional);
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        List<InterfaceSymbol> extendedInterfaces = new ArrayList<>();
        for (LCTypeReferenceExpression extendedInterface : lcInterfaceDeclaration.extendedInterfaces) {
            ObjectSymbol objectSymbol = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByName(lcInterfaceDeclaration, extendedInterface.toTypeString())));
            if (objectSymbol instanceof InterfaceSymbol interfaceSymbol)
                extendedInterfaces.add(interfaceSymbol);
        }
        lcInterfaceDeclaration.symbol.extendedInterfaces = extendedInterfaces;

        return super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object additional) {
        List<InterfaceSymbol> implementedInterfaces = new ArrayList<>();
        for (LCTypeReferenceExpression implementedInterface : lcEnumDeclaration.implementedInterfaces) {
            ObjectSymbol objectSymbol = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByName(lcEnumDeclaration, implementedInterface.toTypeString())));
            if (objectSymbol instanceof InterfaceSymbol interfaceSymbol)
                implementedInterfaces.add(interfaceSymbol);
        }
        lcEnumDeclaration.symbol.implementedInterfaces = implementedInterfaces;
        return super.visitEnumDeclaration(lcEnumDeclaration, additional);
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object additional) {
        List<InterfaceSymbol> implementedInterfaces = new ArrayList<>();
        for (LCTypeReferenceExpression implementedInterface : lcRecordDeclaration.implementedInterfaces) {
            ObjectSymbol objectSymbol = LCAstUtil.getObjectSymbol(Objects.requireNonNull(LCAstUtil.getObjectDeclarationByName(lcRecordDeclaration, implementedInterface.toTypeString())));
            if (objectSymbol instanceof InterfaceSymbol interfaceSymbol)
                implementedInterfaces.add(interfaceSymbol);
        }
        lcRecordDeclaration.symbol.implementedInterfaces = implementedInterfaces;
        return super.visitRecordDeclaration(lcRecordDeclaration, additional);
    }
}
