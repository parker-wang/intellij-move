package org.move.lang.core.psi.ext

import com.intellij.lang.ASTNode
import org.move.lang.MvElementTypes
import org.move.lang.core.psi.MvElementImpl
import org.move.lang.core.psi.MvNamedElement
import org.move.lang.core.psi.MvSchemaField
import org.move.lang.core.resolve.ref.MvReference
import org.move.lang.core.resolve.ref.MvReferenceCached
import org.move.lang.core.resolve.ref.Namespace
import org.move.lang.core.resolve.resolveItem

val MvSchemaField.isShorthand get() = !hasChild(MvElementTypes.COLON)

class MvSchemaFieldReferenceImpl(
    element: MvSchemaField
) : MvReferenceCached<MvSchemaField>(element) {
    override fun resolveInner(): List<MvNamedElement> {
        return resolveItem(element, Namespace.SCHEMA_FIELD)
    }
}

class MvSchemaFieldShorthandReferenceImpl(
    element: MvSchemaField
) : MvReferenceCached<MvSchemaField>(element) {
    override fun resolveInner(): List<MvNamedElement> {
        return listOf(
            resolveItem(element, Namespace.SCHEMA_FIELD),
            resolveItem(element, Namespace.NAME)
        ).flatten()

    }
}

abstract class MvSchemaRefFieldMixin(node: ASTNode) : MvElementImpl(node),
                                                      MvSchemaField {
    override fun getReference(): MvReference {
        if (this.isShorthand) {
            return MvSchemaFieldShorthandReferenceImpl(this)
        } else {
            return MvSchemaFieldReferenceImpl(this)
        }
    }
}