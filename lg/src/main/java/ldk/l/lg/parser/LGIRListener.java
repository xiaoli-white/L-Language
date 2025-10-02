// Generated from C:/Users/XiaoLi/Documents/Projects/L-Language/lg/src/main/antlr/LGIR.g4 by ANTLR 4.13.2
package ldk.l.lg.parser;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link LGIRParser}.
 */
public interface LGIRListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link LGIRParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(LGIRParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(LGIRParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#structure}.
	 * @param ctx the parse tree
	 */
	void enterStructure(LGIRParser.StructureContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#structure}.
	 * @param ctx the parse tree
	 */
	void exitStructure(LGIRParser.StructureContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#function}.
	 * @param ctx the parse tree
	 */
	void enterFunction(LGIRParser.FunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#function}.
	 * @param ctx the parse tree
	 */
	void exitFunction(LGIRParser.FunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#constant_pool}.
	 * @param ctx the parse tree
	 */
	void enterConstant_pool(LGIRParser.Constant_poolContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#constant_pool}.
	 * @param ctx the parse tree
	 */
	void exitConstant_pool(LGIRParser.Constant_poolContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#global_data}.
	 * @param ctx the parse tree
	 */
	void enterGlobal_data(LGIRParser.Global_dataContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#global_data}.
	 * @param ctx the parse tree
	 */
	void exitGlobal_data(LGIRParser.Global_dataContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#init}.
	 * @param ctx the parse tree
	 */
	void enterInit(LGIRParser.InitContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#init}.
	 * @param ctx the parse tree
	 */
	void exitInit(LGIRParser.InitContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#function_body}.
	 * @param ctx the parse tree
	 */
	void enterFunction_body(LGIRParser.Function_bodyContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#function_body}.
	 * @param ctx the parse tree
	 */
	void exitFunction_body(LGIRParser.Function_bodyContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#locals_}.
	 * @param ctx the parse tree
	 */
	void enterLocals_(LGIRParser.Locals_Context ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#locals_}.
	 * @param ctx the parse tree
	 */
	void exitLocals_(LGIRParser.Locals_Context ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterInstruction(LGIRParser.InstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitInstruction(LGIRParser.InstructionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#cmp}.
	 * @param ctx the parse tree
	 */
	void enterCmp(LGIRParser.CmpContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#cmp}.
	 * @param ctx the parse tree
	 */
	void exitCmp(LGIRParser.CmpContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#conditional_jump}.
	 * @param ctx the parse tree
	 */
	void enterConditional_jump(LGIRParser.Conditional_jumpContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#conditional_jump}.
	 * @param ctx the parse tree
	 */
	void exitConditional_jump(LGIRParser.Conditional_jumpContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#increase}.
	 * @param ctx the parse tree
	 */
	void enterIncrease(LGIRParser.IncreaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#increase}.
	 * @param ctx the parse tree
	 */
	void exitIncrease(LGIRParser.IncreaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#decrease}.
	 * @param ctx the parse tree
	 */
	void enterDecrease(LGIRParser.DecreaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#decrease}.
	 * @param ctx the parse tree
	 */
	void exitDecrease(LGIRParser.DecreaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#atomic_increase}.
	 * @param ctx the parse tree
	 */
	void enterAtomic_increase(LGIRParser.Atomic_increaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#atomic_increase}.
	 * @param ctx the parse tree
	 */
	void exitAtomic_increase(LGIRParser.Atomic_increaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#atomic_decrease}.
	 * @param ctx the parse tree
	 */
	void enterAtomic_decrease(LGIRParser.Atomic_decreaseContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#atomic_decrease}.
	 * @param ctx the parse tree
	 */
	void exitAtomic_decrease(LGIRParser.Atomic_decreaseContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#malloc}.
	 * @param ctx the parse tree
	 */
	void enterMalloc(LGIRParser.MallocContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#malloc}.
	 * @param ctx the parse tree
	 */
	void exitMalloc(LGIRParser.MallocContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#free}.
	 * @param ctx the parse tree
	 */
	void enterFree(LGIRParser.FreeContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#free}.
	 * @param ctx the parse tree
	 */
	void exitFree(LGIRParser.FreeContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#realloc}.
	 * @param ctx the parse tree
	 */
	void enterRealloc(LGIRParser.ReallocContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#realloc}.
	 * @param ctx the parse tree
	 */
	void exitRealloc(LGIRParser.ReallocContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#get}.
	 * @param ctx the parse tree
	 */
	void enterGet(LGIRParser.GetContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#get}.
	 * @param ctx the parse tree
	 */
	void exitGet(LGIRParser.GetContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#set}.
	 * @param ctx the parse tree
	 */
	void enterSet(LGIRParser.SetContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#set}.
	 * @param ctx the parse tree
	 */
	void exitSet(LGIRParser.SetContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#goto}.
	 * @param ctx the parse tree
	 */
	void enterGoto(LGIRParser.GotoContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#goto}.
	 * @param ctx the parse tree
	 */
	void exitGoto(LGIRParser.GotoContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#nop}.
	 * @param ctx the parse tree
	 */
	void enterNop(LGIRParser.NopContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#nop}.
	 * @param ctx the parse tree
	 */
	void exitNop(LGIRParser.NopContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#negate}.
	 * @param ctx the parse tree
	 */
	void enterNegate(LGIRParser.NegateContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#negate}.
	 * @param ctx the parse tree
	 */
	void exitNegate(LGIRParser.NegateContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#not}.
	 * @param ctx the parse tree
	 */
	void enterNot(LGIRParser.NotContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#not}.
	 * @param ctx the parse tree
	 */
	void exitNot(LGIRParser.NotContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#return}.
	 * @param ctx the parse tree
	 */
	void enterReturn(LGIRParser.ReturnContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#return}.
	 * @param ctx the parse tree
	 */
	void exitReturn(LGIRParser.ReturnContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#set_virtual_register}.
	 * @param ctx the parse tree
	 */
	void enterSet_virtual_register(LGIRParser.Set_virtual_registerContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#set_virtual_register}.
	 * @param ctx the parse tree
	 */
	void exitSet_virtual_register(LGIRParser.Set_virtual_registerContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#stack_alloc}.
	 * @param ctx the parse tree
	 */
	void enterStack_alloc(LGIRParser.Stack_allocContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#stack_alloc}.
	 * @param ctx the parse tree
	 */
	void exitStack_alloc(LGIRParser.Stack_allocContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#type_cast}.
	 * @param ctx the parse tree
	 */
	void enterType_cast(LGIRParser.Type_castContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#type_cast}.
	 * @param ctx the parse tree
	 */
	void exitType_cast(LGIRParser.Type_castContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#calculate}.
	 * @param ctx the parse tree
	 */
	void enterCalculate(LGIRParser.CalculateContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#calculate}.
	 * @param ctx the parse tree
	 */
	void exitCalculate(LGIRParser.CalculateContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#asm}.
	 * @param ctx the parse tree
	 */
	void enterAsm(LGIRParser.AsmContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#asm}.
	 * @param ctx the parse tree
	 */
	void exitAsm(LGIRParser.AsmContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#invoke}.
	 * @param ctx the parse tree
	 */
	void enterInvoke(LGIRParser.InvokeContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#invoke}.
	 * @param ctx the parse tree
	 */
	void exitInvoke(LGIRParser.InvokeContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#fields}.
	 * @param ctx the parse tree
	 */
	void enterFields(LGIRParser.FieldsContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#fields}.
	 * @param ctx the parse tree
	 */
	void exitFields(LGIRParser.FieldsContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#field}.
	 * @param ctx the parse tree
	 */
	void enterField(LGIRParser.FieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#field}.
	 * @param ctx the parse tree
	 */
	void exitField(LGIRParser.FieldContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(LGIRParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(LGIRParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#base_type}.
	 * @param ctx the parse tree
	 */
	void enterBase_type(LGIRParser.Base_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#base_type}.
	 * @param ctx the parse tree
	 */
	void exitBase_type(LGIRParser.Base_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#void_type}.
	 * @param ctx the parse tree
	 */
	void enterVoid_type(LGIRParser.Void_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#void_type}.
	 * @param ctx the parse tree
	 */
	void exitVoid_type(LGIRParser.Void_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#operand}.
	 * @param ctx the parse tree
	 */
	void enterOperand(LGIRParser.OperandContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#operand}.
	 * @param ctx the parse tree
	 */
	void exitOperand(LGIRParser.OperandContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#virtual_register}.
	 * @param ctx the parse tree
	 */
	void enterVirtual_register(LGIRParser.Virtual_registerContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#virtual_register}.
	 * @param ctx the parse tree
	 */
	void exitVirtual_register(LGIRParser.Virtual_registerContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(LGIRParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(LGIRParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(LGIRParser.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(LGIRParser.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#type_cast_kind}.
	 * @param ctx the parse tree
	 */
	void enterType_cast_kind(LGIRParser.Type_cast_kindContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#type_cast_kind}.
	 * @param ctx the parse tree
	 */
	void exitType_cast_kind(LGIRParser.Type_cast_kindContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#calculation_operator}.
	 * @param ctx the parse tree
	 */
	void enterCalculation_operator(LGIRParser.Calculation_operatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#calculation_operator}.
	 * @param ctx the parse tree
	 */
	void exitCalculation_operator(LGIRParser.Calculation_operatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#asm_resource}.
	 * @param ctx the parse tree
	 */
	void enterAsm_resource(LGIRParser.Asm_resourceContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#asm_resource}.
	 * @param ctx the parse tree
	 */
	void exitAsm_resource(LGIRParser.Asm_resourceContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#argument}.
	 * @param ctx the parse tree
	 */
	void enterArgument(LGIRParser.ArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#argument}.
	 * @param ctx the parse tree
	 */
	void exitArgument(LGIRParser.ArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#constant_pool_entries}.
	 * @param ctx the parse tree
	 */
	void enterConstant_pool_entries(LGIRParser.Constant_pool_entriesContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#constant_pool_entries}.
	 * @param ctx the parse tree
	 */
	void exitConstant_pool_entries(LGIRParser.Constant_pool_entriesContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#constant_pool_entry}.
	 * @param ctx the parse tree
	 */
	void enterConstant_pool_entry(LGIRParser.Constant_pool_entryContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#constant_pool_entry}.
	 * @param ctx the parse tree
	 */
	void exitConstant_pool_entry(LGIRParser.Constant_pool_entryContext ctx);
	/**
	 * Enter a parse tree produced by {@link LGIRParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(LGIRParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link LGIRParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(LGIRParser.ConstantContext ctx);
}