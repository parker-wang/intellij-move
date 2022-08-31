package org.move.psi


// import org.move.cli.runconfig.DefaultRunConfigurationFactory
import OkReflect
import com.intellij.diagnostic.StartUpMeasurer
import com.intellij.ide.impl.OpenProjectTask
import com.intellij.ide.impl.ProjectUtilCore
import com.intellij.ide.impl.isTrusted
import com.intellij.ide.impl.setTrusted
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.serviceIfCreated
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.options.newEditor.SettingsTreeView.prepareProject
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.project.impl.ProjectExImpl
import com.intellij.openapi.project.impl.ProjectImpl
import com.intellij.openapi.project.impl.ProjectManagerImpl
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.util.SystemInfoRt
import com.intellij.openapi.util.io.FileUtil
import com.intellij.platform.PlatformProjectOpenProcessor
import org.jetbrains.annotations.ApiStatus
import org.move.cli.*
import org.move.cli.manifest.MoveToml
import org.move.cli.manifest.TomlDependency
import org.move.cli.runconfig.addDefaultBuildRunConfiguration
import org.move.cli.settings.aptosPath
import org.move.cli.settings.moveSettings
import org.move.ide.newProject.openFile
import org.move.ide.notifications.updateAllNotifications
import org.move.lang.core.psi.*
import org.move.lang.core.psi.ext.wrapWithList
import org.move.lang.findMoveTomlPath
import org.move.lang.toNioPathOrNull
import org.move.lang.toTomlFile
import org.move.openapiext.*
import org.move.stdext.iterateFiles
import java.io.IOException
import java.nio.file.*
import java.util.*
import kotlin.collections.ArrayDeque
import org.move.stdext.iterateFiles

class HandleDepend : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        // org.move.psi.HandleDepend.actionPerformed
        // var service = e.project?.serviceIfCreated<MoveProjectsService>()
        // val project = service?.project
        // project?.modules?.forEach { println("service project ${it.name}") }
        // val cpath = toCanonicalName("C:\\Users\\15061\\Documents\\sun\\test\\stacoin")
        // val cpath = toCanonicalName("../../test/stacoin")

        val cpath = toCanonicalName("C:\\Users\\15061\\Documents\\sun\\code\\my-counter\\stacoin")
        ProjectManager.getInstanceIfCreated()
        extracted2(cpath)

        // com.intellij.openapi.project.ex.ProjectManagerEx.loadProject(cpath)
        // val createProject = ProjectManagerEx.getInstanceIfCreated()
        //     ?.createProject("starcoin", "C:\\Users\\15061\\Documents\\sun\\test\\stacoin")
        // println("createProject ${createProject?.name}")
        // println("createProject init ${createProject?.isInitialized}")
        // println("cp contpath ${createProject?.contentRoot}")
        // var service = e.project?.serviceIfCreated<MoveProjectsService>()
        // if (service != null) {
        //     val project = service.project
        //     println("project ${project.name}")
        //     println("project init${project.isInitialized}")
        // }
        // service?.allProjects?.forEach {
        //     println("allProjects ${it.contentRoot}")
        // }
        // var virtualFile = cpath.toVirtualFile()
        // println("doOpenProject handle: $virtualFile")
        // var result: org.move.psi.PrepareProjectResult? =null
        //
        // if (Files.isDirectory(cpath)) {
        //     val createOptionsToOpenDotIdeaOrCreateNewIfNotExists =
        //         createOptionsToOpenDotIdeaOrCreateNewIfNotExists(cpath, projectToClose = null)
        //     if (createOptionsToOpenDotIdeaOrCreateNewIfNotExists.project == null) {
        //         val okReflect = OkReflect.on("com.intellij.openapi.project.impl.ProjectManagerExImpl")
        //             .create()
        //             .call("prepareProject", createOptionsToOpenDotIdeaOrCreateNewIfNotExists, cpath).get<org.move.psi.PrepareProjectResult>()
        //         println("okReflect ${okReflect is org.move.psi.PrepareProjectResult}")
        //         if (okReflect != null) {
        //             result = okReflect
        //         }
        //         // result = prepareProject(createOptionsToOpenDotIdeaOrCreateNewIfNotExists, cpath)
        //     }
        //     else {
        //         result = PrepareProjectResult(createOptionsToOpenDotIdeaOrCreateNewIfNotExists.project as Project, null)
        //     }
        // }
        // if(result is org.move.psi.PrepareProjectResult){
        //     println("result ${result.project.name}")
        //     println("result init ${result.project.isInitialized}")
        // }

        // var projectToClose = createProject
        // val forceOpenInNewFrame = false
        // val platformOpenProcessor = PlatformProjectOpenProcessor.getInstance()
        // if (virtualFile != null) {
        //     platformOpenProcessor.doOpenProject(
        //         virtualFile,
        //         projectToClose,
        //         forceOpenInNewFrame
        //     )?.also {
        //         println(" handle platformOpenProcessor.doOpenProject :${it.isInitialized}")
        //         StartupManager.getInstance(it).runAfterOpened {
        //             // create default build configuration if it doesn't exist
        //             if (it.aptosBuildRunConfigurations().isEmpty()) {
        //                 val isEmpty = it.aptosRunConfigurations().isEmpty()
        //                 it.addDefaultBuildRunConfiguration(isSelected = isEmpty)
        //             }
        //
        //             val packageRoot = it.contentRoots.firstOrNull()
        //             if (packageRoot != null) {
        //                 val manifest = packageRoot.findChild(Consts.MANIFEST_FILE)
        //                 // if (manifest != null) {
        //                 //     it.openFile(manifest)
        //                 // }
        //                 // updateAllNotifications(it)
        //             }
        //
        //             val aptosPath = Aptos.suggestPath()
        //             if (aptosPath != null && it.aptosPath?.toString().isNullOrBlank()) {
        //                 it.moveSettings.modify {
        //                     it.aptosPath = aptosPath
        //                 }
        //             }
        //             // it.moveProjects.refreshAllProjects()
        //         }
        //     }
        // }
        // val openProjectTask = OpenProjectTask(isNewProject = true, runConfigurators = false, projectName = name)
        // val project = instantiateProject(cpath, openProjectTask)
        // try {
        //     val template = if (openProjectTask.useDefaultProjectAsTemplate) defaultProject else null
        //     ProjectManagerImpl.initProject(
        //         projectFile, project, options.isRefreshVfsNeeded, options.preloadServices, template,
        //         ProgressManager.getInstance().progressIndicator
        //     )
        //     project.setTrusted(true)
        //     return project
        // }
        // catch (t: Throwable) {
        //     handleErrorOnNewProject(t)
        //     return null
        // }
    }


    // private fun toCanonicalName(filePath: String): Path {
    //     val file = Paths.get(filePath)
    //     try {
    //         if (SystemInfoRt.isWindows && FileUtil.containsWindowsShortName(filePath)) {
    //             return file.toRealPath(LinkOption.NOFOLLOW_LINKS)
    //         }
    //     } catch (ignore: InvalidPathException) {
    //     } catch (e: IOException) {
    //         // OK. File does not yet exist, so its canonical path will be equal to its original path.
    //     }
    //     return file
    // }
    //
    // private fun loadDependencies(
    //     project: Project,
    //     rootMoveToml: MoveToml,
    //     deps: MutableList<Pair<MovePackage, RawAddressMap>>,
    //     visitedIds: MutableSet<DepId>,
    // ) {
    //     for ((dep, addressMap) in rootMoveToml.deps) {
    //         val depRoot = dep.localPath()
    //
    //         val depId = DepId(
    //             depRoot.toString(),
    //             rootMoveToml.tomlFile?.parent?.virtualFile?.path
    //         )
    //         if (depId in visitedIds) continue
    //
    //         val depTomlFile = depRoot
    //             .resolveExisting(Consts.MANIFEST_FILE)
    //             ?.toVirtualFile()
    //             ?.toTomlFile(project) ?: continue
    //         val depMoveToml = MoveToml.fromTomlFile(depTomlFile, depRoot)
    //
    //         // first try to parse MovePackage from dependency, no need for nested if parent is invalid
    //         val depPackage = MovePackage.fromMoveToml(depMoveToml) ?: continue
    //
    //         // parse all nested dependencies with their address maps
    //         visitedIds.add(depId)
    //         loadDependencies(project, depMoveToml, deps, visitedIds)
    //
    //         deps.add(Pair(depPackage, addressMap))
    //     }
    // }
    //
    // protected open fun instantiateProject(projectStoreBaseDir: Path, options: OpenProjectTask): ProjectImpl {
    //     val activity = StartUpMeasurer.startActivity("project instantiation")
    //     val project = ProjectExImpl(projectStoreBaseDir, options.projectName)
    //     activity.end()
    //     options.beforeInit?.invoke(project)
    //     return project
    // }
    //
    // fun createOptionsToOpenDotIdeaOrCreateNewIfNotExists(projectDir: Path, projectToClose: Project?): OpenProjectTask =
    //     OpenProjectTask(
    //         runConfigurators = true,
    //         isNewProject = !ProjectUtilCore.isValidProjectPath(projectDir),
    //         projectToClose = projectToClose,
    //         isRefreshVfsNeeded = !ApplicationManager.getApplication().isUnitTestMode,  // doesn't make sense to refresh
    //         useDefaultProjectAsTemplate = true
    //     )
}

fun extracted(cpath: Path): MoveProject {
    val loadProject = ProjectManagerEx.getInstanceEx().loadProject(cpath)
    println(loadProject.basePath)
    println(loadProject.workspaceFile?.path)
    println(loadProject.isTrusted())
    loadProject.wrapWithList().forEach {
        println(it.name)
    }
    // loadProject.basePath
    println("daxiao ${loadProject.modules.size}")
    println("module:${loadProject.modules.toList()}")
    println("loadProject before ${loadProject.basePath}")
    val contentRoots = loadProject.modules.toList().asSequence()
        .flatMap { ModuleRootManager.getInstance(it).contentRoots.asSequence() }
    println("contentRoots ${contentRoots.count()}")
    val projects = mutableListOf<MoveProject>()
    for (contentRoot in contentRoots) {
        println(
            "contentRoot item" +
                    " ${contentRoot.path}"
        )
        contentRoot.iterateFiles({ it.name == Consts.MANIFEST_FILE }) {
            val rawDepQueue = ArrayDeque<Pair<TomlDependency, RawAddressMap>>()
            val root = it.parent?.toNioPathOrNull() ?: return@iterateFiles true
            val tomlFile = it.toTomlFile(loadProject) ?: return@iterateFiles true
            val moveToml = MoveToml.fromTomlFile(tomlFile, root)
            rawDepQueue.addAll(moveToml.deps)
            val rootPackage = MovePackage.fromMoveToml(moveToml) ?: return@iterateFiles true
            val deps = mutableListOf<Pair<MovePackage, RawAddressMap>>()
            val visitedDepIds = mutableSetOf(
                DepId(rootPackage.contentRoot.path, null)
            )
            loadDependencies(loadProject, moveToml, deps, visitedDepIds)
            projects.add(MoveProject(loadProject, rootPackage, deps))
            true
        }
    }
    return projects.first()
    // println("projects ${projects.size}")
    // projects.forEach {
    //     println("project ${it.contentRootPath}")
    //
    // }
    // for (item in projects) {
    //     val mp = item.movePackages()
    //
    //     val elementAt = mp.elementAt(0)
    //     elementAt.moveToml.addresses.forEach {
    //         println("address key: ${it.key}, value: ${it.value}")
    //     }
    //     val deps = elementAt.moveToml.deps
    //     println(
    //         "dep " +
    //                 " size ${deps.size}"
    //     )
    // }
}

fun extracted2(cpath: Path) {
    val loadProject = ProjectManagerEx.getInstanceEx().loadProject(cpath)
    val mg = ModuleManager.getInstance(loadProject)
    // val loadModuleSS = mg.loadModule(cpath.toString())
    // println("modules is null ? ${loadModuleSS}")
    println("ppppppp")
    println(loadProject.basePath)
    println(loadProject.workspaceFile?.path)
    println(loadProject.isTrusted())
    loadProject.wrapWithList().forEach {
        println(it.name)
    }
    // loadProject.basePath
    // loadProject.basePath
    println("daxiao ${loadProject.modules.size}")
    println("module:${loadProject.modules.toList()}")
    loadProject.modules.forEach {
        println("module ${it.name}")
        println("java class  ${it.javaClass}")
    }
    println("loadProject before ${loadProject.basePath}")
    val contentRoots = loadProject.modules.toList().asSequence()
        .flatMap { ModuleRootManager.getInstance(it).contentRoots.asSequence() }
    println("contentRoots ${contentRoots.count()}")
    val projects = mutableListOf<MoveProject>()
    for (contentRoot in contentRoots) {
        println(
            "contentRoot item" +
                    " ${contentRoot.path}"
        )
        contentRoot.iterateFiles({ it.name == Consts.MANIFEST_FILE }) {
            val rawDepQueue = ArrayDeque<Pair<TomlDependency, RawAddressMap>>()
            val root = it.parent?.toNioPathOrNull() ?: return@iterateFiles true
            val tomlFile = it.toTomlFile(loadProject) ?: return@iterateFiles true
            val moveToml = MoveToml.fromTomlFile(tomlFile, root)
            rawDepQueue.addAll(moveToml.deps)
            val rootPackage = MovePackage.fromMoveToml(moveToml) ?: return@iterateFiles true
            val deps = mutableListOf<Pair<MovePackage, RawAddressMap>>()
            val visitedDepIds = mutableSetOf(
                DepId(rootPackage.contentRoot.path, null)
            )
            loadDependencies(loadProject, moveToml, deps, visitedDepIds)
            projects.add(MoveProject(loadProject, rootPackage, deps))
            true
        }
    }

    println("projects ${projects.size}")
    projects.forEach {
        println("project ${it.contentRootPath}")

    }
    for (item in projects) {
        val mp = item.movePackages()

        val elementAt = mp.elementAt(0)
        elementAt.moveToml.addresses.forEach {
            println("address key: ${it.key}, value: ${it.value}")
        }
        val deps = elementAt.moveToml.deps
        println(
            "dep " +
                    " size ${deps.size}"
        )
    }
    println("enddd")
}

