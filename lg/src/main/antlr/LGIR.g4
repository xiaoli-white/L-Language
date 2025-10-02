grammar LGIR;

program: structure* function* global_data* constant_pool? init?;

structure: 'structure' CLOSE_BRACE fields CLOSE_BRACE;
function: 'function' type IDENTIFIER OPEN_PAREN fields CLOSE_PAREN function_body;
constant_pool: 'constant_pool' OPEN_BRACE constant_pool_entries CLOSE_BRACE;
global_data: IDENTIFIER ((COLON type) | ('=' constant));
init: 'init' function_body;

function_body: OPEN_BRACE locals_? instruction* CLOSE_BRACE;

locals_: 'locals' OPEN_BRACE fields CLOSE_BRACE;
instruction: cmp | conditional_jump | increase | decrease | atomic_increase | atomic_decrease
    | malloc | free | realloc | get | set | goto | nop | negate | not | return | set_virtual_register
    | stack_alloc | type_cast | calculate | asm | invoke;
cmp: virtual_register '=' 'atomic_'? 'cmp' type condition operand (COMMA operand)?;
conditional_jump: 'atomic_'? 'conditional_jump' type condition operand (COMMA operand)? COMMA label;
increase: virtual_register '=' 'increase' type operand;
decrease: virtual_register '=' 'decrease' type operand;
atomic_increase: 'atomic_increase' type operand;
atomic_decrease: 'atomic_decrease' type operand;
malloc: virtual_register '=' 'malloc' operand;
free: 'free' operand;
realloc: virtual_register '=' 'realloc' operand COMMA operand;
get: virtual_register '=' 'get' type operand;
set: 'set' type operand COMMA operand;
goto: 'goto' label;
nop: 'nop';
negate: virtual_register '=' 'atomic_'? 'negate' type operand;
not: virtual_register '=' 'atomic_'? 'not' type operand;
return: 'return' operand?;
set_virtual_register: virtual_register '=' operand;
stack_alloc: virtual_register '=' 'stack_alloc' operand;
type_cast: virtual_register '=' type_cast_kind type operand 'to' type;
calculate: virtual_register '=' 'atomic_'? calculation_operator type operand COMMA operand;
asm: 'asm' STRING_LITERAL (COMMA asm_resource)*;
invoke: (virtual_register '=')? 'invoke' type operand (COMMA argument)*;

fields: field (COMMA field)* | ;
field: IDENTIFIER COLON type;
type : base_type | type '*' | void_type;
base_type: U1 | U8 | U16 | U32 | U64 | I1 | I8 | I16 | I32 | I64 | FLOAT | DOUBLE;
void_type: VOID;

operand: virtual_register;
virtual_register: PERCENT IDENTIFIER;
condition: 'e' | 'ne' | 'l' | 'le' | 'g' | 'ge' | 'if_true' | 'if_false';
label: SHARP IDENTIFIER;
type_cast_kind: ZEXT | SEXT | TRUNC | ITOF | FTOI | FEXT | FTRUNC;
calculation_operator: ADD | SUB | MUL | DIV | MOD | AND | OR | XOR | SHL | SHR | USHR;
asm_resource: OPEN_BRACKET (type COMMA operand COMMA IDENTIFIER) CLOSE_BRACKET;
argument: OPEN_BRACKET type COMMA operand CLOSE_BRACKET;
constant_pool_entries: constant_pool_entry (COMMA constant_pool_entry)* | ;
constant_pool_entry: type constant;

constant: NUMBER | STRING_LITERAL;


NUMBER : [0-9]+ ('.' [0-9]+)?;
WS : [ \t\r\n]+ -> skip;

SHARP: '#';
PERCENT: '%';
COMMA: ',';
COLON: ':';
SEMICOLON: ';';
OPEN_PAREN: '(';
CLOSE_PAREN: ')';
OPEN_BRACKET: '[';
CLOSE_BRACKET: ']';
OPEN_BRACE: '{';
CLOSE_BRACE: '}';

ADD: 'add';
SUB: 'sub';
MUL: 'mul';
DIV: 'div';
MOD: 'mod';
AND: 'and';
OR: 'or';
XOR: 'xor';
SHL: 'shl';
SHR: 'shr';
USHR: 'ushr';
NEG: 'neg';
NOT: 'not';


ZEXT: 'zext';
SEXT: 'sext';
TRUNC: 'trunc';
ITOF: 'itof';
FTOI: 'ftoi';
FEXT: 'fext';
FTRUNC: 'ftrunc';

VOID: 'void';
U1: 'u1';
U8: 'u8';
U16: 'u16';
U32: 'u32';
U64: 'u64';
I1: 'i1';
I8: 'i8';
I16: 'i16';
I32: 'i32';
I64: 'i64';
FLOAT: 'float';
DOUBLE: 'double';

STRING_LITERAL: '"' (~["\\] | '\\' .)* '"';
IDENTIFIER: [\p{L}_] [\p{L}0-9_]* | STRING_LITERAL;