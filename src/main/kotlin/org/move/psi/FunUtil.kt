package org.move.psi

import com.intellij.codeInsight.TargetElementUtil
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.jetbrains.rd.util.addUnique
import org.move.ide.annotator.GLOBAL_STORAGE_ACCESS_FUNCTIONS
import org.move.ide.formatter.impl.isDeclarationItem
import org.move.ide.formatter.impl.isStmtOrExpr
import org.move.ide.presentation.fullname
import org.move.ide.utils.callInfo
import org.move.lang.core.psi.*
import org.move.lang.core.psi.ext.*
import org.move.lang.core.types.infer.*
import org.move.lang.core.types.ty.TyBool
import org.move.lang.core.types.ty.TyFunction
import org.move.lang.core.types.ty.TyUnknown

class FunUtil {
    companion object {
        /**
         * 通过解析函数中所有语句来识别资源操作并将操作顺序存储
         * @param resList - list of resource
         * @param name - resolve target function
         * @
         */
        fun findFunction(
            resList: List<String>,
            targetFun: MvFunction,
            currentResolveResult: MutableMap<String, List<Ops>>,
        ): MutableList<String> {
// 记录函数操作序列的
            var ops: MutableList<Ops> = mutableListOf()
//            解析所有语句
//            语句分4种1.let 2 调用 3赋值语句 4返回
            val stmtlist = targetFun.codeBlock!!.stmtList
            val childrenArray = targetFun.codeBlock!!.children
            for (item in childrenArray.indices) {
                if (childrenArray[item] is MvStmt) {
                    when (childrenArray[item]) {
                        is MvLetStmt -> {
                            val let = childrenArray[item] as MvLetStmt
                            val initializer = let.initializer
                            val expr = initializer?.expr
                            var funcalllist = LinkedHashSet<Ops>()
                            if (expr != null) {
                                // 获得所有函数调用，返回函数名
                                inferExpr(expr, funcalllist)
                            }
                            if (funcalllist.size > 0) {
                                funcalllist.forEach { it ->
                                    println("funcalllist item :${it.toString()}")
                                    if (currentResolveResult.containsKey(it.op))
                                        ops.addAll(currentResolveResult.get(it.op)!!.toMutableList().filter {
                                            !(it.op.contains("builtins") && (it.op.contains("freeze")))
                                        })

                                }

                            }
                        }

                        is MvExprStmt -> {
                            var funcalllist = LinkedHashSet<Ops>()
                            val expr = (childrenArray[item] as MvExprStmt).expr
                            // 获得所有函数调用，返回函数名
                            inferExpr(expr, funcalllist)
                            if (funcalllist.size > 0) {
                                funcalllist.forEach { it ->
                                    if (currentResolveResult.containsKey(it.op))
                                        ops.addAll(currentResolveResult.get(it.op)!!.toMutableList().filter {
                                            !(it.op.contains("builtins") && (it.op.contains("freeze")))
                                        })

                                }

                            }

                        }

                        else -> {}
                    }
                } else {

                }
            }
            // 遍历所有语句后将语句添加至现存函数操作列表中
            currentResolveResult.put(targetFun.nameElement?.text!!, ops?.toList())

            val ac = targetFun.acquiresType?.pathTypeList
            return mutableListOf()
        }

        fun getFunReference(expr: MvExpr) {
            // var exprTy = when (expr) {
            //     is MvRefExpr -> inferRefExprTy(expr, ctx)
            //     is MvBorrowExpr -> inferBorrowExprTy(expr, ctx)
            //     is MvCallExpr -> {
            //         val funcTy = inferCallExprTy(expr, ctx) as? TyFunction
            //         if (funcTy == null || !funcTy.solvable) {
            //             TyUnknown
            //         } else {
            //             funcTy.retType
            //         }
            //     }
            //     is MvDotExpr -> inferDotExprTy(expr, ctx)
            //     is MvStructLitExpr -> inferStructLitExpr(expr, ctx)
            //     is MvDerefExpr -> inferDerefExprTy(expr, ctx)
            //     is MvLitExpr -> inferLitExprTy(expr, ctx)
            //     is MvTupleLitExpr -> inferTupleLitExprTy(expr, ctx)
            //
            //     is MvMoveExpr -> expr.expr?.let { inferExprTy(it, ctx) } ?: TyUnknown
            //     is MvCopyExpr -> expr.expr?.let { inferExprTy(it, ctx) } ?: TyUnknown
            //
            //     is MvCastExpr -> inferMvTypeTy(expr.type, ctx.msl)
            //     is MvParensExpr -> expr.expr?.let { inferExprTy(it, ctx) } ?: TyUnknown
            //
            //     is MvPlusExpr -> inferBinaryExprTy(expr.exprList, ctx)
            //     is MvMinusExpr -> inferBinaryExprTy(expr.exprList, ctx)
            //     is MvMulExpr -> inferBinaryExprTy(expr.exprList, ctx)
            //     is MvDivExpr -> inferBinaryExprTy(expr.exprList, ctx)
            //     is MvModExpr -> inferBinaryExprTy(expr.exprList, ctx)
            //
            //     is MvBangExpr -> TyBool
            //     is MvLessExpr -> TyBool
            //     is MvLessEqualsExpr -> TyBool
            //     is MvGreaterExpr -> TyBool
            //     is MvGreaterEqualsExpr -> TyBool
            //     is MvAndExpr -> TyBool
            //     is MvOrExpr -> TyBool
            //
            //     is MvIfExpr -> inferIfExprTy(expr, ctx)
            //
            //     else -> TyUnknown
            // }
            // when(expr){
            //    is MvAssignmentExpr->{}
            //    is MvSpecVisRestrictedExpr->{}
            //     is  MvRangeExpr->{}
            //     is  (MvForallQuantExpr | MvExistsQuantExpr | MvChooseQuantExpr)->{}
            //     is  MvImplyOperatorsExpr_items->{}
            //     is  MvOrExpr->{}
            //     is  MvAndExpr->{}
            //     is  MvLogicalEqExprItem->{}
            //     is  MvBitOrExpr->{}
            //     is  MvBitXorExpr->{}
            //     is  MvBitAndExpr->{}
            //     is  (MvLeftShiftExpr | MvRightShiftExpr)->{}
            //     is  MvAddExprItem->{}
            //     is  MvMulExprItem->{}
            //     is  MvControlFlowExpr->{}
            //     is  MvCastExpr->{}
            //     is MvUnaryExpr->{}
            //     is  MvBorrowExpr->{}
            //     is  MvAtomExpr->{}
            // }

        }
    }
}

/**
 * Stores the text representation and ranges for parameters
 */
class TypeParamsDescription(
    val presentText: String,
    private val ranges: List<TextRange>,
) {
    fun getRange(index: Int): TextRange =
        if (index !in ranges.indices) TextRange.EMPTY_RANGE else ranges[index]
}

/**
 * Calculates the text representation and ranges for parameters
 */
private fun typeParamsDescription(params: List<MvTypeParameter>): TypeParamsDescription {

    val parts = params.map {
        val name = it.name ?: "_"
        val bound = it.typeParamBound?.text ?: ""
        name + bound
    }
    val presentText = if (parts.isEmpty()) "<no arguments>" else parts.joinToString(", ")
    return TypeParamsDescription(
        presentText,
        parts.indices.map { parts.calculateRange(it) }
    )
}

private fun List<String>.calculateRange(index: Int): TextRange {
    val start = this.take(index).sumOf({ it.length + 2 }) // plus ", "
    return TextRange(start, start + this[index].length)
}
