package org.move.psi
//
import org.move.lang.core.psi.*
import org.move.lang.core.psi.ext.*
import org.move.lang.core.types.ty.TyInfer
import org.move.lang.core.types.ty.TyTypeParameter
import org.move.lang.moveProject

fun inferExpr(expr: MvExpr, fp: LinkedHashSet<Ops>) {

    when (expr) {
        is MvRefExpr -> {}
        is MvBorrowExpr -> inferBorrowExpr(expr, fp)
        is MvCallExpr -> inferCallExpr(expr, fp)


        is MvDotExpr -> expr.expr?.let { inferDotExpr(it, fp) }
        is MvStructLitExpr -> inferStructLitExpr(expr, fp)
        is MvDerefExpr -> inferDerefExpr(expr, fp)
        is MvLitExpr -> {}
        is MvTupleLitExpr -> inferTupleLitExpr(expr, fp)

        is MvMoveExpr -> expr.expr?.let { inferExpr(it, fp) }
        is MvCopyExpr -> expr.expr?.let { inferExpr(it, fp) }

        is MvCastExpr -> {}
        is MvParensExpr -> expr.expr?.let { inferExpr(it, fp) }

        is MvPlusExpr -> inferBinaryExpr(expr.exprList, fp)
        is MvMinusExpr -> inferBinaryExpr(expr.exprList, fp)
        is MvMulExpr -> inferBinaryExpr(expr.exprList, fp)
        is MvDivExpr -> inferBinaryExpr(expr.exprList, fp)
        is MvModExpr -> inferBinaryExpr(expr.exprList, fp)

        is MvBangExpr -> inferBangExpr(expr.expr, fp)
        is MvLessExpr -> inferLessExpr(expr.exprList, fp)
        is MvLessEqualsExpr -> inferLessEqualsExpr(expr.exprList, fp)
        is MvGreaterExpr -> inferGreaterExpr(expr.exprList, fp)
        is MvGreaterEqualsExpr -> inferGreaterEqualsExpr(expr.exprList, fp)
        is MvAndExpr -> inferAndExpr(expr.exprList, fp)
        is MvOrExpr -> inferOrExpr(expr.exprList, fp)
        is MvIfExpr -> inferIfExpr(expr, fp)

        else -> {

        }
    }


}

fun inferOrExpr(exprList: List<MvExpr>, fp: LinkedHashSet<Ops>) {
    exprList.forEach {
        inferExpr(it, fp)
    }

}

fun inferAndExpr(exprList: List<MvExpr>, fp: LinkedHashSet<Ops>) {
    exprList.forEach {
        inferExpr(it, fp)
    }
}

fun inferGreaterEqualsExpr(exprList: List<MvExpr>, fp: LinkedHashSet<Ops>) {
    exprList.forEach {
        inferExpr(it, fp)
    }
}

fun inferGreaterExpr(exprList: List<MvExpr>, fp: LinkedHashSet<Ops>) {
    exprList.forEach {
        inferExpr(it, fp)
    }
}

fun inferLessEqualsExpr(exprList: List<MvExpr>, fp: LinkedHashSet<Ops>) {
    exprList.forEach {
        inferExpr(it, fp)
    }
}

fun inferLessExpr(exprList: List<MvExpr>, fp: LinkedHashSet<Ops>) {
    exprList.forEach {
        inferExpr(it, fp)
    }
}

fun inferBangExpr(expr: MvExpr?, fp: LinkedHashSet<Ops>) {
    if (expr != null) {
        inferExpr(expr, fp)
    }
}

// private fun inferRefExprTy(refExpr: MvRefExpr, ctx: InferenceContext): Ty {
//     val binding =
//         refExpr.path.reference?.resolve() as? MvBindingPat ?: return TyUnknown
//     return binding.cachedTy(ctx)
// }

private fun inferBorrowExpr(borrowExpr: MvBorrowExpr, fq: LinkedHashSet<Ops>) {
    val innerExpr = borrowExpr.expr
    inferExpr(innerExpr!!, fq)
}

fun inferCallExpr(callExpr: MvCallExpr, fq: LinkedHashSet<Ops>) {
    // val existingTy = ctx.callExprTypes[callExpr]
    // if (existingTy != null) {
    //     return existingTy
    // }

    val path = callExpr.path
    val funcItem = path.reference?.resolve() as? MvFunctionLike

    // val funcTy = instantiateItemTy(funcItem, ctx.msl) as? TyFunction ?: return TyUnknown

    // val inference = InferenceContext(ctx.msl)
    // find all types passed as explicit type parameters, create constraints with those
    // if (path.typeArguments.isNotEmpty()) {
    //     if (path.typeArguments.size != funcTy.typeVars.size) return TyUnknown
    //     for ((typeVar, typeArgument) in funcTy.typeVars.zip(path.typeArguments)) {
    //         val passedTy = inferMvTypeTy(typeArgument.type, ctx.msl)
    //         inference.registerConstraint(Constraint.Equate(typeVar, passedTy))
    //     }
    // }
    // find all types of passed expressions, create constraints with those
    if (callExpr.arguments.isNotEmpty()) {
        callExpr.arguments.forEach {
            inferExpr(it, fq)
        }
        // for ((argumentExpr) in callExpr.arguments) {
        //     inferExprTy(argumentExpr, fq)
        // }
    }

    if (funcItem != null) {
        val ss = funcItem.containingModule?.fqName + "::" + funcItem.name
        val res = callExpr.typeArguments.map { it.containingModule?.fqName+"::"+it.text.replace(Regex("<.*>"), "")}
        val ooop=Ops(ss, res)
        println("oop : ${ooop.toString()}")
        fq.add(ooop)
    }


}

private fun inferDotExpr(dotExpr: MvExpr, fq: LinkedHashSet<Ops>) {
    inferExpr(dotExpr, fq)

}

private fun inferStructLitExpr(litExpr: MvStructLitExpr, fq: LinkedHashSet<Ops>) {
    val structItem = litExpr.path.maybeStruct
    val structTypeVars = structItem?.typeParameters?.map { TyInfer.TyVar(TyTypeParameter(it)) }


    // TODO: combine it with TyStruct constructor
    val typeArgs = litExpr.path.typeArguments

    for (field in litExpr.fields) {
        val fieldName = field.referenceName
        val fieldExprTy = field.inferInitExprTy(fq)
    }
}

fun MvStructLitField.inferInitExprTy(fq: LinkedHashSet<Ops>) {
    val initExpr = this.expr
    if (initExpr != null) {
        initExpr.inferExpr2(fq)
    }

}

fun MvExpr.inferExpr2(fq: LinkedHashSet<Ops>) = inferExpr(this, fq)

private fun inferBinaryExpr(exprList: List<MvExpr>, fq: LinkedHashSet<Ops>) {
    for ((i, expr) in exprList.withIndex()) {
        inferExpr(expr, fq)
    }
}

private fun inferDerefExpr(derefExpr: MvDerefExpr, fq: LinkedHashSet<Ops>) {
    val exprTy =
        derefExpr.expr?.let { inferExpr(it, fq) }
}

private fun inferTupleLitExpr(tupleExpr: MvTupleLitExpr, fq: LinkedHashSet<Ops>) {
    val types = tupleExpr.exprList.map { it.inferExpr2(fq) }

}


private fun inferIfExpr(ifExpr: MvIfExpr, fq: LinkedHashSet<Ops>) {
    val ifTy = ifExpr.returningExpr?.inferExpr2(fq)
    val elseTy = ifExpr.elseExpr?.inferExpr2(fq)

}
