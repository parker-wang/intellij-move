package org.move.ide.inspections

import com.intellij.openapi.util.Key
import com.intellij.psi.PsiElement
import com.intellij.psi.util.*
import org.move.lang.core.psi.*
import org.move.lang.core.psi.ext.isLocal

data class PathUsageMap(
    val pathUsages: MutableMap<String, MutableSet<MvNamedElement>>,
)

private val PATH_USAGE_KEY: Key<CachedValue<PathUsageMap>> = Key.create("PATH_USAGE_KEY")

val MvItemsOwner.pathUsageMap: PathUsageMap
    get() = CachedValuesManager.getCachedValue(this, PATH_USAGE_KEY) {
        val usages = calculatePathUsages(this)
        CachedValueProvider.Result.create(usages, PsiModificationTracker.MODIFICATION_COUNT)
    }

private fun calculatePathUsages(owner: MvItemsOwner): PathUsageMap {
    val usages = hashMapOf<String, MutableSet<MvNamedElement>>()

    for (child in owner.children) {
        PsiTreeUtil.processElements(child) { element -> handleElement(element, usages) }
    }

    return PathUsageMap(usages)
}

private fun handleElement(
    element: PsiElement,
    usages: MutableMap<String, MutableSet<MvNamedElement>>
): Boolean {
    fun addItem(name: String, item: MvNamedElement) {
        usages.getOrPut(name) { hashSetOf() }.add(item)
    }

    return when (element) {
        is MvPath -> {
            val moduleRef = element.moduleRef
            when {
                moduleRef != null && moduleRef !is MvFQModuleRef -> {
                    val modName = moduleRef.referenceName ?: return true
                    val targets = moduleRef.reference?.multiResolve().orEmpty()
                    targets.forEach { addItem(modName, it) }
                    true
                }
                moduleRef == null -> {
                    val name = element.referenceName ?: return true
                    val targets = element.reference?.multiResolve().orEmpty()
                    targets.forEach { addItem(name, it) }
                    true
                }
                else -> return true
            }
        }
        else -> true
    }
}
