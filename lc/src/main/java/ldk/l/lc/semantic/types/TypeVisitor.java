package ldk.l.lc.semantic.types;

public abstract class TypeVisitor{
    public Object visit(Type type){
        return type.accept(this);
    }
    public abstract Object visitReferenceType(ReferenceType referenceType);
    public abstract Object visitNamedType(NamedType namedType);
    public abstract Object visitMethodType(MethodPointerType methodPointerType);
    public abstract Object visitPointerType(PointerType pointerType);
    public abstract Object visitArrayType(ArrayType arrayType);
    public abstract Object visitNullableType(NullableType nullableType);
}