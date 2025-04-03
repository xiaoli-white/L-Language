package ldk.l.lc.util.symbol;

import ldk.l.lc.util.symbol.object.*;

public abstract class SymbolVisitor {
    public Object visit(Symbol symbol, Object additional) {
        return symbol.accept(this, additional);
    }

    public abstract Object visitVariableSymbol(VariableSymbol variableSymbol, Object additional);

    public abstract Object visitMethodSymbol(MethodSymbol methodSymbol, Object additional);

    public abstract Object visitClassSymbol(ClassSymbol classSymbol, Object additional);

    public abstract Object visitInterfaceSymbol(InterfaceSymbol interfaceSymbol, Object additional);

    public abstract Object visitAnnotationSymbol(AnnotationSymbol annotationSymbol, Object additional);

    public abstract Object visitAnnotationFieldSymbol(AnnotationSymbol.AnnotationFieldSymbol annotationFieldSymbol, Object additional);

    public abstract Object visitEnumSymbol(EnumSymbol enumSymbol, Object additional);

    public abstract Object visitEnumFieldSymbol(EnumSymbol.EnumFieldSymbol enumFieldSymbol, Object additional);

    public abstract Object visitRecordSymbol(RecordSymbol recordSymbol, Object additional);

    public abstract Object visitStructSymbol(StructSymbol structSymbol, Object additional);

    public abstract Object visitResourceForNativeSymbol(ResourceForNativeSymbol resourceForNativeSymbol, Object additional);

    public abstract Object visitTypeParameterSymbol(TypeParameterSymbol typeParameterSymbol, Object additional);

    public abstract Object visitTemplateTypeParameterSymbol(TemplateTypeParameterSymbol templateTypeParameterSymbol, Object additional);
}