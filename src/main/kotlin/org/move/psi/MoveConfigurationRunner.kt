package org.move.psi

import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.runners.DefaultProgramRunner
import org.move.cli.runconfig.AptosCommandConfiguration

class MoveConfigurationRunner: DefaultProgramRunner() {

    override fun getRunnerId(): String {
        TODO("Not yet implemented")
    }


//    val LOG = TsLog(javaClass)

    override fun canRun(executorId: String, profile: RunProfile): Boolean
    {
        val bool = DefaultRunExecutor.EXECUTOR_ID == executorId && profile is AptosCommandConfiguration

        //LOG.info("[canRun] $bool $executorId $profile")

        return bool
    }
}