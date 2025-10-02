// Generated from C:/Users/XiaoLi/Documents/Projects/L-Language/lg/src/main/antlr/LGIR.g4 by ANTLR 4.13.2
package ldk.l.lg.parser;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link LGIRParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface LGIRVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link LGIRParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(LGIRParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#structure}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStructure(LGIRParser.StructureContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(LGIRParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#constant_pool}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_pool(LGIRParser.Constant_poolContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#global_data}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGlobal_data(LGIRParser.Global_dataContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#init}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInit(LGIRParser.InitContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#function_body}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction_body(LGIRParser.Function_bodyContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#locals_}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLocals_(LGIRParser.Locals_Context ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#instruction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInstruction(LGIRParser.InstructionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#cmp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCmp(LGIRParser.CmpContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#conditional_jump}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditional_jump(LGIRParser.Conditional_jumpContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#increase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIncrease(LGIRParser.IncreaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#decrease}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDecrease(LGIRParser.DecreaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#atomic_increase}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomic_increase(LGIRParser.Atomic_increaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#atomic_decrease}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAtomic_decrease(LGIRParser.Atomic_decreaseContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#malloc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMalloc(LGIRParser.MallocContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#free}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFree(LGIRParser.FreeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#realloc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRealloc(LGIRParser.ReallocContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#get}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGet(LGIRParser.GetContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#set}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet(LGIRParser.SetContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#goto}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGoto(LGIRParser.GotoContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#nop}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNop(LGIRParser.NopContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#negate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNegate(LGIRParser.NegateContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#not}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNot(LGIRParser.NotContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#return}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitReturn(LGIRParser.ReturnContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#set_virtual_register}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSet_virtual_register(LGIRParser.Set_virtual_registerContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#stack_alloc}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStack_alloc(LGIRParser.Stack_allocContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#type_cast}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_cast(LGIRParser.Type_castContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#calculate}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCalculate(LGIRParser.CalculateContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#asm}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsm(LGIRParser.AsmContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#invoke}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInvoke(LGIRParser.InvokeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#fields}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFields(LGIRParser.FieldsContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#field}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitField(LGIRParser.FieldContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType(LGIRParser.TypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#base_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBase_type(LGIRParser.Base_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#void_type}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVoid_type(LGIRParser.Void_typeContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#operand}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOperand(LGIRParser.OperandContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#virtual_register}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitVirtual_register(LGIRParser.Virtual_registerContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(LGIRParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(LGIRParser.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#type_cast_kind}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_cast_kind(LGIRParser.Type_cast_kindContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#calculation_operator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCalculation_operator(LGIRParser.Calculation_operatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#asm_resource}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsm_resource(LGIRParser.Asm_resourceContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#argument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArgument(LGIRParser.ArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#constant_pool_entries}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_pool_entries(LGIRParser.Constant_pool_entriesContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#constant_pool_entry}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant_pool_entry(LGIRParser.Constant_pool_entryContext ctx);
	/**
	 * Visit a parse tree produced by {@link LGIRParser#constant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstant(LGIRParser.ConstantContext ctx);
}