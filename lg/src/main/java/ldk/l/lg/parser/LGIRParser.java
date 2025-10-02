// Generated from C:/Users/XiaoLi/Documents/Projects/L-Language/lg/src/main/antlr/LGIR.g4 by ANTLR 4.13.2
package ldk.l.lg.parser;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue", "this-escape"})
public class LGIRParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, NUMBER=36, WS=37, SHARP=38, PERCENT=39, 
		COMMA=40, COLON=41, SEMICOLON=42, OPEN_PAREN=43, CLOSE_PAREN=44, OPEN_BRACKET=45, 
		CLOSE_BRACKET=46, OPEN_BRACE=47, CLOSE_BRACE=48, ADD=49, SUB=50, MUL=51, 
		DIV=52, MOD=53, AND=54, OR=55, XOR=56, SHL=57, SHR=58, USHR=59, NEG=60, 
		NOT=61, ZEXT=62, SEXT=63, TRUNC=64, ITOF=65, FTOI=66, FEXT=67, FTRUNC=68, 
		VOID=69, U1=70, U8=71, U16=72, U32=73, U64=74, I1=75, I8=76, I16=77, I32=78, 
		I64=79, FLOAT=80, DOUBLE=81, STRING_LITERAL=82, IDENTIFIER=83;
	public static final int
		RULE_program = 0, RULE_structure = 1, RULE_function = 2, RULE_constant_pool = 3, 
		RULE_global_data = 4, RULE_init = 5, RULE_function_body = 6, RULE_locals_ = 7, 
		RULE_instruction = 8, RULE_cmp = 9, RULE_conditional_jump = 10, RULE_increase = 11, 
		RULE_decrease = 12, RULE_atomic_increase = 13, RULE_atomic_decrease = 14, 
		RULE_malloc = 15, RULE_free = 16, RULE_realloc = 17, RULE_get = 18, RULE_set = 19, 
		RULE_goto = 20, RULE_nop = 21, RULE_negate = 22, RULE_not = 23, RULE_return = 24, 
		RULE_set_virtual_register = 25, RULE_stack_alloc = 26, RULE_type_cast = 27, 
		RULE_calculate = 28, RULE_asm = 29, RULE_invoke = 30, RULE_fields = 31, 
		RULE_field = 32, RULE_type = 33, RULE_base_type = 34, RULE_void_type = 35, 
		RULE_operand = 36, RULE_virtual_register = 37, RULE_condition = 38, RULE_label = 39, 
		RULE_type_cast_kind = 40, RULE_calculation_operator = 41, RULE_asm_resource = 42, 
		RULE_argument = 43, RULE_constant_pool_entries = 44, RULE_constant_pool_entry = 45, 
		RULE_constant = 46;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "structure", "function", "constant_pool", "global_data", "init", 
			"function_body", "locals_", "instruction", "cmp", "conditional_jump", 
			"increase", "decrease", "atomic_increase", "atomic_decrease", "malloc", 
			"free", "realloc", "get", "set", "goto", "nop", "negate", "not", "return", 
			"set_virtual_register", "stack_alloc", "type_cast", "calculate", "asm", 
			"invoke", "fields", "field", "type", "base_type", "void_type", "operand", 
			"virtual_register", "condition", "label", "type_cast_kind", "calculation_operator", 
			"asm_resource", "argument", "constant_pool_entries", "constant_pool_entry", 
			"constant"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'structure'", "'function'", "'constant_pool'", "'='", "'init'", 
			"'locals'", "'atomic_'", "'cmp'", "'conditional_jump'", "'increase'", 
			"'decrease'", "'atomic_increase'", "'atomic_decrease'", "'malloc'", "'free'", 
			"'realloc'", "'get'", "'set'", "'goto'", "'nop'", "'negate'", "'return'", 
			"'stack_alloc'", "'to'", "'asm'", "'invoke'", "'*'", "'e'", "'ne'", "'l'", 
			"'le'", "'g'", "'ge'", "'if_true'", "'if_false'", null, null, "'#'", 
			"'%'", "','", "':'", "';'", "'('", "')'", "'['", "']'", "'{'", "'}'", 
			"'add'", "'sub'", "'mul'", "'div'", "'mod'", "'and'", "'or'", "'xor'", 
			"'shl'", "'shr'", "'ushr'", "'neg'", "'not'", "'zext'", "'sext'", "'trunc'", 
			"'itof'", "'ftoi'", "'fext'", "'ftrunc'", "'void'", "'u1'", "'u8'", "'u16'", 
			"'u32'", "'u64'", "'i1'", "'i8'", "'i16'", "'i32'", "'i64'", "'float'", 
			"'double'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			"NUMBER", "WS", "SHARP", "PERCENT", "COMMA", "COLON", "SEMICOLON", "OPEN_PAREN", 
			"CLOSE_PAREN", "OPEN_BRACKET", "CLOSE_BRACKET", "OPEN_BRACE", "CLOSE_BRACE", 
			"ADD", "SUB", "MUL", "DIV", "MOD", "AND", "OR", "XOR", "SHL", "SHR", 
			"USHR", "NEG", "NOT", "ZEXT", "SEXT", "TRUNC", "ITOF", "FTOI", "FEXT", 
			"FTRUNC", "VOID", "U1", "U8", "U16", "U32", "U64", "I1", "I8", "I16", 
			"I32", "I64", "FLOAT", "DOUBLE", "STRING_LITERAL", "IDENTIFIER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "LGIR.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public LGIRParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public List<StructureContext> structure() {
			return getRuleContexts(StructureContext.class);
		}
		public StructureContext structure(int i) {
			return getRuleContext(StructureContext.class,i);
		}
		public List<FunctionContext> function() {
			return getRuleContexts(FunctionContext.class);
		}
		public FunctionContext function(int i) {
			return getRuleContext(FunctionContext.class,i);
		}
		public List<Global_dataContext> global_data() {
			return getRuleContexts(Global_dataContext.class);
		}
		public Global_dataContext global_data(int i) {
			return getRuleContext(Global_dataContext.class,i);
		}
		public Constant_poolContext constant_pool() {
			return getRuleContext(Constant_poolContext.class,0);
		}
		public InitContext init() {
			return getRuleContext(InitContext.class,0);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitProgram(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__0) {
				{
				{
				setState(94);
				structure();
				}
				}
				setState(99);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__1) {
				{
				{
				setState(100);
				function();
				}
				}
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==IDENTIFIER) {
				{
				{
				setState(106);
				global_data();
				}
				}
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__2) {
				{
				setState(112);
				constant_pool();
				}
			}

			setState(116);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__4) {
				{
				setState(115);
				init();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StructureContext extends ParserRuleContext {
		public List<TerminalNode> CLOSE_BRACE() { return getTokens(LGIRParser.CLOSE_BRACE); }
		public TerminalNode CLOSE_BRACE(int i) {
			return getToken(LGIRParser.CLOSE_BRACE, i);
		}
		public FieldsContext fields() {
			return getRuleContext(FieldsContext.class,0);
		}
		public StructureContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_structure; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterStructure(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitStructure(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitStructure(this);
			else return visitor.visitChildren(this);
		}
	}

	public final StructureContext structure() throws RecognitionException {
		StructureContext _localctx = new StructureContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_structure);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118);
			match(T__0);
			setState(119);
			match(CLOSE_BRACE);
			setState(120);
			fields();
			setState(121);
			match(CLOSE_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FunctionContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(LGIRParser.IDENTIFIER, 0); }
		public TerminalNode OPEN_PAREN() { return getToken(LGIRParser.OPEN_PAREN, 0); }
		public FieldsContext fields() {
			return getRuleContext(FieldsContext.class,0);
		}
		public TerminalNode CLOSE_PAREN() { return getToken(LGIRParser.CLOSE_PAREN, 0); }
		public Function_bodyContext function_body() {
			return getRuleContext(Function_bodyContext.class,0);
		}
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterFunction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitFunction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_function);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(123);
			match(T__1);
			setState(124);
			type(0);
			setState(125);
			match(IDENTIFIER);
			setState(126);
			match(OPEN_PAREN);
			setState(127);
			fields();
			setState(128);
			match(CLOSE_PAREN);
			setState(129);
			function_body();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Constant_poolContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACE() { return getToken(LGIRParser.OPEN_BRACE, 0); }
		public Constant_pool_entriesContext constant_pool_entries() {
			return getRuleContext(Constant_pool_entriesContext.class,0);
		}
		public TerminalNode CLOSE_BRACE() { return getToken(LGIRParser.CLOSE_BRACE, 0); }
		public Constant_poolContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant_pool; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterConstant_pool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitConstant_pool(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitConstant_pool(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Constant_poolContext constant_pool() throws RecognitionException {
		Constant_poolContext _localctx = new Constant_poolContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_constant_pool);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131);
			match(T__2);
			setState(132);
			match(OPEN_BRACE);
			setState(133);
			constant_pool_entries();
			setState(134);
			match(CLOSE_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Global_dataContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(LGIRParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(LGIRParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public Global_dataContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_global_data; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterGlobal_data(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitGlobal_data(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitGlobal_data(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Global_dataContext global_data() throws RecognitionException {
		Global_dataContext _localctx = new Global_dataContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_global_data);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(136);
			match(IDENTIFIER);
			setState(141);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case COLON:
				{
				{
				setState(137);
				match(COLON);
				setState(138);
				type(0);
				}
				}
				break;
			case T__3:
				{
				{
				setState(139);
				match(T__3);
				setState(140);
				constant();
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InitContext extends ParserRuleContext {
		public Function_bodyContext function_body() {
			return getRuleContext(Function_bodyContext.class,0);
		}
		public InitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_init; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterInit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitInit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitInit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InitContext init() throws RecognitionException {
		InitContext _localctx = new InitContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_init);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(143);
			match(T__4);
			setState(144);
			function_body();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Function_bodyContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACE() { return getToken(LGIRParser.OPEN_BRACE, 0); }
		public TerminalNode CLOSE_BRACE() { return getToken(LGIRParser.CLOSE_BRACE, 0); }
		public Locals_Context locals_() {
			return getRuleContext(Locals_Context.class,0);
		}
		public List<InstructionContext> instruction() {
			return getRuleContexts(InstructionContext.class);
		}
		public InstructionContext instruction(int i) {
			return getRuleContext(InstructionContext.class,i);
		}
		public Function_bodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_body; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterFunction_body(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitFunction_body(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitFunction_body(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Function_bodyContext function_body() throws RecognitionException {
		Function_bodyContext _localctx = new Function_bodyContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_function_body);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(OPEN_BRACE);
			setState(148);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__5) {
				{
				setState(147);
				locals_();
				}
			}

			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 549862552192L) != 0)) {
				{
				{
				setState(150);
				instruction();
				}
				}
				setState(155);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(156);
			match(CLOSE_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Locals_Context extends ParserRuleContext {
		public TerminalNode OPEN_BRACE() { return getToken(LGIRParser.OPEN_BRACE, 0); }
		public FieldsContext fields() {
			return getRuleContext(FieldsContext.class,0);
		}
		public TerminalNode CLOSE_BRACE() { return getToken(LGIRParser.CLOSE_BRACE, 0); }
		public Locals_Context(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_locals_; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterLocals_(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitLocals_(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitLocals_(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Locals_Context locals_() throws RecognitionException {
		Locals_Context _localctx = new Locals_Context(_ctx, getState());
		enterRule(_localctx, 14, RULE_locals_);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(158);
			match(T__5);
			setState(159);
			match(OPEN_BRACE);
			setState(160);
			fields();
			setState(161);
			match(CLOSE_BRACE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InstructionContext extends ParserRuleContext {
		public CmpContext cmp() {
			return getRuleContext(CmpContext.class,0);
		}
		public Conditional_jumpContext conditional_jump() {
			return getRuleContext(Conditional_jumpContext.class,0);
		}
		public IncreaseContext increase() {
			return getRuleContext(IncreaseContext.class,0);
		}
		public DecreaseContext decrease() {
			return getRuleContext(DecreaseContext.class,0);
		}
		public Atomic_increaseContext atomic_increase() {
			return getRuleContext(Atomic_increaseContext.class,0);
		}
		public Atomic_decreaseContext atomic_decrease() {
			return getRuleContext(Atomic_decreaseContext.class,0);
		}
		public MallocContext malloc() {
			return getRuleContext(MallocContext.class,0);
		}
		public FreeContext free() {
			return getRuleContext(FreeContext.class,0);
		}
		public ReallocContext realloc() {
			return getRuleContext(ReallocContext.class,0);
		}
		public GetContext get() {
			return getRuleContext(GetContext.class,0);
		}
		public SetContext set() {
			return getRuleContext(SetContext.class,0);
		}
		public GotoContext goto_() {
			return getRuleContext(GotoContext.class,0);
		}
		public NopContext nop() {
			return getRuleContext(NopContext.class,0);
		}
		public NegateContext negate() {
			return getRuleContext(NegateContext.class,0);
		}
		public NotContext not() {
			return getRuleContext(NotContext.class,0);
		}
		public ReturnContext return_() {
			return getRuleContext(ReturnContext.class,0);
		}
		public Set_virtual_registerContext set_virtual_register() {
			return getRuleContext(Set_virtual_registerContext.class,0);
		}
		public Stack_allocContext stack_alloc() {
			return getRuleContext(Stack_allocContext.class,0);
		}
		public Type_castContext type_cast() {
			return getRuleContext(Type_castContext.class,0);
		}
		public CalculateContext calculate() {
			return getRuleContext(CalculateContext.class,0);
		}
		public AsmContext asm() {
			return getRuleContext(AsmContext.class,0);
		}
		public InvokeContext invoke() {
			return getRuleContext(InvokeContext.class,0);
		}
		public InstructionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_instruction; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterInstruction(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitInstruction(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitInstruction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InstructionContext instruction() throws RecognitionException {
		InstructionContext _localctx = new InstructionContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_instruction);
		try {
			setState(185);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,8,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(163);
				cmp();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(164);
				conditional_jump();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(165);
				increase();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(166);
				decrease();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(167);
				atomic_increase();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(168);
				atomic_decrease();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(169);
				malloc();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(170);
				free();
				}
				break;
			case 9:
				enterOuterAlt(_localctx, 9);
				{
				setState(171);
				realloc();
				}
				break;
			case 10:
				enterOuterAlt(_localctx, 10);
				{
				setState(172);
				get();
				}
				break;
			case 11:
				enterOuterAlt(_localctx, 11);
				{
				setState(173);
				set();
				}
				break;
			case 12:
				enterOuterAlt(_localctx, 12);
				{
				setState(174);
				goto_();
				}
				break;
			case 13:
				enterOuterAlt(_localctx, 13);
				{
				setState(175);
				nop();
				}
				break;
			case 14:
				enterOuterAlt(_localctx, 14);
				{
				setState(176);
				negate();
				}
				break;
			case 15:
				enterOuterAlt(_localctx, 15);
				{
				setState(177);
				not();
				}
				break;
			case 16:
				enterOuterAlt(_localctx, 16);
				{
				setState(178);
				return_();
				}
				break;
			case 17:
				enterOuterAlt(_localctx, 17);
				{
				setState(179);
				set_virtual_register();
				}
				break;
			case 18:
				enterOuterAlt(_localctx, 18);
				{
				setState(180);
				stack_alloc();
				}
				break;
			case 19:
				enterOuterAlt(_localctx, 19);
				{
				setState(181);
				type_cast();
				}
				break;
			case 20:
				enterOuterAlt(_localctx, 20);
				{
				setState(182);
				calculate();
				}
				break;
			case 21:
				enterOuterAlt(_localctx, 21);
				{
				setState(183);
				asm();
				}
				break;
			case 22:
				enterOuterAlt(_localctx, 22);
				{
				setState(184);
				invoke();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CmpContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public List<OperandContext> operand() {
			return getRuleContexts(OperandContext.class);
		}
		public OperandContext operand(int i) {
			return getRuleContext(OperandContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(LGIRParser.COMMA, 0); }
		public CmpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cmp; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterCmp(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitCmp(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitCmp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CmpContext cmp() throws RecognitionException {
		CmpContext _localctx = new CmpContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_cmp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(187);
			virtual_register();
			setState(188);
			match(T__3);
			setState(190);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(189);
				match(T__6);
				}
			}

			setState(192);
			match(T__7);
			setState(193);
			type(0);
			setState(194);
			condition();
			setState(195);
			operand();
			setState(198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COMMA) {
				{
				setState(196);
				match(COMMA);
				setState(197);
				operand();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Conditional_jumpContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ConditionContext condition() {
			return getRuleContext(ConditionContext.class,0);
		}
		public List<OperandContext> operand() {
			return getRuleContexts(OperandContext.class);
		}
		public OperandContext operand(int i) {
			return getRuleContext(OperandContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(LGIRParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(LGIRParser.COMMA, i);
		}
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public Conditional_jumpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditional_jump; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterConditional_jump(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitConditional_jump(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitConditional_jump(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Conditional_jumpContext conditional_jump() throws RecognitionException {
		Conditional_jumpContext _localctx = new Conditional_jumpContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_conditional_jump);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(200);
				match(T__6);
				}
			}

			setState(203);
			match(T__8);
			setState(204);
			type(0);
			setState(205);
			condition();
			setState(206);
			operand();
			setState(209);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				{
				setState(207);
				match(COMMA);
				setState(208);
				operand();
				}
				break;
			}
			setState(211);
			match(COMMA);
			setState(212);
			label();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class IncreaseContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public IncreaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_increase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterIncrease(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitIncrease(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitIncrease(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IncreaseContext increase() throws RecognitionException {
		IncreaseContext _localctx = new IncreaseContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_increase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214);
			virtual_register();
			setState(215);
			match(T__3);
			setState(216);
			match(T__9);
			setState(217);
			type(0);
			setState(218);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DecreaseContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public DecreaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_decrease; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterDecrease(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitDecrease(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitDecrease(this);
			else return visitor.visitChildren(this);
		}
	}

	public final DecreaseContext decrease() throws RecognitionException {
		DecreaseContext _localctx = new DecreaseContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_decrease);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(220);
			virtual_register();
			setState(221);
			match(T__3);
			setState(222);
			match(T__10);
			setState(223);
			type(0);
			setState(224);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Atomic_increaseContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public Atomic_increaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic_increase; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterAtomic_increase(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitAtomic_increase(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitAtomic_increase(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Atomic_increaseContext atomic_increase() throws RecognitionException {
		Atomic_increaseContext _localctx = new Atomic_increaseContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_atomic_increase);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(226);
			match(T__11);
			setState(227);
			type(0);
			setState(228);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Atomic_decreaseContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public Atomic_decreaseContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_atomic_decrease; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterAtomic_decrease(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitAtomic_decrease(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitAtomic_decrease(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Atomic_decreaseContext atomic_decrease() throws RecognitionException {
		Atomic_decreaseContext _localctx = new Atomic_decreaseContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_atomic_decrease);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(230);
			match(T__12);
			setState(231);
			type(0);
			setState(232);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class MallocContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public MallocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_malloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterMalloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitMalloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitMalloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MallocContext malloc() throws RecognitionException {
		MallocContext _localctx = new MallocContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_malloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(234);
			virtual_register();
			setState(235);
			match(T__3);
			setState(236);
			match(T__13);
			setState(237);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FreeContext extends ParserRuleContext {
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public FreeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_free; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterFree(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitFree(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitFree(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FreeContext free() throws RecognitionException {
		FreeContext _localctx = new FreeContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_free);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(239);
			match(T__14);
			setState(240);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReallocContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public List<OperandContext> operand() {
			return getRuleContexts(OperandContext.class);
		}
		public OperandContext operand(int i) {
			return getRuleContext(OperandContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(LGIRParser.COMMA, 0); }
		public ReallocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_realloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterRealloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitRealloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitRealloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReallocContext realloc() throws RecognitionException {
		ReallocContext _localctx = new ReallocContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_realloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(242);
			virtual_register();
			setState(243);
			match(T__3);
			setState(244);
			match(T__15);
			setState(245);
			operand();
			setState(246);
			match(COMMA);
			setState(247);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GetContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public GetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_get; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterGet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitGet(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitGet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GetContext get() throws RecognitionException {
		GetContext _localctx = new GetContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_get);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(249);
			virtual_register();
			setState(250);
			match(T__3);
			setState(251);
			match(T__16);
			setState(252);
			type(0);
			setState(253);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class SetContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<OperandContext> operand() {
			return getRuleContexts(OperandContext.class);
		}
		public OperandContext operand(int i) {
			return getRuleContext(OperandContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(LGIRParser.COMMA, 0); }
		public SetContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterSet(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitSet(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitSet(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SetContext set() throws RecognitionException {
		SetContext _localctx = new SetContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_set);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(255);
			match(T__17);
			setState(256);
			type(0);
			setState(257);
			operand();
			setState(258);
			match(COMMA);
			setState(259);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class GotoContext extends ParserRuleContext {
		public LabelContext label() {
			return getRuleContext(LabelContext.class,0);
		}
		public GotoContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_goto; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterGoto(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitGoto(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitGoto(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GotoContext goto_() throws RecognitionException {
		GotoContext _localctx = new GotoContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_goto);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(261);
			match(T__18);
			setState(262);
			label();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NopContext extends ParserRuleContext {
		public NopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_nop; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterNop(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitNop(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitNop(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NopContext nop() throws RecognitionException {
		NopContext _localctx = new NopContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_nop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(264);
			match(T__19);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NegateContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public NegateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_negate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterNegate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitNegate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitNegate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NegateContext negate() throws RecognitionException {
		NegateContext _localctx = new NegateContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_negate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(266);
			virtual_register();
			setState(267);
			match(T__3);
			setState(269);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(268);
				match(T__6);
				}
			}

			setState(271);
			match(T__20);
			setState(272);
			type(0);
			setState(273);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class NotContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public TerminalNode NOT() { return getToken(LGIRParser.NOT, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public NotContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_not; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterNot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitNot(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitNot(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NotContext not() throws RecognitionException {
		NotContext _localctx = new NotContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_not);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(275);
			virtual_register();
			setState(276);
			match(T__3);
			setState(278);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(277);
				match(T__6);
				}
			}

			setState(280);
			match(NOT);
			setState(281);
			type(0);
			setState(282);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReturnContext extends ParserRuleContext {
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public ReturnContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_return; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterReturn(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitReturn(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitReturn(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ReturnContext return_() throws RecognitionException {
		ReturnContext _localctx = new ReturnContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_return);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(284);
			match(T__21);
			setState(286);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,15,_ctx) ) {
			case 1:
				{
				setState(285);
				operand();
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Set_virtual_registerContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public Set_virtual_registerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_set_virtual_register; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterSet_virtual_register(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitSet_virtual_register(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitSet_virtual_register(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Set_virtual_registerContext set_virtual_register() throws RecognitionException {
		Set_virtual_registerContext _localctx = new Set_virtual_registerContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_set_virtual_register);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(288);
			virtual_register();
			setState(289);
			match(T__3);
			setState(290);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Stack_allocContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public Stack_allocContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stack_alloc; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterStack_alloc(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitStack_alloc(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitStack_alloc(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Stack_allocContext stack_alloc() throws RecognitionException {
		Stack_allocContext _localctx = new Stack_allocContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_stack_alloc);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(292);
			virtual_register();
			setState(293);
			match(T__3);
			setState(294);
			match(T__22);
			setState(295);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Type_castContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public Type_cast_kindContext type_cast_kind() {
			return getRuleContext(Type_cast_kindContext.class,0);
		}
		public List<TypeContext> type() {
			return getRuleContexts(TypeContext.class);
		}
		public TypeContext type(int i) {
			return getRuleContext(TypeContext.class,i);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public Type_castContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_cast; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterType_cast(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitType_cast(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitType_cast(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_castContext type_cast() throws RecognitionException {
		Type_castContext _localctx = new Type_castContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_type_cast);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(297);
			virtual_register();
			setState(298);
			match(T__3);
			setState(299);
			type_cast_kind();
			setState(300);
			type(0);
			setState(301);
			operand();
			setState(302);
			match(T__23);
			setState(303);
			type(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class CalculateContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public Calculation_operatorContext calculation_operator() {
			return getRuleContext(Calculation_operatorContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<OperandContext> operand() {
			return getRuleContexts(OperandContext.class);
		}
		public OperandContext operand(int i) {
			return getRuleContext(OperandContext.class,i);
		}
		public TerminalNode COMMA() { return getToken(LGIRParser.COMMA, 0); }
		public CalculateContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_calculate; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterCalculate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitCalculate(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitCalculate(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CalculateContext calculate() throws RecognitionException {
		CalculateContext _localctx = new CalculateContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_calculate);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(305);
			virtual_register();
			setState(306);
			match(T__3);
			setState(308);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__6) {
				{
				setState(307);
				match(T__6);
				}
			}

			setState(310);
			calculation_operator();
			setState(311);
			type(0);
			setState(312);
			operand();
			setState(313);
			match(COMMA);
			setState(314);
			operand();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AsmContext extends ParserRuleContext {
		public TerminalNode STRING_LITERAL() { return getToken(LGIRParser.STRING_LITERAL, 0); }
		public List<TerminalNode> COMMA() { return getTokens(LGIRParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(LGIRParser.COMMA, i);
		}
		public List<Asm_resourceContext> asm_resource() {
			return getRuleContexts(Asm_resourceContext.class);
		}
		public Asm_resourceContext asm_resource(int i) {
			return getRuleContext(Asm_resourceContext.class,i);
		}
		public AsmContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_asm; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterAsm(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitAsm(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitAsm(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AsmContext asm() throws RecognitionException {
		AsmContext _localctx = new AsmContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_asm);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			match(T__24);
			setState(317);
			match(STRING_LITERAL);
			setState(322);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(318);
				match(COMMA);
				setState(319);
				asm_resource();
				}
				}
				setState(324);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InvokeContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(LGIRParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(LGIRParser.COMMA, i);
		}
		public List<ArgumentContext> argument() {
			return getRuleContexts(ArgumentContext.class);
		}
		public ArgumentContext argument(int i) {
			return getRuleContext(ArgumentContext.class,i);
		}
		public InvokeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_invoke; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterInvoke(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitInvoke(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitInvoke(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InvokeContext invoke() throws RecognitionException {
		InvokeContext _localctx = new InvokeContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_invoke);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(328);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==PERCENT) {
				{
				setState(325);
				virtual_register();
				setState(326);
				match(T__3);
				}
			}

			setState(330);
			match(T__25);
			setState(331);
			type(0);
			setState(332);
			operand();
			setState(337);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(333);
				match(COMMA);
				setState(334);
				argument();
				}
				}
				setState(339);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FieldsContext extends ParserRuleContext {
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(LGIRParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(LGIRParser.COMMA, i);
		}
		public FieldsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_fields; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterFields(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitFields(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitFields(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldsContext fields() throws RecognitionException {
		FieldsContext _localctx = new FieldsContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_fields);
		int _la;
		try {
			setState(349);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case IDENTIFIER:
				enterOuterAlt(_localctx, 1);
				{
				setState(340);
				field();
				setState(345);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(341);
					match(COMMA);
					setState(342);
					field();
					}
					}
					setState(347);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case CLOSE_PAREN:
			case CLOSE_BRACE:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FieldContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(LGIRParser.IDENTIFIER, 0); }
		public TerminalNode COLON() { return getToken(LGIRParser.COLON, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitField(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitField(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FieldContext field() throws RecognitionException {
		FieldContext _localctx = new FieldContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_field);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(351);
			match(IDENTIFIER);
			setState(352);
			match(COLON);
			setState(353);
			type(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public Base_typeContext base_type() {
			return getRuleContext(Base_typeContext.class,0);
		}
		public Void_typeContext void_type() {
			return getRuleContext(Void_typeContext.class,0);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterType(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitType(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitType(this);
			else return visitor.visitChildren(this);
		}
	}

	public final TypeContext type() throws RecognitionException {
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 66;
		enterRecursionRule(_localctx, 66, RULE_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(358);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case U1:
			case U8:
			case U16:
			case U32:
			case U64:
			case I1:
			case I8:
			case I16:
			case I32:
			case I64:
			case FLOAT:
			case DOUBLE:
				{
				setState(356);
				base_type();
				}
				break;
			case VOID:
				{
				setState(357);
				void_type();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(364);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new TypeContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(360);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(361);
					match(T__26);
					}
					} 
				}
				setState(366);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,23,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Base_typeContext extends ParserRuleContext {
		public TerminalNode U1() { return getToken(LGIRParser.U1, 0); }
		public TerminalNode U8() { return getToken(LGIRParser.U8, 0); }
		public TerminalNode U16() { return getToken(LGIRParser.U16, 0); }
		public TerminalNode U32() { return getToken(LGIRParser.U32, 0); }
		public TerminalNode U64() { return getToken(LGIRParser.U64, 0); }
		public TerminalNode I1() { return getToken(LGIRParser.I1, 0); }
		public TerminalNode I8() { return getToken(LGIRParser.I8, 0); }
		public TerminalNode I16() { return getToken(LGIRParser.I16, 0); }
		public TerminalNode I32() { return getToken(LGIRParser.I32, 0); }
		public TerminalNode I64() { return getToken(LGIRParser.I64, 0); }
		public TerminalNode FLOAT() { return getToken(LGIRParser.FLOAT, 0); }
		public TerminalNode DOUBLE() { return getToken(LGIRParser.DOUBLE, 0); }
		public Base_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_base_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterBase_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitBase_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitBase_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Base_typeContext base_type() throws RecognitionException {
		Base_typeContext _localctx = new Base_typeContext(_ctx, getState());
		enterRule(_localctx, 68, RULE_base_type);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(367);
			_la = _input.LA(1);
			if ( !(((((_la - 70)) & ~0x3f) == 0 && ((1L << (_la - 70)) & 4095L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Void_typeContext extends ParserRuleContext {
		public TerminalNode VOID() { return getToken(LGIRParser.VOID, 0); }
		public Void_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_void_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterVoid_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitVoid_type(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitVoid_type(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Void_typeContext void_type() throws RecognitionException {
		Void_typeContext _localctx = new Void_typeContext(_ctx, getState());
		enterRule(_localctx, 70, RULE_void_type);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(369);
			match(VOID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OperandContext extends ParserRuleContext {
		public Virtual_registerContext virtual_register() {
			return getRuleContext(Virtual_registerContext.class,0);
		}
		public OperandContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operand; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterOperand(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitOperand(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitOperand(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OperandContext operand() throws RecognitionException {
		OperandContext _localctx = new OperandContext(_ctx, getState());
		enterRule(_localctx, 72, RULE_operand);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(371);
			virtual_register();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Virtual_registerContext extends ParserRuleContext {
		public TerminalNode PERCENT() { return getToken(LGIRParser.PERCENT, 0); }
		public TerminalNode IDENTIFIER() { return getToken(LGIRParser.IDENTIFIER, 0); }
		public Virtual_registerContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_virtual_register; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterVirtual_register(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitVirtual_register(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitVirtual_register(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Virtual_registerContext virtual_register() throws RecognitionException {
		Virtual_registerContext _localctx = new Virtual_registerContext(_ctx, getState());
		enterRule(_localctx, 74, RULE_virtual_register);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(373);
			match(PERCENT);
			setState(374);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConditionContext extends ParserRuleContext {
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterCondition(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitCondition(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 76, RULE_condition);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(376);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 68451041280L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LabelContext extends ParserRuleContext {
		public TerminalNode SHARP() { return getToken(LGIRParser.SHARP, 0); }
		public TerminalNode IDENTIFIER() { return getToken(LGIRParser.IDENTIFIER, 0); }
		public LabelContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_label; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterLabel(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitLabel(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitLabel(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LabelContext label() throws RecognitionException {
		LabelContext _localctx = new LabelContext(_ctx, getState());
		enterRule(_localctx, 78, RULE_label);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(378);
			match(SHARP);
			setState(379);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Type_cast_kindContext extends ParserRuleContext {
		public TerminalNode ZEXT() { return getToken(LGIRParser.ZEXT, 0); }
		public TerminalNode SEXT() { return getToken(LGIRParser.SEXT, 0); }
		public TerminalNode TRUNC() { return getToken(LGIRParser.TRUNC, 0); }
		public TerminalNode ITOF() { return getToken(LGIRParser.ITOF, 0); }
		public TerminalNode FTOI() { return getToken(LGIRParser.FTOI, 0); }
		public TerminalNode FEXT() { return getToken(LGIRParser.FEXT, 0); }
		public TerminalNode FTRUNC() { return getToken(LGIRParser.FTRUNC, 0); }
		public Type_cast_kindContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_cast_kind; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterType_cast_kind(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitType_cast_kind(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitType_cast_kind(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_cast_kindContext type_cast_kind() throws RecognitionException {
		Type_cast_kindContext _localctx = new Type_cast_kindContext(_ctx, getState());
		enterRule(_localctx, 80, RULE_type_cast_kind);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(381);
			_la = _input.LA(1);
			if ( !(((((_la - 62)) & ~0x3f) == 0 && ((1L << (_la - 62)) & 127L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Calculation_operatorContext extends ParserRuleContext {
		public TerminalNode ADD() { return getToken(LGIRParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(LGIRParser.SUB, 0); }
		public TerminalNode MUL() { return getToken(LGIRParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(LGIRParser.DIV, 0); }
		public TerminalNode MOD() { return getToken(LGIRParser.MOD, 0); }
		public TerminalNode AND() { return getToken(LGIRParser.AND, 0); }
		public TerminalNode OR() { return getToken(LGIRParser.OR, 0); }
		public TerminalNode XOR() { return getToken(LGIRParser.XOR, 0); }
		public TerminalNode SHL() { return getToken(LGIRParser.SHL, 0); }
		public TerminalNode SHR() { return getToken(LGIRParser.SHR, 0); }
		public TerminalNode USHR() { return getToken(LGIRParser.USHR, 0); }
		public Calculation_operatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_calculation_operator; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterCalculation_operator(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitCalculation_operator(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitCalculation_operator(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Calculation_operatorContext calculation_operator() throws RecognitionException {
		Calculation_operatorContext _localctx = new Calculation_operatorContext(_ctx, getState());
		enterRule(_localctx, 82, RULE_calculation_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(383);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 1152358554653425664L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Asm_resourceContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACKET() { return getToken(LGIRParser.OPEN_BRACKET, 0); }
		public TerminalNode CLOSE_BRACKET() { return getToken(LGIRParser.CLOSE_BRACKET, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public List<TerminalNode> COMMA() { return getTokens(LGIRParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(LGIRParser.COMMA, i);
		}
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(LGIRParser.IDENTIFIER, 0); }
		public Asm_resourceContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_asm_resource; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterAsm_resource(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitAsm_resource(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitAsm_resource(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Asm_resourceContext asm_resource() throws RecognitionException {
		Asm_resourceContext _localctx = new Asm_resourceContext(_ctx, getState());
		enterRule(_localctx, 84, RULE_asm_resource);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(385);
			match(OPEN_BRACKET);
			{
			setState(386);
			type(0);
			setState(387);
			match(COMMA);
			setState(388);
			operand();
			setState(389);
			match(COMMA);
			setState(390);
			match(IDENTIFIER);
			}
			setState(392);
			match(CLOSE_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentContext extends ParserRuleContext {
		public TerminalNode OPEN_BRACKET() { return getToken(LGIRParser.OPEN_BRACKET, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode COMMA() { return getToken(LGIRParser.COMMA, 0); }
		public OperandContext operand() {
			return getRuleContext(OperandContext.class,0);
		}
		public TerminalNode CLOSE_BRACKET() { return getToken(LGIRParser.CLOSE_BRACKET, 0); }
		public ArgumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterArgument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitArgument(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitArgument(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArgumentContext argument() throws RecognitionException {
		ArgumentContext _localctx = new ArgumentContext(_ctx, getState());
		enterRule(_localctx, 86, RULE_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(394);
			match(OPEN_BRACKET);
			setState(395);
			type(0);
			setState(396);
			match(COMMA);
			setState(397);
			operand();
			setState(398);
			match(CLOSE_BRACKET);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Constant_pool_entriesContext extends ParserRuleContext {
		public List<Constant_pool_entryContext> constant_pool_entry() {
			return getRuleContexts(Constant_pool_entryContext.class);
		}
		public Constant_pool_entryContext constant_pool_entry(int i) {
			return getRuleContext(Constant_pool_entryContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(LGIRParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(LGIRParser.COMMA, i);
		}
		public Constant_pool_entriesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant_pool_entries; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterConstant_pool_entries(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitConstant_pool_entries(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitConstant_pool_entries(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Constant_pool_entriesContext constant_pool_entries() throws RecognitionException {
		Constant_pool_entriesContext _localctx = new Constant_pool_entriesContext(_ctx, getState());
		enterRule(_localctx, 88, RULE_constant_pool_entries);
		int _la;
		try {
			setState(409);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case VOID:
			case U1:
			case U8:
			case U16:
			case U32:
			case U64:
			case I1:
			case I8:
			case I16:
			case I32:
			case I64:
			case FLOAT:
			case DOUBLE:
				enterOuterAlt(_localctx, 1);
				{
				setState(400);
				constant_pool_entry();
				setState(405);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(401);
					match(COMMA);
					setState(402);
					constant_pool_entry();
					}
					}
					setState(407);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				break;
			case CLOSE_BRACE:
				enterOuterAlt(_localctx, 2);
				{
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class Constant_pool_entryContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ConstantContext constant() {
			return getRuleContext(ConstantContext.class,0);
		}
		public Constant_pool_entryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant_pool_entry; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterConstant_pool_entry(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitConstant_pool_entry(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitConstant_pool_entry(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Constant_pool_entryContext constant_pool_entry() throws RecognitionException {
		Constant_pool_entryContext _localctx = new Constant_pool_entryContext(_ctx, getState());
		enterRule(_localctx, 90, RULE_constant_pool_entry);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(411);
			type(0);
			setState(412);
			constant();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConstantContext extends ParserRuleContext {
		public TerminalNode NUMBER() { return getToken(LGIRParser.NUMBER, 0); }
		public TerminalNode STRING_LITERAL() { return getToken(LGIRParser.STRING_LITERAL, 0); }
		public ConstantContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_constant; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).enterConstant(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof LGIRListener ) ((LGIRListener)listener).exitConstant(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof LGIRVisitor ) return ((LGIRVisitor<? extends T>)visitor).visitConstant(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConstantContext constant() throws RecognitionException {
		ConstantContext _localctx = new ConstantContext(_ctx, getState());
		enterRule(_localctx, 92, RULE_constant);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(414);
			_la = _input.LA(1);
			if ( !(_la==NUMBER || _la==STRING_LITERAL) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 33:
			return type_sempred((TypeContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u0001S\u01a1\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0002\u001b\u0007\u001b"+
		"\u0002\u001c\u0007\u001c\u0002\u001d\u0007\u001d\u0002\u001e\u0007\u001e"+
		"\u0002\u001f\u0007\u001f\u0002 \u0007 \u0002!\u0007!\u0002\"\u0007\"\u0002"+
		"#\u0007#\u0002$\u0007$\u0002%\u0007%\u0002&\u0007&\u0002\'\u0007\'\u0002"+
		"(\u0007(\u0002)\u0007)\u0002*\u0007*\u0002+\u0007+\u0002,\u0007,\u0002"+
		"-\u0007-\u0002.\u0007.\u0001\u0000\u0005\u0000`\b\u0000\n\u0000\f\u0000"+
		"c\t\u0000\u0001\u0000\u0005\u0000f\b\u0000\n\u0000\f\u0000i\t\u0000\u0001"+
		"\u0000\u0005\u0000l\b\u0000\n\u0000\f\u0000o\t\u0000\u0001\u0000\u0003"+
		"\u0000r\b\u0000\u0001\u0000\u0003\u0000u\b\u0000\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0003"+
		"\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0003\u0001\u0004\u0001\u0004"+
		"\u0001\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u008e\b\u0004\u0001\u0005"+
		"\u0001\u0005\u0001\u0005\u0001\u0006\u0001\u0006\u0003\u0006\u0095\b\u0006"+
		"\u0001\u0006\u0005\u0006\u0098\b\u0006\n\u0006\f\u0006\u009b\t\u0006\u0001"+
		"\u0006\u0001\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\u0007\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b"+
		"\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\b\u0001\b\u0003\b\u00ba\b\b\u0001\t\u0001"+
		"\t\u0001\t\u0003\t\u00bf\b\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001"+
		"\t\u0003\t\u00c7\b\t\u0001\n\u0003\n\u00ca\b\n\u0001\n\u0001\n\u0001\n"+
		"\u0001\n\u0001\n\u0001\n\u0003\n\u00d2\b\n\u0001\n\u0001\n\u0001\n\u0001"+
		"\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\f\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000e\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011"+
		"\u0001\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012"+
		"\u0001\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u010e\b\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0001\u0016\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0003\u0017\u0117\b\u0017\u0001\u0017\u0001\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0018\u0001\u0018\u0003\u0018\u011f\b\u0018\u0001\u0019\u0001\u0019"+
		"\u0001\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0001\u001a"+
		"\u0001\u001a\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001b"+
		"\u0001\u001b\u0001\u001b\u0001\u001b\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0003\u001c\u0135\b\u001c\u0001\u001c\u0001\u001c\u0001\u001c\u0001\u001c"+
		"\u0001\u001c\u0001\u001c\u0001\u001d\u0001\u001d\u0001\u001d\u0001\u001d"+
		"\u0005\u001d\u0141\b\u001d\n\u001d\f\u001d\u0144\t\u001d\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0003\u001e\u0149\b\u001e\u0001\u001e\u0001\u001e\u0001"+
		"\u001e\u0001\u001e\u0001\u001e\u0005\u001e\u0150\b\u001e\n\u001e\f\u001e"+
		"\u0153\t\u001e\u0001\u001f\u0001\u001f\u0001\u001f\u0005\u001f\u0158\b"+
		"\u001f\n\u001f\f\u001f\u015b\t\u001f\u0001\u001f\u0003\u001f\u015e\b\u001f"+
		"\u0001 \u0001 \u0001 \u0001 \u0001!\u0001!\u0001!\u0003!\u0167\b!\u0001"+
		"!\u0001!\u0005!\u016b\b!\n!\f!\u016e\t!\u0001\"\u0001\"\u0001#\u0001#"+
		"\u0001$\u0001$\u0001%\u0001%\u0001%\u0001&\u0001&\u0001\'\u0001\'\u0001"+
		"\'\u0001(\u0001(\u0001)\u0001)\u0001*\u0001*\u0001*\u0001*\u0001*\u0001"+
		"*\u0001*\u0001*\u0001*\u0001+\u0001+\u0001+\u0001+\u0001+\u0001+\u0001"+
		",\u0001,\u0001,\u0005,\u0194\b,\n,\f,\u0197\t,\u0001,\u0003,\u019a\b,"+
		"\u0001-\u0001-\u0001-\u0001.\u0001.\u0001.\u0000\u0001B/\u0000\u0002\u0004"+
		"\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \""+
		"$&(*,.02468:<>@BDFHJLNPRTVXZ\\\u0000\u0005\u0001\u0000FQ\u0001\u0000\u001c"+
		"#\u0001\u0000>D\u0001\u00001;\u0002\u0000$$RR\u019f\u0000a\u0001\u0000"+
		"\u0000\u0000\u0002v\u0001\u0000\u0000\u0000\u0004{\u0001\u0000\u0000\u0000"+
		"\u0006\u0083\u0001\u0000\u0000\u0000\b\u0088\u0001\u0000\u0000\u0000\n"+
		"\u008f\u0001\u0000\u0000\u0000\f\u0092\u0001\u0000\u0000\u0000\u000e\u009e"+
		"\u0001\u0000\u0000\u0000\u0010\u00b9\u0001\u0000\u0000\u0000\u0012\u00bb"+
		"\u0001\u0000\u0000\u0000\u0014\u00c9\u0001\u0000\u0000\u0000\u0016\u00d6"+
		"\u0001\u0000\u0000\u0000\u0018\u00dc\u0001\u0000\u0000\u0000\u001a\u00e2"+
		"\u0001\u0000\u0000\u0000\u001c\u00e6\u0001\u0000\u0000\u0000\u001e\u00ea"+
		"\u0001\u0000\u0000\u0000 \u00ef\u0001\u0000\u0000\u0000\"\u00f2\u0001"+
		"\u0000\u0000\u0000$\u00f9\u0001\u0000\u0000\u0000&\u00ff\u0001\u0000\u0000"+
		"\u0000(\u0105\u0001\u0000\u0000\u0000*\u0108\u0001\u0000\u0000\u0000,"+
		"\u010a\u0001\u0000\u0000\u0000.\u0113\u0001\u0000\u0000\u00000\u011c\u0001"+
		"\u0000\u0000\u00002\u0120\u0001\u0000\u0000\u00004\u0124\u0001\u0000\u0000"+
		"\u00006\u0129\u0001\u0000\u0000\u00008\u0131\u0001\u0000\u0000\u0000:"+
		"\u013c\u0001\u0000\u0000\u0000<\u0148\u0001\u0000\u0000\u0000>\u015d\u0001"+
		"\u0000\u0000\u0000@\u015f\u0001\u0000\u0000\u0000B\u0166\u0001\u0000\u0000"+
		"\u0000D\u016f\u0001\u0000\u0000\u0000F\u0171\u0001\u0000\u0000\u0000H"+
		"\u0173\u0001\u0000\u0000\u0000J\u0175\u0001\u0000\u0000\u0000L\u0178\u0001"+
		"\u0000\u0000\u0000N\u017a\u0001\u0000\u0000\u0000P\u017d\u0001\u0000\u0000"+
		"\u0000R\u017f\u0001\u0000\u0000\u0000T\u0181\u0001\u0000\u0000\u0000V"+
		"\u018a\u0001\u0000\u0000\u0000X\u0199\u0001\u0000\u0000\u0000Z\u019b\u0001"+
		"\u0000\u0000\u0000\\\u019e\u0001\u0000\u0000\u0000^`\u0003\u0002\u0001"+
		"\u0000_^\u0001\u0000\u0000\u0000`c\u0001\u0000\u0000\u0000a_\u0001\u0000"+
		"\u0000\u0000ab\u0001\u0000\u0000\u0000bg\u0001\u0000\u0000\u0000ca\u0001"+
		"\u0000\u0000\u0000df\u0003\u0004\u0002\u0000ed\u0001\u0000\u0000\u0000"+
		"fi\u0001\u0000\u0000\u0000ge\u0001\u0000\u0000\u0000gh\u0001\u0000\u0000"+
		"\u0000hm\u0001\u0000\u0000\u0000ig\u0001\u0000\u0000\u0000jl\u0003\b\u0004"+
		"\u0000kj\u0001\u0000\u0000\u0000lo\u0001\u0000\u0000\u0000mk\u0001\u0000"+
		"\u0000\u0000mn\u0001\u0000\u0000\u0000nq\u0001\u0000\u0000\u0000om\u0001"+
		"\u0000\u0000\u0000pr\u0003\u0006\u0003\u0000qp\u0001\u0000\u0000\u0000"+
		"qr\u0001\u0000\u0000\u0000rt\u0001\u0000\u0000\u0000su\u0003\n\u0005\u0000"+
		"ts\u0001\u0000\u0000\u0000tu\u0001\u0000\u0000\u0000u\u0001\u0001\u0000"+
		"\u0000\u0000vw\u0005\u0001\u0000\u0000wx\u00050\u0000\u0000xy\u0003>\u001f"+
		"\u0000yz\u00050\u0000\u0000z\u0003\u0001\u0000\u0000\u0000{|\u0005\u0002"+
		"\u0000\u0000|}\u0003B!\u0000}~\u0005S\u0000\u0000~\u007f\u0005+\u0000"+
		"\u0000\u007f\u0080\u0003>\u001f\u0000\u0080\u0081\u0005,\u0000\u0000\u0081"+
		"\u0082\u0003\f\u0006\u0000\u0082\u0005\u0001\u0000\u0000\u0000\u0083\u0084"+
		"\u0005\u0003\u0000\u0000\u0084\u0085\u0005/\u0000\u0000\u0085\u0086\u0003"+
		"X,\u0000\u0086\u0087\u00050\u0000\u0000\u0087\u0007\u0001\u0000\u0000"+
		"\u0000\u0088\u008d\u0005S\u0000\u0000\u0089\u008a\u0005)\u0000\u0000\u008a"+
		"\u008e\u0003B!\u0000\u008b\u008c\u0005\u0004\u0000\u0000\u008c\u008e\u0003"+
		"\\.\u0000\u008d\u0089\u0001\u0000\u0000\u0000\u008d\u008b\u0001\u0000"+
		"\u0000\u0000\u008e\t\u0001\u0000\u0000\u0000\u008f\u0090\u0005\u0005\u0000"+
		"\u0000\u0090\u0091\u0003\f\u0006\u0000\u0091\u000b\u0001\u0000\u0000\u0000"+
		"\u0092\u0094\u0005/\u0000\u0000\u0093\u0095\u0003\u000e\u0007\u0000\u0094"+
		"\u0093\u0001\u0000\u0000\u0000\u0094\u0095\u0001\u0000\u0000\u0000\u0095"+
		"\u0099\u0001\u0000\u0000\u0000\u0096\u0098\u0003\u0010\b\u0000\u0097\u0096"+
		"\u0001\u0000\u0000\u0000\u0098\u009b\u0001\u0000\u0000\u0000\u0099\u0097"+
		"\u0001\u0000\u0000\u0000\u0099\u009a\u0001\u0000\u0000\u0000\u009a\u009c"+
		"\u0001\u0000\u0000\u0000\u009b\u0099\u0001\u0000\u0000\u0000\u009c\u009d"+
		"\u00050\u0000\u0000\u009d\r\u0001\u0000\u0000\u0000\u009e\u009f\u0005"+
		"\u0006\u0000\u0000\u009f\u00a0\u0005/\u0000\u0000\u00a0\u00a1\u0003>\u001f"+
		"\u0000\u00a1\u00a2\u00050\u0000\u0000\u00a2\u000f\u0001\u0000\u0000\u0000"+
		"\u00a3\u00ba\u0003\u0012\t\u0000\u00a4\u00ba\u0003\u0014\n\u0000\u00a5"+
		"\u00ba\u0003\u0016\u000b\u0000\u00a6\u00ba\u0003\u0018\f\u0000\u00a7\u00ba"+
		"\u0003\u001a\r\u0000\u00a8\u00ba\u0003\u001c\u000e\u0000\u00a9\u00ba\u0003"+
		"\u001e\u000f\u0000\u00aa\u00ba\u0003 \u0010\u0000\u00ab\u00ba\u0003\""+
		"\u0011\u0000\u00ac\u00ba\u0003$\u0012\u0000\u00ad\u00ba\u0003&\u0013\u0000"+
		"\u00ae\u00ba\u0003(\u0014\u0000\u00af\u00ba\u0003*\u0015\u0000\u00b0\u00ba"+
		"\u0003,\u0016\u0000\u00b1\u00ba\u0003.\u0017\u0000\u00b2\u00ba\u00030"+
		"\u0018\u0000\u00b3\u00ba\u00032\u0019\u0000\u00b4\u00ba\u00034\u001a\u0000"+
		"\u00b5\u00ba\u00036\u001b\u0000\u00b6\u00ba\u00038\u001c\u0000\u00b7\u00ba"+
		"\u0003:\u001d\u0000\u00b8\u00ba\u0003<\u001e\u0000\u00b9\u00a3\u0001\u0000"+
		"\u0000\u0000\u00b9\u00a4\u0001\u0000\u0000\u0000\u00b9\u00a5\u0001\u0000"+
		"\u0000\u0000\u00b9\u00a6\u0001\u0000\u0000\u0000\u00b9\u00a7\u0001\u0000"+
		"\u0000\u0000\u00b9\u00a8\u0001\u0000\u0000\u0000\u00b9\u00a9\u0001\u0000"+
		"\u0000\u0000\u00b9\u00aa\u0001\u0000\u0000\u0000\u00b9\u00ab\u0001\u0000"+
		"\u0000\u0000\u00b9\u00ac\u0001\u0000\u0000\u0000\u00b9\u00ad\u0001\u0000"+
		"\u0000\u0000\u00b9\u00ae\u0001\u0000\u0000\u0000\u00b9\u00af\u0001\u0000"+
		"\u0000\u0000\u00b9\u00b0\u0001\u0000\u0000\u0000\u00b9\u00b1\u0001\u0000"+
		"\u0000\u0000\u00b9\u00b2\u0001\u0000\u0000\u0000\u00b9\u00b3\u0001\u0000"+
		"\u0000\u0000\u00b9\u00b4\u0001\u0000\u0000\u0000\u00b9\u00b5\u0001\u0000"+
		"\u0000\u0000\u00b9\u00b6\u0001\u0000\u0000\u0000\u00b9\u00b7\u0001\u0000"+
		"\u0000\u0000\u00b9\u00b8\u0001\u0000\u0000\u0000\u00ba\u0011\u0001\u0000"+
		"\u0000\u0000\u00bb\u00bc\u0003J%\u0000\u00bc\u00be\u0005\u0004\u0000\u0000"+
		"\u00bd\u00bf\u0005\u0007\u0000\u0000\u00be\u00bd\u0001\u0000\u0000\u0000"+
		"\u00be\u00bf\u0001\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000\u0000"+
		"\u00c0\u00c1\u0005\b\u0000\u0000\u00c1\u00c2\u0003B!\u0000\u00c2\u00c3"+
		"\u0003L&\u0000\u00c3\u00c6\u0003H$\u0000\u00c4\u00c5\u0005(\u0000\u0000"+
		"\u00c5\u00c7\u0003H$\u0000\u00c6\u00c4\u0001\u0000\u0000\u0000\u00c6\u00c7"+
		"\u0001\u0000\u0000\u0000\u00c7\u0013\u0001\u0000\u0000\u0000\u00c8\u00ca"+
		"\u0005\u0007\u0000\u0000\u00c9\u00c8\u0001\u0000\u0000\u0000\u00c9\u00ca"+
		"\u0001\u0000\u0000\u0000\u00ca\u00cb\u0001\u0000\u0000\u0000\u00cb\u00cc"+
		"\u0005\t\u0000\u0000\u00cc\u00cd\u0003B!\u0000\u00cd\u00ce\u0003L&\u0000"+
		"\u00ce\u00d1\u0003H$\u0000\u00cf\u00d0\u0005(\u0000\u0000\u00d0\u00d2"+
		"\u0003H$\u0000\u00d1\u00cf\u0001\u0000\u0000\u0000\u00d1\u00d2\u0001\u0000"+
		"\u0000\u0000\u00d2\u00d3\u0001\u0000\u0000\u0000\u00d3\u00d4\u0005(\u0000"+
		"\u0000\u00d4\u00d5\u0003N\'\u0000\u00d5\u0015\u0001\u0000\u0000\u0000"+
		"\u00d6\u00d7\u0003J%\u0000\u00d7\u00d8\u0005\u0004\u0000\u0000\u00d8\u00d9"+
		"\u0005\n\u0000\u0000\u00d9\u00da\u0003B!\u0000\u00da\u00db\u0003H$\u0000"+
		"\u00db\u0017\u0001\u0000\u0000\u0000\u00dc\u00dd\u0003J%\u0000\u00dd\u00de"+
		"\u0005\u0004\u0000\u0000\u00de\u00df\u0005\u000b\u0000\u0000\u00df\u00e0"+
		"\u0003B!\u0000\u00e0\u00e1\u0003H$\u0000\u00e1\u0019\u0001\u0000\u0000"+
		"\u0000\u00e2\u00e3\u0005\f\u0000\u0000\u00e3\u00e4\u0003B!\u0000\u00e4"+
		"\u00e5\u0003H$\u0000\u00e5\u001b\u0001\u0000\u0000\u0000\u00e6\u00e7\u0005"+
		"\r\u0000\u0000\u00e7\u00e8\u0003B!\u0000\u00e8\u00e9\u0003H$\u0000\u00e9"+
		"\u001d\u0001\u0000\u0000\u0000\u00ea\u00eb\u0003J%\u0000\u00eb\u00ec\u0005"+
		"\u0004\u0000\u0000\u00ec\u00ed\u0005\u000e\u0000\u0000\u00ed\u00ee\u0003"+
		"H$\u0000\u00ee\u001f\u0001\u0000\u0000\u0000\u00ef\u00f0\u0005\u000f\u0000"+
		"\u0000\u00f0\u00f1\u0003H$\u0000\u00f1!\u0001\u0000\u0000\u0000\u00f2"+
		"\u00f3\u0003J%\u0000\u00f3\u00f4\u0005\u0004\u0000\u0000\u00f4\u00f5\u0005"+
		"\u0010\u0000\u0000\u00f5\u00f6\u0003H$\u0000\u00f6\u00f7\u0005(\u0000"+
		"\u0000\u00f7\u00f8\u0003H$\u0000\u00f8#\u0001\u0000\u0000\u0000\u00f9"+
		"\u00fa\u0003J%\u0000\u00fa\u00fb\u0005\u0004\u0000\u0000\u00fb\u00fc\u0005"+
		"\u0011\u0000\u0000\u00fc\u00fd\u0003B!\u0000\u00fd\u00fe\u0003H$\u0000"+
		"\u00fe%\u0001\u0000\u0000\u0000\u00ff\u0100\u0005\u0012\u0000\u0000\u0100"+
		"\u0101\u0003B!\u0000\u0101\u0102\u0003H$\u0000\u0102\u0103\u0005(\u0000"+
		"\u0000\u0103\u0104\u0003H$\u0000\u0104\'\u0001\u0000\u0000\u0000\u0105"+
		"\u0106\u0005\u0013\u0000\u0000\u0106\u0107\u0003N\'\u0000\u0107)\u0001"+
		"\u0000\u0000\u0000\u0108\u0109\u0005\u0014\u0000\u0000\u0109+\u0001\u0000"+
		"\u0000\u0000\u010a\u010b\u0003J%\u0000\u010b\u010d\u0005\u0004\u0000\u0000"+
		"\u010c\u010e\u0005\u0007\u0000\u0000\u010d\u010c\u0001\u0000\u0000\u0000"+
		"\u010d\u010e\u0001\u0000\u0000\u0000\u010e\u010f\u0001\u0000\u0000\u0000"+
		"\u010f\u0110\u0005\u0015\u0000\u0000\u0110\u0111\u0003B!\u0000\u0111\u0112"+
		"\u0003H$\u0000\u0112-\u0001\u0000\u0000\u0000\u0113\u0114\u0003J%\u0000"+
		"\u0114\u0116\u0005\u0004\u0000\u0000\u0115\u0117\u0005\u0007\u0000\u0000"+
		"\u0116\u0115\u0001\u0000\u0000\u0000\u0116\u0117\u0001\u0000\u0000\u0000"+
		"\u0117\u0118\u0001\u0000\u0000\u0000\u0118\u0119\u0005=\u0000\u0000\u0119"+
		"\u011a\u0003B!\u0000\u011a\u011b\u0003H$\u0000\u011b/\u0001\u0000\u0000"+
		"\u0000\u011c\u011e\u0005\u0016\u0000\u0000\u011d\u011f\u0003H$\u0000\u011e"+
		"\u011d\u0001\u0000\u0000\u0000\u011e\u011f\u0001\u0000\u0000\u0000\u011f"+
		"1\u0001\u0000\u0000\u0000\u0120\u0121\u0003J%\u0000\u0121\u0122\u0005"+
		"\u0004\u0000\u0000\u0122\u0123\u0003H$\u0000\u01233\u0001\u0000\u0000"+
		"\u0000\u0124\u0125\u0003J%\u0000\u0125\u0126\u0005\u0004\u0000\u0000\u0126"+
		"\u0127\u0005\u0017\u0000\u0000\u0127\u0128\u0003H$\u0000\u01285\u0001"+
		"\u0000\u0000\u0000\u0129\u012a\u0003J%\u0000\u012a\u012b\u0005\u0004\u0000"+
		"\u0000\u012b\u012c\u0003P(\u0000\u012c\u012d\u0003B!\u0000\u012d\u012e"+
		"\u0003H$\u0000\u012e\u012f\u0005\u0018\u0000\u0000\u012f\u0130\u0003B"+
		"!\u0000\u01307\u0001\u0000\u0000\u0000\u0131\u0132\u0003J%\u0000\u0132"+
		"\u0134\u0005\u0004\u0000\u0000\u0133\u0135\u0005\u0007\u0000\u0000\u0134"+
		"\u0133\u0001\u0000\u0000\u0000\u0134\u0135\u0001\u0000\u0000\u0000\u0135"+
		"\u0136\u0001\u0000\u0000\u0000\u0136\u0137\u0003R)\u0000\u0137\u0138\u0003"+
		"B!\u0000\u0138\u0139\u0003H$\u0000\u0139\u013a\u0005(\u0000\u0000\u013a"+
		"\u013b\u0003H$\u0000\u013b9\u0001\u0000\u0000\u0000\u013c\u013d\u0005"+
		"\u0019\u0000\u0000\u013d\u0142\u0005R\u0000\u0000\u013e\u013f\u0005(\u0000"+
		"\u0000\u013f\u0141\u0003T*\u0000\u0140\u013e\u0001\u0000\u0000\u0000\u0141"+
		"\u0144\u0001\u0000\u0000\u0000\u0142\u0140\u0001\u0000\u0000\u0000\u0142"+
		"\u0143\u0001\u0000\u0000\u0000\u0143;\u0001\u0000\u0000\u0000\u0144\u0142"+
		"\u0001\u0000\u0000\u0000\u0145\u0146\u0003J%\u0000\u0146\u0147\u0005\u0004"+
		"\u0000\u0000\u0147\u0149\u0001\u0000\u0000\u0000\u0148\u0145\u0001\u0000"+
		"\u0000\u0000\u0148\u0149\u0001\u0000\u0000\u0000\u0149\u014a\u0001\u0000"+
		"\u0000\u0000\u014a\u014b\u0005\u001a\u0000\u0000\u014b\u014c\u0003B!\u0000"+
		"\u014c\u0151\u0003H$\u0000\u014d\u014e\u0005(\u0000\u0000\u014e\u0150"+
		"\u0003V+\u0000\u014f\u014d\u0001\u0000\u0000\u0000\u0150\u0153\u0001\u0000"+
		"\u0000\u0000\u0151\u014f\u0001\u0000\u0000\u0000\u0151\u0152\u0001\u0000"+
		"\u0000\u0000\u0152=\u0001\u0000\u0000\u0000\u0153\u0151\u0001\u0000\u0000"+
		"\u0000\u0154\u0159\u0003@ \u0000\u0155\u0156\u0005(\u0000\u0000\u0156"+
		"\u0158\u0003@ \u0000\u0157\u0155\u0001\u0000\u0000\u0000\u0158\u015b\u0001"+
		"\u0000\u0000\u0000\u0159\u0157\u0001\u0000\u0000\u0000\u0159\u015a\u0001"+
		"\u0000\u0000\u0000\u015a\u015e\u0001\u0000\u0000\u0000\u015b\u0159\u0001"+
		"\u0000\u0000\u0000\u015c\u015e\u0001\u0000\u0000\u0000\u015d\u0154\u0001"+
		"\u0000\u0000\u0000\u015d\u015c\u0001\u0000\u0000\u0000\u015e?\u0001\u0000"+
		"\u0000\u0000\u015f\u0160\u0005S\u0000\u0000\u0160\u0161\u0005)\u0000\u0000"+
		"\u0161\u0162\u0003B!\u0000\u0162A\u0001\u0000\u0000\u0000\u0163\u0164"+
		"\u0006!\uffff\uffff\u0000\u0164\u0167\u0003D\"\u0000\u0165\u0167\u0003"+
		"F#\u0000\u0166\u0163\u0001\u0000\u0000\u0000\u0166\u0165\u0001\u0000\u0000"+
		"\u0000\u0167\u016c\u0001\u0000\u0000\u0000\u0168\u0169\n\u0002\u0000\u0000"+
		"\u0169\u016b\u0005\u001b\u0000\u0000\u016a\u0168\u0001\u0000\u0000\u0000"+
		"\u016b\u016e\u0001\u0000\u0000\u0000\u016c\u016a\u0001\u0000\u0000\u0000"+
		"\u016c\u016d\u0001\u0000\u0000\u0000\u016dC\u0001\u0000\u0000\u0000\u016e"+
		"\u016c\u0001\u0000\u0000\u0000\u016f\u0170\u0007\u0000\u0000\u0000\u0170"+
		"E\u0001\u0000\u0000\u0000\u0171\u0172\u0005E\u0000\u0000\u0172G\u0001"+
		"\u0000\u0000\u0000\u0173\u0174\u0003J%\u0000\u0174I\u0001\u0000\u0000"+
		"\u0000\u0175\u0176\u0005\'\u0000\u0000\u0176\u0177\u0005S\u0000\u0000"+
		"\u0177K\u0001\u0000\u0000\u0000\u0178\u0179\u0007\u0001\u0000\u0000\u0179"+
		"M\u0001\u0000\u0000\u0000\u017a\u017b\u0005&\u0000\u0000\u017b\u017c\u0005"+
		"S\u0000\u0000\u017cO\u0001\u0000\u0000\u0000\u017d\u017e\u0007\u0002\u0000"+
		"\u0000\u017eQ\u0001\u0000\u0000\u0000\u017f\u0180\u0007\u0003\u0000\u0000"+
		"\u0180S\u0001\u0000\u0000\u0000\u0181\u0182\u0005-\u0000\u0000\u0182\u0183"+
		"\u0003B!\u0000\u0183\u0184\u0005(\u0000\u0000\u0184\u0185\u0003H$\u0000"+
		"\u0185\u0186\u0005(\u0000\u0000\u0186\u0187\u0005S\u0000\u0000\u0187\u0188"+
		"\u0001\u0000\u0000\u0000\u0188\u0189\u0005.\u0000\u0000\u0189U\u0001\u0000"+
		"\u0000\u0000\u018a\u018b\u0005-\u0000\u0000\u018b\u018c\u0003B!\u0000"+
		"\u018c\u018d\u0005(\u0000\u0000\u018d\u018e\u0003H$\u0000\u018e\u018f"+
		"\u0005.\u0000\u0000\u018fW\u0001\u0000\u0000\u0000\u0190\u0195\u0003Z"+
		"-\u0000\u0191\u0192\u0005(\u0000\u0000\u0192\u0194\u0003Z-\u0000\u0193"+
		"\u0191\u0001\u0000\u0000\u0000\u0194\u0197\u0001\u0000\u0000\u0000\u0195"+
		"\u0193\u0001\u0000\u0000\u0000\u0195\u0196\u0001\u0000\u0000\u0000\u0196"+
		"\u019a\u0001\u0000\u0000\u0000\u0197\u0195\u0001\u0000\u0000\u0000\u0198"+
		"\u019a\u0001\u0000\u0000\u0000\u0199\u0190\u0001\u0000\u0000\u0000\u0199"+
		"\u0198\u0001\u0000\u0000\u0000\u019aY\u0001\u0000\u0000\u0000\u019b\u019c"+
		"\u0003B!\u0000\u019c\u019d\u0003\\.\u0000\u019d[\u0001\u0000\u0000\u0000"+
		"\u019e\u019f\u0007\u0004\u0000\u0000\u019f]\u0001\u0000\u0000\u0000\u001a"+
		"agmqt\u008d\u0094\u0099\u00b9\u00be\u00c6\u00c9\u00d1\u010d\u0116\u011e"+
		"\u0134\u0142\u0148\u0151\u0159\u015d\u0166\u016c\u0195\u0199";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}