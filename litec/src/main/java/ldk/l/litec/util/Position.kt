package ldk.l.litec.util

data class Position(
    val line: Int,
    val column: Int
) {
    override fun toString() = "$line:$column"
}