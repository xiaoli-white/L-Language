package ldk.l.litec.util.error

enum class ErrorType(
    val code: String,          // 错误代码（如E0001）
    val message: String,       // 错误简短描述
    val template: String       // 详细信息模板
) {
    ILLEGAL_CHARACTER(
        "E0001",
        "输入非法",
        "非法字符 '%s'"
    ),
    UNDEFINED_SYMBOL(
        "E0002",
        "use of undeclared symbol",
        "symbol '%s' has not been declared"
    ),
    TYPE_MISMATCH(
        "E0003",
        "mismatched types",
        "expected type '%s' but found '%s'"
    ),
    UNEXPECTED_TOKEN(
        "E0004",
        "unexpected token",
        "expected token '%s' but found '%s'"
    );
}