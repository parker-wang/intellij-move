package org.move.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.move.lang.core.psi.MoveElementImpl
import org.move.lang.core.psi.MoveType
import org.move.lang.core.types.infer.InferenceContext
import org.move.lang.core.types.ty.Ty
import org.move.lang.core.types.ty.TyUnknown

abstract class MoveTypeMixin(node: ASTNode) : MoveElementImpl(node), MoveType {
    override fun resolvedType(): Ty = TyUnknown
}