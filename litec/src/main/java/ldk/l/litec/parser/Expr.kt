package ldk.l.litec.parser

import ldk.l.litec.`object`.Object

sealed class Expr

data class LiteralExpr(
    val value: Object
) : Expr()

data class BinaryExpr(
    val left: Expr,
    val right: Expr,
    val op: String,
) : Expr()

data class IdentifierExpr(
    val name: Expr
) : Expr()

data class UnaryExpr(
    val op: String,
    val right: Expr
) : Expr()

data class LogicalExpr(
    val left: Expr,
    val right: Expr,
    val op: String
) : Expr()