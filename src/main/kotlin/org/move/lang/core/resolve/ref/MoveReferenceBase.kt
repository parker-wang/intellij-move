package org.move.lang.core.resolve.ref

import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import org.move.lang.core.psi.MvNamedElement
import org.move.lang.core.psi.MvReferenceElement
import org.move.utils.doRenameIdentifier

//interface MvPolyVariantReference : PsiPolyVariantReference {
//    override fun getElement(): MvElement
//}
//
//abstract class MvPolyVariantReferenceBase<T : MvPolyVariantReferenceElement>(element: T) :
//    PsiPolyVariantReferenceBase<T>(element),
//    MvPolyVariantReference {
//
//    override fun equals(other: Any?): Boolean =
//        other is MvPolyVariantReferenceBase<*> && element === other.element
//
//    override fun hashCode(): Int = element.hashCode()
//
//    final override fun getRangeInElement(): TextRange = super.getRangeInElement()
//
//    final override fun calculateDefaultRangeInElement(): TextRange {
//        val anchor = element.referenceNameElement ?: return TextRange.EMPTY_RANGE
//        return TextRange.from(
//            anchor.startOffsetInParent,
//            anchor.textLength
//        )
//    }
//
//    override fun handleElementRename(newName: String): PsiElement {
//        val refNameElement = element.referenceNameElement
//        if (refNameElement != null) {
//            doRenameIdentifier(refNameElement, newName)
//        }
//        return element
//    }
//}

abstract class MvReferenceBase<T : MvReferenceElement>(element: T) : PsiPolyVariantReferenceBase<T>(element),
                                                                     MvReference {

    override fun resolve(): MvNamedElement? = super.resolve() as? MvNamedElement

    override fun multiResolve(incompleteCode: Boolean): Array<out ResolveResult> =
        multiResolve().map { PsiElementResolveResult(it) }.toTypedArray()

    override fun equals(other: Any?): Boolean =
        other is MvReferenceBase<*> && element === other.element

    override fun hashCode(): Int = element.hashCode()

    final override fun getRangeInElement(): TextRange = super.getRangeInElement()

    final override fun calculateDefaultRangeInElement(): TextRange {
        val anchor = element.referenceNameElement ?: return TextRange.EMPTY_RANGE
        return TextRange.from(
            anchor.startOffsetInParent,
            anchor.textLength
        )
    }

    override fun handleElementRename(newName: String): PsiElement {
        val refNameElement = element.referenceNameElement
        if (refNameElement != null) {
            doRenameIdentifier(refNameElement, newName)
        }
        return element
    }
}
