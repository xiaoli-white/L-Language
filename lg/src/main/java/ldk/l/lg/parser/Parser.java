package ldk.l.lg.parser;

import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.base.IRFunction;
import ldk.l.lg.ir.instruction.IRInstruction;
import ldk.l.lg.ir.structure.IRField;
import ldk.l.lg.ir.structure.IRStructure;
import ldk.l.lg.ir.type.IRPointerType;
import ldk.l.lg.ir.type.IRType;

import java.util.ArrayList;
import java.util.List;

public final class Parser {
    private final IRModule module;
    private final CharStream charStream;

    public Parser(IRModule module, CharStream charStream) {
        this.module = module;
        this.charStream = charStream;
    }

    public void parse() {
        while (!charStream.eof()) {
            char c = charStream.peek();
            if (c == 's' && charStream.pos + 9 < charStream.length() && charStream.startsWith("structure")) {
                parseStructure();
            } else if (c == 'f' && charStream.pos + 7 < charStream.length() && charStream.startsWith("function")) {
                parseFunction();
            }
        }
    }

    public void parseStructure() {
        charStream.addPos(9);
        if (Character.isWhitespace(charStream.peek())) {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        StringBuilder name = new StringBuilder();
        while (!charStream.eof() && charStream.peek() != '{' && !Character.isWhitespace(charStream.peek())) {
            name.append(charStream.next());
        }
        skipWhiteSpace();
        if (charStream.peek() == '{') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        List<IRField> fields = new ArrayList<>();
        while (!charStream.eof() && charStream.peek() != '}') {
            IRField field = parseField();
            fields.add(field);
            skipWhiteSpace();
            char p = charStream.peek();
            if (p == ',') {
                charStream.addPos(1);
                skipWhiteSpace();
            } else if (p != '}') {
                // TODO dump error
            }
        }
        IRStructure structure = new IRStructure(name.toString(), fields.toArray(new IRField[0]));
        module.putStructure(structure);
    }

    public void parseFunction() {
        charStream.addPos(7);
        if (Character.isWhitespace(charStream.peek())) {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        IRType returnType = parseType();
        skipWhiteSpace();
        if (Character.isWhitespace(charStream.peek())) {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        StringBuilder name = new StringBuilder();
        while (!charStream.eof() && charStream.peek() != '(' && !Character.isWhitespace(charStream.peek())) {
            name.append(charStream.next());
        }
        skipWhiteSpace();
        if (charStream.peek() == '(') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        List<IRField> parameters = new ArrayList<>();
        while (!charStream.eof() && charStream.peek() != ')') {
            IRField parameter = parseField();
            parameters.add(parameter);
            skipWhiteSpace();
            char p = charStream.peek();
            if (p == ',') {
                charStream.addPos(1);
                skipWhiteSpace();
            } else if (p != ')') {
                // TODO dump error
            }
        }
        skipWhiteSpace();
        if (charStream.peek() == '{') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        if (charStream.peek() == 'l' && charStream.pos + 6 < charStream.length() && charStream.startsWith("locals")) {
            charStream.addPos(6);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        if (charStream.peek() == '{') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        List<IRField> locals = new ArrayList<>();
        while (!charStream.eof() && charStream.peek() != '}') {
            IRField local = parseField();
            locals.add(local);
            skipWhiteSpace();
            char p = charStream.peek();
            if (p == ',') {
                charStream.addPos(1);
                skipWhiteSpace();
            } else if (p != '}') {
                // TODO dump error
            }
        }
        skipWhiteSpace();
        if (charStream.peek() == '}') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();

        List<IRInstruction> instructions = new ArrayList<>();
        while (!charStream.eof() && charStream.peek() != '}') {
            IRInstruction instruction = parseInstruction();
            instructions.add(instruction);
            skipWhiteSpace();
        }

        if (charStream.peek() == '}') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();


        List<IRField> fields = new ArrayList<>(parameters);
        fields.addAll(locals);
        IRFunction function = new IRFunction(returnType, name.toString(), parameters.size(), fields.toArray(new IRField[0]), null);
        module.putFunction(function);
    }
    public IRInstruction parseInstruction() {
        // TODO
        return null;
    }
    public IRField parseField() {
        StringBuilder name = new StringBuilder();
        while (!charStream.eof() && !Character.isWhitespace(charStream.peek())) {
            name.append(charStream.next());
        }
        skipWhiteSpace();
        if (charStream.peek() == ':') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        IRType type = parseType();
        skipWhiteSpace();
        return new IRField(name.toString(), type);
    }

    public IRType parseType() {
        IRType type;
        char c = charStream.peek();
        if (c == 'i') {
            charStream.addPos(1);
            char p = charStream.peek();
            if (p == '8') {
                charStream.addPos(1);
                type = IRType.getByteType();
            } else {
                char p2 = charStream.peek2();
                if (p == '1' && p2 == '6') {
                    charStream.addPos(2);
                    type = IRType.getShortType();
                } else if (p == '3' && p2 == '2') {
                    charStream.addPos(2);
                    type = IRType.getIntType();
                } else if (p == '6' && p2 == '4') {
                    charStream.addPos(2);
                    type = IRType.getLongType();
                } else {
                    throw new RuntimeException();
                }
            }
        } else if (c == 'u') {
            charStream.addPos(1);
            char p = charStream.peek();
            if (p == '1') {
                if (charStream.pos + 1 < charStream.length() && charStream.peek2() == '6') {
                    charStream.addPos(2);
                    type = IRType.getUnsignedShortType();
                } else {
                    charStream.addPos(1);
                    type = IRType.getBooleanType();
                }
            } else if (p == '8') {
                charStream.addPos(1);
                return IRType.getUnsignedByteType();
            } else {
                char p2 = charStream.peek2();
                if (p == '3' && p2 == '2') {
                    charStream.addPos(2);
                    type = IRType.getUnsignedIntType();
                } else if (p == '6' && p2 == '4') {
                    charStream.addPos(2);
                    type = IRType.getUnsignedLongType();
                } else {
                    throw new RuntimeException();
                }
            }
        } else if (c == 'f' && charStream.pos + 5 < charStream.length() && charStream.startsWith("float")) {
            charStream.addPos(5);
            type = IRType.getFloatType();
        } else if (c == 'd' && charStream.pos + 6 < charStream.length() && charStream.startsWith("double")) {
            charStream.addPos(6);
            type = IRType.getDoubleType();
        } else if (c == 'v' && charStream.pos + 4 < charStream.length() && charStream.startsWith("void")) {
            charStream.addPos(4);
            type = IRType.getVoidType();
        } else {
            throw new RuntimeException();
        }
        skipWhiteSpace();
        while (!charStream.eof() && charStream.peek() == '*') {
            charStream.addPos(1);
            type = new IRPointerType(type);
            skipWhiteSpace();
        }
        return type;
    }

    private void skipWhiteSpace() {
        while (!charStream.eof() && !Character.isWhitespace(charStream.peek())) {
            charStream.next();
        }
    }
}
