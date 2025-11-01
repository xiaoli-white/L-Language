grammar LGIR;

program: structure* function* global_data* constant_pool?;

structure: 'structure' CLOSE_BRACE fields CLOSE_BRACE;
function: 'function' type IDENTIFIER OPEN_PAREN fields CLOSE_PAREN function_body;
constant_pool: 'constant_pool' OPEN_BRACE constant_pool_entries CLOSE_BRACE;
global_data: IDENTIFIER ((COLON type) | ('=' constant));

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
base_type: 'u1' | 'u8' | 'u16' | 'u32' | 'u64' | 'i1' | 'i8' | 'i16' | 'i32' | 'i64' | 'float' | 'double';
void_type: 'void';

operand: virtual_register;
virtual_register: PERCENT IDENTIFIER;
condition: 'e' | 'ne' | 'l' | 'le' | 'g' | 'ge' | 'if_true' | 'if_false';
label: SHARP IDENTIFIER;
type_cast_kind: 'zext' | 'sext' | 'trunc' | 'itof' | 'ftoi' | 'fext' | 'ftrunc';
calculation_operator: 'add' | 'sub' | 'mul' | 'div' | 'mod' | 'and' | 'or' | 'xor' | 'shl' | 'shr' | 'ushr';
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


STRING_LITERAL: '"' (~["\\] | '\\' .)* '"';
IDENTIFIER: [\p{L}_] [\p{L}0-9_]* | STRING_LITERAL;