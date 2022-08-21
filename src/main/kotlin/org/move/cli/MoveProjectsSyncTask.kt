package org.move.cli

import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Computable
import com.intellij.psi.PsiDocumentManager
import org.move.cli.manifest.MoveToml
import org.move.cli.manifest.TomlDependency
import org.move.lang.toNioPathOrNull
import org.move.lang.toTomlFile
import org.move.openapiext.contentRoot
import org.move.openapiext.contentRoots
import org.move.openapiext.resolveExisting
import org.move.openapiext.toVirtualFile
import org.move.stdext.iterateFiles
import java.util.concurrent.CompletableFuture

class MoveProjectsSyncTask(
    project: Project,
    private val future: CompletableFuture<List<MoveProject>>
) : Task.Backgroundable(project, "Reloading Move packages", true) {

    override fun run(indicator: ProgressIndicator) {
        indicator.isIndeterminate = true

        // aptos move fetch
        fetchDependencies()

        val projects = PsiDocumentManager
            .getInstance(project)
            .commitAndRunReadAction(Computable { loadProjects(project) })
        future.complete(projects)
    }

    private fun fetchDependencies() {
        // run `aptos move fetch` here
    }

    companion object {
        private data class DepId(val root: String, val from: String?)

        fun loadProjects(project: Project): List<MoveProject> {
            val projects = mutableListOf<MoveProject>()
           println("Loading project ${project.contentRoot}")
           project.contentRoots.forEach {
               println("dsad ${it.path}")
           }
            for (contentRoot in project.contentRoots) {
                contentRoot.iterateFiles({ it.name == Consts.MANIFEST_FILE }) {
                    val rawDepQueue = ArrayDeque<Pair<TomlDependency, RawAddressMap>>()
                    val root = it.parent?.toNioPathOrNull() ?: return@iterateFiles true
                    val tomlFile = it.toTomlFile(project) ?: return@iterateFiles true
                    val moveToml = MoveToml.fromTomlFile(tomlFile, root)
                    rawDepQueue.addAll(moveToml.deps)
                    val rootPackage = MovePackage.fromMoveToml(moveToml) ?: return@iterateFiles true
                    val deps = mutableListOf<Pair<MovePackage, RawAddressMap>>()
                    val visitedDepIds = mutableSetOf(
                        DepId(rootPackage.contentRoot.path, null)
                    )
                    loadDependencies(project, moveToml, deps, visitedDepIds)
                    projects.add(MoveProject(project, rootPackage, deps))
                    

                    true
                }
            }

            return projects
        }

        private fun loadDependencies(
            project: Project,
            rootMoveToml: MoveToml,
            deps: MutableList<Pair<MovePackage, RawAddressMap>>,
            visitedIds: MutableSet<DepId>
        ) {
            for ((dep, addressMap) in rootMoveToml.deps) {
                val depRoot = dep.localPath()

                val depId = DepId(
                    depRoot.toString(),
                    rootMoveToml.tomlFile?.parent?.virtualFile?.path
                )
                if (depId in visitedIds) continue

                val depTomlFile = depRoot
                    .resolveExisting(Consts.MANIFEST_FILE)
                    ?.toVirtualFile()
                    ?.toTomlFile(project) ?: continue
                val depMoveToml = MoveToml.fromTomlFile(depTomlFile, depRoot)

                // first try to parse MovePackage from dependency, no need for nested if parent is invalid
                val depPackage = MovePackage.fromMoveToml(depMoveToml) ?: continue

                // parse all nested dependencies with their address maps
                visitedIds.add(depId)
                loadDependencies(project, depMoveToml, deps, visitedIds)

                deps.add(Pair(depPackage, addressMap))
            }
        }
    }
}
