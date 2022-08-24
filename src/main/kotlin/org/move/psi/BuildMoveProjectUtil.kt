package org.move.psi

import com.intellij.diagnostic.StartUpMeasurer
import com.intellij.ide.impl.OpenProjectTask
import com.intellij.ide.impl.ProjectUtilCore
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.impl.ProjectExImpl
import com.intellij.openapi.project.impl.ProjectImpl
import com.intellij.openapi.util.SystemInfoRt
import com.intellij.openapi.util.io.FileUtil
import org.move.cli.Consts
import org.move.cli.MovePackage
import org.move.cli.RawAddressMap
import org.move.cli.manifest.MoveToml
import org.move.lang.toTomlFile
import org.move.openapiext.resolveExisting
import org.move.openapiext.toVirtualFile
import java.io.IOException
import java.nio.file.InvalidPathException
import java.nio.file.LinkOption
import java.nio.file.Path
import java.nio.file.Paths

fun toCanonicalName(filePath: String): Path {
    val file = Paths.get(filePath)
    try {
        if (SystemInfoRt.isWindows && FileUtil.containsWindowsShortName(filePath)) {
            return file.toRealPath(LinkOption.NOFOLLOW_LINKS)
        }
    } catch (ignore: InvalidPathException) {
    } catch (e: IOException) {
        // OK. File does not yet exist, so its canonical path will be equal to its original path.
    }
    return file
}


fun loadDependencies(
    project: Project,
    rootMoveToml: MoveToml,
    deps: MutableList<Pair<MovePackage, RawAddressMap>>,
    visitedIds: MutableSet<DepId>,
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

fun instantiateProject(projectStoreBaseDir: Path, options: OpenProjectTask): ProjectImpl {
    val activity = StartUpMeasurer.startActivity("project instantiation")
    val project = ProjectExImpl(projectStoreBaseDir, options.projectName)
    activity.end()
    options.beforeInit?.invoke(project)
    return project
}

fun createOptionsToOpenDotIdeaOrCreateNewIfNotExists(projectDir: Path, projectToClose: Project?): OpenProjectTask =
    OpenProjectTask(
        runConfigurators = true,
        isNewProject = !ProjectUtilCore.isValidProjectPath(projectDir),
        projectToClose = projectToClose,
        isRefreshVfsNeeded = !ApplicationManager.getApplication().isUnitTestMode,  // doesn't make sense to refresh
        useDefaultProjectAsTemplate = true
    )


data class DepId(val root: String, val from: String?)

data class PrepareProjectResult(val project: Project, val module: Module?)