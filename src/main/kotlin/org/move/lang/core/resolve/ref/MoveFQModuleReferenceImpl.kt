package org.move.lang.core.resolve.ref

import org.move.lang.core.psi.MvFQModuleRef
import org.move.lang.core.psi.MvModule
import org.move.lang.core.psi.MvNamedElement
import org.move.lang.core.psi.ext.wrapWithList
import org.move.lang.core.resolve.processFQModuleRef

interface MvFQModuleReference : MvReference {
    override fun resolve(): MvNamedElement?
}

class MvFQModuleReferenceImpl(
    element: MvFQModuleRef,
) : MvReferenceCached<MvFQModuleRef>(element), MvFQModuleReference {

    override fun resolveInner(): List<MvNamedElement> {
        val referenceName = element.referenceName
        var resolved: MvModule? = null
        processFQModuleRef(element) {
            val element = it.element as? MvModule ?: return@processFQModuleRef false
            if (it.name == referenceName) {
                resolved = element
                true
            } else {
                false
            }
        }
        return resolved.wrapWithList()
    }
}
