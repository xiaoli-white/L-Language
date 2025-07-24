package ldk.l.lg.parser;

import ldk.l.lg.ir.IRModule;
import ldk.l.lg.ir.base.IRCondition;
import ldk.l.lg.ir.base.IRControlFlowGraph;
import ldk.l.lg.ir.base.IRFunction;
import ldk.l.lg.ir.instruction.*;
import ldk.l.lg.ir.operand.IRConstant;
import ldk.l.lg.ir.operand.IRMacro;
import ldk.l.lg.ir.operand.IROperand;
import ldk.l.lg.ir.operand.IRVirtualRegister;
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
            skipWhiteSpace();
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
                break;
            }
        }
        skipWhiteSpace();
        if (charStream.peek() == '}') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();

        List<IRControlFlowGraph.BasicBlock> basicBlocks = new ArrayList<>();
        while (!charStream.eof() && charStream.peek() != '}') {
            IRControlFlowGraph.BasicBlock basicBlock = parseBasicBlock();
            basicBlocks.add(basicBlock);
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

    public IRControlFlowGraph.BasicBlock parseBasicBlock() {
        if (charStream.peek() != '#') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        skipWhiteSpace();
        StringBuilder name = new StringBuilder();
        while (!charStream.eof() && charStream.peek() != ':' && !Character.isWhitespace(charStream.peek())) {
            name.append(charStream.next());
        }
        IRControlFlowGraph.BasicBlock basicBlock = new IRControlFlowGraph.BasicBlock(name.toString());
        skipWhiteSpace();
        if (charStream.peek() == ':') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        while (!charStream.eof() && charStream.peek() != '#') {
            IRInstruction instruction = parseInstruction();
            basicBlock.instructions.add(instruction);
            skipWhiteSpace();
        }
        return basicBlock;
    }

    public IRInstruction parseInstruction() {
        IROperand result = parseOperand();
        skipWhiteSpace();
        IRVirtualRegister target;
        if (result != null) {
            if (result instanceof IRVirtualRegister virtualRegister) {
                target = virtualRegister;
            } else {
                throw new RuntimeException("Invalid target register");
            }
            if (charStream.peek() == '=') {
                charStream.addPos(1);
            } else {
                // TODO dump error
            }
            skipWhiteSpace();
        } else {
            target = null;
        }
        if (charStream.startsWith("invoke")) {
            charStream.addPos(6);
            if (Character.isWhitespace(charStream.peek())) {
                skipWhiteSpace();
            } else {
                // TODO dump error
            }
            IRType returnType = parseType();
            if (Character.isWhitespace(charStream.peek())) {
                skipWhiteSpace();
            } else {
                // TODO dump error
            }
            IROperand address = parseOperand();
            List<IRType> argumentTypes = new ArrayList<>();
            List<IROperand> arguments = new ArrayList<>();
            while (!charStream.eof() && charStream.peek() == ',') {
                charStream.addPos(1);
                skipWhiteSpace();
                if (charStream.peek() == '[') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                argumentTypes.add(parseType());
                skipWhiteSpace();
                if (charStream.startsWith(",")) {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                arguments.add(parseOperand());
                skipWhiteSpace();
                if (charStream.startsWith("]")) {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
            }
            return new IRInvoke(returnType, address, argumentTypes.toArray(new IRType[0]), arguments.toArray(new IROperand[0]), target);
        }
        if (target != null) {
            if (charStream.startsWith("increase")) {
                charStream.addPos(8);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRIncrease(type, operand, target);
            } else if (charStream.startsWith("decrease")) {
                charStream.addPos(8);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRDecrease(type, operand, target);
            } else if (charStream.startsWith("get")) {
                charStream.addPos(3);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRGet(type, operand, target);
            } else if (charStream.startsWith("malloc")) {
                charStream.addPos(6);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand size = parseOperand();
                return new IRMalloc(size, target);
            } else if (charStream.startsWith("negate")) {
                charStream.addPos(6);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRNegate(false, type, operand, target);
            } else if (charStream.startsWith("not")) {
                charStream.addPos(3);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRNot(false, type, operand, target);
            } else if (charStream.startsWith("atomic_negate")) {
                charStream.addPos(13);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRNegate(true, type, operand, target);
            } else if (charStream.startsWith("atomic_not")) {
                charStream.addPos(10);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRNot(true, type, operand, target);
            } else if (charStream.startsWith("realloc")) {
                charStream.addPos(7);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand ptr = parseOperand();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand size = parseOperand();
                return new IRRealloc(ptr, size, target);
            } else if (charStream.startsWith("stack_alloc")) {
                charStream.addPos(11);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand size = parseOperand();
                return new IRStackAllocate(size, target);
            }
            for (IRCalculate.Operator op : IRCalculate.Operator.values()) {
                if (charStream.startsWith(op.text)) {
                    charStream.addPos(op.text.length());
                    if (Character.isWhitespace(charStream.peek())) {
                        skipWhiteSpace();
                    } else {
                        // TODO dump error
                    }
                    IRType type = parseType();
                    if (Character.isWhitespace(charStream.peek())) {
                        skipWhiteSpace();
                    } else {
                        // TODO dump error
                    }
                    IROperand operand1 = parseOperand();
                    skipWhiteSpace();
                    if (charStream.peek() == ',') {
                        charStream.addPos(1);
                    } else {
                        // TODO dump error
                    }
                    skipWhiteSpace();
                    IROperand operand2 = parseOperand();
                    return new IRCalculate(op.text.startsWith("atomic_"), op, type, operand1, operand2, target);
                }
            }
            for (IRTypeCast.Kind kind : IRTypeCast.Kind.values()) {
                if (charStream.startsWith(kind.name)) {
                    charStream.addPos(kind.name.length());
                    if (Character.isWhitespace(charStream.peek())) {
                        skipWhiteSpace();
                    } else {
                        // TODO dump error
                    }
                    IRType originalType = parseType();
                    if (Character.isWhitespace(charStream.peek())) {
                        skipWhiteSpace();
                    } else {
                        // TODO dump error
                    }
                    IROperand source = parseOperand();
                    if (Character.isWhitespace(charStream.peek())) {
                        skipWhiteSpace();
                    } else {
                        // TODO dump error
                    }
                    if (charStream.startsWith("to")) {
                        charStream.addPos(2);
                    } else {
                        // TODO dump error
                    }
                    if (Character.isWhitespace(charStream.peek())) {
                        skipWhiteSpace();
                    } else {
                        // TODO dump error
                    }
                    IRType targetType = parseType();
                    return new IRTypeCast(kind, originalType, source, targetType, target);
                }
            }
            IROperand operand = parseOperand();
            if (operand != null) return new IRSetVirtualRegister(operand, target);
        } else {
            if (charStream.startsWith("nop")) {
                charStream.addPos(3);
                return new IRNoOperate();
            } else if (charStream.startsWith("goto")) {
                charStream.addPos(4);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                if (charStream.peek() == '#') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                String label = parseString();
                return new IRGoto(label);
            } else if (charStream.startsWith("conditional_jump")) {
                charStream.addPos(16);
                skipWhiteSpace();
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRCondition condition;
                if (charStream.startsWith("e")) {
                    condition = IRCondition.Equal;
                    charStream.addPos(1);
                } else if (charStream.startsWith("ne")) {
                    condition = IRCondition.NotEqual;
                    charStream.addPos(2);
                } else if (charStream.startsWith("ge")) {
                    condition = IRCondition.GreaterEqual;
                    charStream.addPos(2);
                } else if (charStream.startsWith("g")) {
                    condition = IRCondition.Greater;
                    charStream.addPos(1);
                } else if (charStream.startsWith("le")) {
                    condition = IRCondition.LessEqual;
                    charStream.addPos(2);
                } else if (charStream.startsWith("l")) {
                    condition = IRCondition.Less;
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                    throw new RuntimeException();
                }
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                IROperand operand1 = parseOperand();
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                IROperand operand2 = parseOperand();
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                if (charStream.peek() == '#') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                String label = parseString();
                return new IRConditionalJump(type, condition, operand1, operand2, label);
            } else if (charStream.startsWith("atomic_conditional_jump")) {
                charStream.addPos(23);
                skipWhiteSpace();
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRCondition condition;
                if (charStream.startsWith("e")) {
                    condition = IRCondition.Equal;
                    charStream.addPos(1);
                } else if (charStream.startsWith("ne")) {
                    condition = IRCondition.NotEqual;
                    charStream.addPos(2);
                } else if (charStream.startsWith("ge")) {
                    condition = IRCondition.GreaterEqual;
                    charStream.addPos(2);
                } else if (charStream.startsWith("g")) {
                    condition = IRCondition.Greater;
                    charStream.addPos(1);
                } else if (charStream.startsWith("le")) {
                    condition = IRCondition.LessEqual;
                    charStream.addPos(2);
                } else if (charStream.startsWith("l")) {
                    condition = IRCondition.Less;
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                    throw new RuntimeException();
                }
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                IROperand operand1 = parseOperand();
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                IROperand operand2 = parseOperand();
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                if (charStream.peek() == '#') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                String label = parseString();
                return new IRConditionalJump(true, type, condition, operand1, operand2, label);
            } else if (charStream.startsWith("atomic_increase")) {
                charStream.addPos(15);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRIncrease(type, operand);
            } else if (charStream.startsWith("atomic_decrease")) {
                charStream.addPos(15);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRDecrease(type, operand);
            } else if (charStream.startsWith("free")) {
                charStream.addPos(4);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand operand = parseOperand();
                return new IRFree(operand);
            } else if (charStream.startsWith("return")) {
                charStream.addPos(6);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IROperand value = parseOperand();
                return new IRReturn(value);
            } else if (charStream.startsWith("set")) {
                charStream.addPos(3);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                IRType type = parseType();
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                IROperand address = parseOperand();
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                } else {
                    // TODO dump error
                }
                skipWhiteSpace();
                IROperand value = parseOperand();
                return new IRSet(type, address, value);
            } else if (charStream.startsWith("asm")) {
                charStream.addPos(3);
                if (Character.isWhitespace(charStream.peek())) {
                    skipWhiteSpace();
                } else {
                    // TODO dump error
                }
                String code = parseString();
                List<IRType> types = new ArrayList<>();
                List<IROperand> resources = new ArrayList<>();
                List<String> names = new ArrayList<>();
                while (!charStream.eof() && charStream.peek() == ',') {
                    charStream.addPos(1);
                    skipWhiteSpace();
                    if (charStream.peek() == '[') {
                        charStream.addPos(1);
                    } else {
                        // TODO dump error
                    }
                    skipWhiteSpace();
                    types.add(parseType());
                    skipWhiteSpace();
                    if (charStream.startsWith(",")) {
                        charStream.addPos(1);
                    } else {
                        // TODO dump error
                    }
                    skipWhiteSpace();
                    resources.add(parseOperand());
                    skipWhiteSpace();
                    if (charStream.startsWith(",")) {
                        charStream.addPos(1);
                    } else {
                        // TODO dump error
                    }
                    skipWhiteSpace();
                    names.add(parseString());
                    skipWhiteSpace();
                    if (charStream.startsWith("]")) {
                        charStream.addPos(1);
                    } else {
                        // TODO dump error
                    }
                    skipWhiteSpace();
                }
                return new IRAsm(code, types.toArray(new IRType[0]), resources.toArray(new IROperand[0]), names.toArray(new String[0]));
            }
        }
        throw new RuntimeException();
    }

    public IROperand parseOperand() {
        char c = charStream.peek();
        if (c == '%') {
            charStream.addPos(1);
            StringBuilder name = new StringBuilder();
            while (!charStream.eof() && !Character.isWhitespace(charStream.peek())) {
                name.append(charStream.next());
            }
            return new IRVirtualRegister(name.toString());
        } else if (c == '`') {
            charStream.addPos(1);
            StringBuilder name = new StringBuilder();
            while (!charStream.eof() && charStream.peek() != '(' && !Character.isWhitespace(charStream.peek()))
                name.append(charStream.next());
            skipWhiteSpace();
            if (charStream.peek() == '(') {
                charStream.addPos(1);
            } else {
                // TODO dump error
            }
            skipWhiteSpace();
            if (charStream.peek() == '[') {
                charStream.addPos(1);
            } else {
                // TODO dump error
            }
            skipWhiteSpace();
            List<String> args = new ArrayList<>();
            while (!charStream.eof() && charStream.peek() != ']') {
                String arg = parseString();
                args.add(arg);
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                    skipWhiteSpace();
                } else if (charStream.peek() != ']') {
                    // TODO dump error
                    break;
                }
            }
            if (charStream.peek() == ']') {
                charStream.addPos(1);
            } else {
                // TODO dump error
            }
            skipWhiteSpace();
            if (charStream.peek() == ',') {
                charStream.addPos(1);
            } else {
                // TODO dump error
            }
            skipWhiteSpace();
            if (charStream.peek() == '[') {
                charStream.addPos(1);
            } else {
                // TODO dump error
            }
            skipWhiteSpace();
            List<IROperand> additionalOperands = new ArrayList<>();
            while (!charStream.eof() && charStream.peek() != ']') {
                IROperand operand = parseOperand();
                additionalOperands.add(operand);
                skipWhiteSpace();
                if (charStream.peek() == ',') {
                    charStream.addPos(1);
                    skipWhiteSpace();
                } else if (charStream.peek() != ']') {
                    // TODO dump error
                    break;
                }
            }
            if (charStream.peek() == ']') {
                charStream.addPos(1);
            } else {
                // TODO dump error
            }
            skipWhiteSpace();
            if (charStream.peek() == ')') {
                charStream.addPos(1);
            } else {
                // TODO dump error
            }
            return new IRMacro(name.toString(), args.toArray(new String[0]), additionalOperands.toArray(new IROperand[0]));
        } else if (c == '$') {
            charStream.addPos(1);
            if (charStream.peek() == '0') {
                charStream.addPos(1);
                return new IRConstant(0);
            }
            int value = 0;
            while (charStream.peek() >= '0' && charStream.peek() <= '9') {
                value = value * 10 + (charStream.peek() - '0');
                charStream.addPos(1);
            }
            return new IRConstant(value);
        }
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

    public String parseString() {
        if (charStream.peek() == '"') {
            charStream.addPos(1);
        } else {
            // TODO dump error
        }
        StringBuilder stringBuilder = new StringBuilder();
        while (!charStream.eof() && charStream.peek() != '"') {
            char c = charStream.next();
            if (c == '\\') {
                char c2 = charStream.next();
                stringBuilder.append(switch (c2) {
                    case 'n' -> '\n';
                    case 't' -> '\t';
                    case 'r' -> '\r';
                    case '\\' -> '\\';
                    case '"' -> '"';
                    case '\'' -> '\'';
                    default -> throw new RuntimeException("Invalid escape character: \\" + c2);
                });
            } else {
                stringBuilder.append(c);
            }
        }
        return stringBuilder.toString();
    }

    private void skipWhiteSpace() {
        while (!charStream.eof() && !Character.isWhitespace(charStream.peek())) {
            charStream.next();
        }
    }
}
