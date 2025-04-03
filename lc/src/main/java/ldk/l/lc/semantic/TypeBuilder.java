package ldk.l.lc.semantic;

import ldk.l.lc.ast.LCAstUtil;
import ldk.l.lc.ast.LCAstVisitor;
import ldk.l.lc.ast.file.LCSourceCodeFile;
import ldk.l.lc.ast.statement.declaration.object.*;
import ldk.l.lc.ast.statement.declaration.object.LCAnnotationDeclaration;
import ldk.l.lc.ast.statement.LCPackage;
import ldk.l.lc.semantic.types.NamedType;

public class TypeBuilder extends LCAstVisitor {
    @Override
    public Object visitPackage(LCPackage lcPackage, Object additional) {
        if (LCAstUtil.getSourceFile(lcPackage) instanceof LCSourceCodeFile sourceCodeFile) {
            sourceCodeFile.packageName = lcPackage.name;
        }
        return null;
    }

    @Override
    public Object visitClassDeclaration(LCClassDeclaration lcClassDeclaration, Object additional) {
        String fullName = lcClassDeclaration.getFullName();
        this.getAST(lcClassDeclaration).name2Type.put(fullName, new NamedType(fullName));
        return super.visitClassDeclaration(lcClassDeclaration, additional);
    }

    @Override
    public Object visitEnumDeclaration(LCEnumDeclaration lcEnumDeclaration, Object additional) {
        String fullName = lcEnumDeclaration.getFullName();
        this.getAST(lcEnumDeclaration).name2Type.put(fullName, new NamedType(fullName));
        return super.visitEnumDeclaration(lcEnumDeclaration, additional);
    }

    @Override
    public Object visitInterfaceDeclaration(LCInterfaceDeclaration lcInterfaceDeclaration, Object additional) {
        String fullName = lcInterfaceDeclaration.getFullName();
        this.getAST(lcInterfaceDeclaration).name2Type.put(fullName, new NamedType(fullName));
        return super.visitInterfaceDeclaration(lcInterfaceDeclaration, additional);
    }

    @Override
    public Object visitAnnotationDeclaration(LCAnnotationDeclaration lcAnnotationDeclaration, Object additional) {
        String fullName = lcAnnotationDeclaration.getFullName();
        this.getAST(lcAnnotationDeclaration).name2Type.put(fullName, new NamedType(fullName));
        return super.visitAnnotationDeclaration(lcAnnotationDeclaration, additional);
    }

    @Override
    public Object visitStructDeclaration(LCStructDeclaration lcStructDeclaration, Object additional) {
        String fullName = lcStructDeclaration.getFullName();
        this.getAST(lcStructDeclaration).name2Type.put(fullName, new NamedType(fullName));
        return super.visitStructDeclaration(lcStructDeclaration, additional);
    }

    @Override
    public Object visitRecordDeclaration(LCRecordDeclaration lcRecordDeclaration, Object additional) {
        String fullName = lcRecordDeclaration.getFullName();
        this.getAST(lcRecordDeclaration).name2Type.put(fullName, new NamedType(fullName));
        return super.visitRecordDeclaration(lcRecordDeclaration, additional);
    }
}
