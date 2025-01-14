package org.move.ide.lineMarkers

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.psi.PsiElement
import org.move.cli.runconfig.producers.PublishCommandConfigurationProducer
import org.move.cli.runconfig.producers.TestCommandConfigurationProducer
import org.move.ide.MoveIcons
import org.move.lang.MvElementTypes.IDENTIFIER
import org.move.lang.core.psi.MvFunction
import org.move.lang.core.psi.MvModule
import org.move.lang.core.psi.MvNameIdentifierOwner
import org.move.lang.core.psi.ext.elementType

class AptosCommandLineMarkerContributor : RunLineMarkerContributor() {
    override fun getInfo(element: PsiElement): Info? {
        if (element.elementType != IDENTIFIER) return null
        val parent = element.parent
        if (parent !is MvNameIdentifierOwner || element != parent.nameIdentifier) return null

        if (parent is MvFunction) {
            val config =
                TestCommandConfigurationProducer.fromLocation(parent, climbUp = false)
            if (config != null) {
                return Info(
                    MoveIcons.TEST,
                    { config.configurationName },
                    *contextActions()
                )
            }
        }
        if (parent is MvModule) {
            val publishConfig =
                PublishCommandConfigurationProducer.fromLocation(parent, false)
            if (publishConfig != null) {
                return Info(
                    MoveIcons.PUBLISH,
                    { publishConfig.configurationName },
                    *ExecutorAction.getActions(1)
                )
            }
            val testConfig = TestCommandConfigurationProducer.fromLocation(parent, climbUp = false)
            if (testConfig != null) {
                return Info(
                    MoveIcons.TEST,
                    { testConfig.configurationName },
                    *contextActions()
                )
            }
        }
        return null
    }
}

private fun contextActions(): Array<AnAction> {
    return ExecutorAction.getActions(0).toList()
//        .filter { it.toString().startsWith("Run context configuration") }
        .toTypedArray()
}
