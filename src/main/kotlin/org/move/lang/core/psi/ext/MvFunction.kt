package org.move.lang.core.psi.ext

import com.intellij.ide.projectView.PresentationData
import com.intellij.lang.ASTNode
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.util.PlatformIcons
import org.move.ide.MoveIcons
import org.move.ide.annotator.BUILTIN_FUNCTIONS
import org.move.lang.core.psi.MvFunction
import org.move.lang.core.psi.impl.MvNameIdentifierOwnerImpl
import org.move.lang.core.psi.isNative
import org.move.lang.core.types.ty.Ty
import javax.swing.Icon

enum class FunctionVisibility {
    PRIVATE,
    PUBLIC,
    PUBLIC_FRIEND,
    PUBLIC_SCRIPT;
}

val MvFunction.visibility: FunctionVisibility
    get() {
        val visibility = this.functionVisibilityModifier ?: return FunctionVisibility.PRIVATE
        return when (visibility.node.text) {
            "public" -> FunctionVisibility.PUBLIC
            "public(friend)" -> FunctionVisibility.PUBLIC_FRIEND
            "public(script)" -> FunctionVisibility.PUBLIC_SCRIPT
            else -> FunctionVisibility.PRIVATE
        }
    }

val MvFunction.isTest: Boolean get() = this.findSingleItemAttr("test") != null

val MvFunction.isBuiltinFunc get() = this.isNative && this.name in BUILTIN_FUNCTIONS

val MvFunction.acquiresTys: List<Ty>
    get() =
        this.acquiresType?.pathTypeList.orEmpty().map { it.ty() }

val MvFunction.signatureText: String get() {
    val params = this.functionParameterList?.parametersText ?: "()"
    val returnTypeText = this.returnType?.type?.text ?: ""
    val returnType = if (returnTypeText == "") "" else ": $returnTypeText"
    return "$params$returnType"
}

val MvFunction.outerFileName: String
    get() =
        if (this.name in BUILTIN_FUNCTIONS) {
            "builtins"
        } else {
            this.containingFile?.name.orEmpty()
        }

abstract class MvFunctionMixin(node: ASTNode) : MvNameIdentifierOwnerImpl(node),
                                                MvFunction {
    var builtIn = false

    override fun canNavigate(): Boolean = !builtIn
    override fun canNavigateToSource(): Boolean = !builtIn

    override fun getIcon(flags: Int): Icon = MoveIcons.FUNCTION

    override fun getPresentation(): ItemPresentation? {
        val name = this.name ?: return null
//        val params = this.functionParameterList?.parametersText ?: "()"
//        val returnTypeText = this.returnType?.type?.text ?: ""
//        val tail = if (returnTypeText == "") "" else ": $returnTypeText"
        return PresentationData(
            "$name${this.signatureText}",
            null,
            PlatformIcons.PUBLIC_ICON,
            TextAttributesKey.createTextAttributesKey("public")
        );
    }
}
