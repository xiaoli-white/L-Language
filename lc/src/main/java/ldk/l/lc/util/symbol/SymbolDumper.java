package ldk.l.lc.util.symbol;

import ldk.l.lc.ast.base.LCFlags;
import ldk.l.lc.util.symbol.object.*;

import java.util.Arrays;

public class SymbolDumper extends SymbolVisitor {
    @Override
    public Object visitVariableSymbol(VariableSymbol variableSymbol, Object prefix) {
        System.out.println(prefix + "VariableSymbol{name: '" + variableSymbol.name + "', flags: '" + LCFlags.toFlagsString(variableSymbol.flags) + "', attributes: " + Arrays.toString(variableSymbol.attributes) + "}");

        return null;
    }

    @Override
    public Object visitMethodSymbol(MethodSymbol methodSymbol, Object prefix) {
        System.out.println(prefix + methodSymbol.methodKind.name() + "{name: '" + methodSymbol.getFullName() + "', flags: '" + LCFlags.toFlagsString(methodSymbol.flags) + "', attributes: " + Arrays.toString(methodSymbol.attributes) + ", local var count: " + methodSymbol.vars.size() + "}");

        if (methodSymbol.closure != null && methodSymbol.closure.vars.length > 0) {
            System.out.println(prefix + "    " + methodSymbol.closure);
        }

        return null;
    }

    @Override
    public Object visitClassSymbol(ClassSymbol classSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("ClassSymbol{name: '").append(classSymbol.getFullName()).append("'");

        if (classSymbol.typeParameters.length > 0) {
            str.append(", typeParameter:[");
            for (int i = 0; i < classSymbol.typeParameters.length; i++) {
                TypeParameterSymbol typeParameter = classSymbol.typeParameters[i];
                str.append("'").append(typeParameter.name).append("'");

                if (i < classSymbol.implementedInterfaces.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (classSymbol.extended != null) str.append(", extends: '").append(classSymbol.extended.name).append("'");
        if (classSymbol.implementedInterfaces != null && classSymbol.implementedInterfaces.length > 0) {
            str.append(", implements:[");
            for (int i = 0; i < classSymbol.implementedInterfaces.length; i++) {
                InterfaceSymbol implementedInterface = classSymbol.implementedInterfaces[i];
                str.append("'").append(implementedInterface.name).append("'");

                if (i < classSymbol.implementedInterfaces.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (classSymbol.permittedClasses != null && classSymbol.permittedClasses.length > 0) {
            str.append(", permits:[");
            for (int i = 0; i < classSymbol.permittedClasses.length; i++) {
                ClassSymbol permittedClass = classSymbol.permittedClasses[i];
                str.append("'").append(permittedClass.name).append("'");

                if (i < classSymbol.permittedClasses.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (classSymbol.properties.length > 0) {
            str.append(", props:[");
            for (int i = 0; i < classSymbol.properties.length; i++) {
                VariableSymbol property = classSymbol.properties[i];
                str.append("'").append(property.name).append("'");

                if (i < classSymbol.properties.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (classSymbol.methods.length > 0) {
            str.append(", methods:[");
            for (int i = 0; i < classSymbol.methods.length; i++) {
                MethodSymbol method = classSymbol.methods[i];
                str.append("'").append(method.name).append("'");
                if (i < classSymbol.methods.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);

        return null;
    }

    @Override
    public Object visitInterfaceSymbol(InterfaceSymbol interfaceSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("InterfaceSymbol{name: '").append(interfaceSymbol.getFullName()).append("'");

        if (interfaceSymbol.extendedInterfaces != null && interfaceSymbol.extendedInterfaces.length > 0) {
            str.append(", extends:[");
            for (int i = 0; i < interfaceSymbol.extendedInterfaces.length; i++) {
                InterfaceSymbol implementedInterface = interfaceSymbol.extendedInterfaces[i];
                str.append("'").append(implementedInterface.name).append("'");

                if (i < interfaceSymbol.extendedInterfaces.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (interfaceSymbol.methods.length > 0) {
            str.append(", methods:[");
            for (int i = 0; i < interfaceSymbol.methods.length; i++) {
                MethodSymbol method = interfaceSymbol.methods[i];
                str.append("'").append(method.name).append("'");
                if (i < interfaceSymbol.methods.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);
        return null;
    }

    @Override
    public Object visitAnnotationSymbol(AnnotationSymbol annotationSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("AnnotationSymbol{name: '").append(annotationSymbol.getFullName()).append("'");

        if (annotationSymbol.fields.length > 0) {
            str.append(", fields:[");
            for (int i = 0; i < annotationSymbol.fields.length; i++) {
                AnnotationSymbol.AnnotationFieldSymbol field = annotationSymbol.fields[i];
                str.append("'").append(field.name).append("'");
                if (i < annotationSymbol.fields.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);
        return null;
    }

    @Override
    public Object visitAnnotationFieldSymbol(AnnotationSymbol.AnnotationFieldSymbol annotationFieldSymbol, Object prefix) {
        System.out.println(prefix + "AnnotationFieldSymbol{name: '" + annotationFieldSymbol.name + "'}");
        return null;
    }

    @Override
    public Object visitEnumSymbol(EnumSymbol enumSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("EnumSymbol{name: '").append(enumSymbol.getFullName()).append("'");

        if (enumSymbol.implementedInterfaces != null && enumSymbol.implementedInterfaces.length > 0) {
            str.append(", implements:[");
            for (int i = 0; i < enumSymbol.implementedInterfaces.length; i++) {
                InterfaceSymbol implementedInterface = enumSymbol.implementedInterfaces[i];
                str.append("'").append(implementedInterface.name).append("'");

                if (i < enumSymbol.implementedInterfaces.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (enumSymbol.fields.length > 0) {
            str.append(", fields:[");
            for (int i = 0; i < enumSymbol.fields.length; i++) {
                EnumSymbol.EnumFieldSymbol field = enumSymbol.fields[i];
                str.append("'").append(field.name).append("'");

                if (i < enumSymbol.fields.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (enumSymbol.properties.length > 0) {
            str.append(", props:[");
            for (int i = 0; i < enumSymbol.properties.length; i++) {
                VariableSymbol property = enumSymbol.properties[i];
                str.append("'").append(property.name).append("'");

                if (i < enumSymbol.properties.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (enumSymbol.methods.length > 0) {
            str.append(", methods:[");
            for (int i = 0; i < enumSymbol.methods.length; i++) {
                MethodSymbol method = enumSymbol.methods[i];
                str.append("'").append(method.name).append("'");
                if (i < enumSymbol.methods.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);

        return null;
    }

    @Override
    public Object visitEnumFieldSymbol(EnumSymbol.EnumFieldSymbol enumFieldSymbol, Object prefix) {
        System.out.println(prefix + "EnumFieldSymbol{name: '" + enumFieldSymbol.name + "'}");
        return null;
    }

    @Override
    public Object visitRecordSymbol(RecordSymbol recordSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("RecordSymbol{name: '").append(recordSymbol.getFullName()).append("'");

        if (recordSymbol.implementedInterfaces != null && recordSymbol.implementedInterfaces.length > 0) {
            str.append(", implements:[");
            for (int i = 0; i < recordSymbol.implementedInterfaces.length; i++) {
                InterfaceSymbol implementedInterface = recordSymbol.implementedInterfaces[i];
                str.append("'").append(implementedInterface.name).append("'");

                if (i < recordSymbol.implementedInterfaces.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }

        if (recordSymbol.fields.length > 0) {
            str.append(", fields:[");
            for (int i = 0; i < recordSymbol.fields.length; i++) {
                VariableSymbol field = recordSymbol.fields[i];
                str.append("'").append(field.name).append("'");

                if (i < recordSymbol.fields.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (recordSymbol.properties.length > 0) {
            str.append(", props:[");
            for (int i = 0; i < recordSymbol.properties.length; i++) {
                VariableSymbol property = recordSymbol.properties[i];
                str.append("'").append(property.name).append("'");

                if (i < recordSymbol.properties.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (recordSymbol.methods.length > 0) {
            str.append(", methods:[");
            for (int i = 0; i < recordSymbol.methods.length; i++) {
                MethodSymbol method = recordSymbol.methods[i];
                str.append("'").append(method.name).append("'");
                if (i < recordSymbol.methods.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);

        return null;
    }

    @Override
    public Object visitStructSymbol(StructSymbol structSymbol, Object prefix) {
        StringBuilder str = new StringBuilder((String) prefix);
        str.append("StructSymbol{name: '").append(structSymbol.getFullName()).append("'");

        if (structSymbol.properties.length > 0) {
            str.append(", props:[");
            for (int i = 0; i < structSymbol.properties.length; i++) {
                VariableSymbol property = structSymbol.properties[i];
                str.append("'").append(property.name).append("'");

                if (i < structSymbol.properties.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        if (structSymbol.methods.length > 0) {
            str.append(", methods:[");
            for (int i = 0; i < structSymbol.methods.length; i++) {
                MethodSymbol method = structSymbol.methods[i];
                str.append("'").append(method.name).append("'");
                if (i < structSymbol.methods.length - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
        }
        str.append("}");

        System.out.println(str);
        return null;
    }

    @Override
    public Object visitResourceForNativeSymbol(ResourceForNativeSymbol resourceForNativeSymbol, Object prefix) {
        System.out.println(prefix + "ResourceForNativeSymbol{name: '" + resourceForNativeSymbol.name + "'}");
        return null;
    }

    @Override
    public Object visitTypeParameterSymbol(TypeParameterSymbol typeParameterSymbol, Object prefix) {
        System.out.println(prefix + "TypeParameterSymbol{name: '" + typeParameterSymbol.name + "'}");
        return null;
    }

    @Override
    public Object visitTemplateTypeParameterSymbol(TemplateTypeParameterSymbol templateTypeParameterSymbol, Object prefix) {
        System.out.println(prefix + "TemplateTypeParameterSymbol{name: '" + templateTypeParameterSymbol.name + "'}");
        return null;
    }
}