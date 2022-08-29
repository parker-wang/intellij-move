package org.move.psi

// import org.move.cli.runconfig.DefaultRunConfigurationFactory
import com.intellij.execution.ProgramRunnerUtil
import com.intellij.execution.RunManager
import com.intellij.execution.RunManagerEx
import com.intellij.execution.RunnerAndConfigurationSettings
import com.intellij.execution.executors.DefaultRunExecutor
import com.intellij.execution.executors.DefaultRunExecutor.EXECUTOR_ID
import com.intellij.execution.runners.ProgramRunner
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.serviceIfCreated
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.selected
import com.jetbrains.rd.util.string.printToString
import com.jetbrains.rd.util.string.println
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.move.cli.Consts
import org.move.cli.MoveProject
import org.move.cli.MoveProjectsService
import org.move.cli.manifest.MoveToml
import org.move.cli.manifest.TomlDependency
import org.move.cli.runconfig.AptosCommandConfiguration
import org.move.cli.runconfig.AptosCommandConfigurationType
import org.move.lang.MoveFile
import org.move.lang.core.psi.MvElement
import org.move.lang.core.psi.MvFunction
import org.move.lang.core.psi.MvModule
import org.move.lang.core.psi.containingFunction
import org.move.lang.core.psi.ext.allFunctions
import org.move.lang.core.psi.ext.fqName
import org.move.lang.core.psi.usages
import org.move.lang.isMoveFile
import org.move.lang.modules
import org.move.lang.toMoveFile
import org.move.openapiext.toPsiFile
import org.move.openapiext.toVirtualFile
import org.move.psi.FunUtil.Companion.findFunction
import org.move.stdext.toPath
import org.toml.lang.psi.TomlFile
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.*
import javax.swing.JComponent


class ResouceTest : AnAction() {
    private var nowPath: MutableList<String> = mutableListOf()

    override fun actionPerformed(e: AnActionEvent) {

        val project = e.project

        val runManager = RunManagerEx.getInstanceEx(project!!)

        val configurationFactory = DefaultRunConfigurationFactory(runManager, project)
        val configuration: RunnerAndConfigurationSettings = configurationFactory.createAptosBuildConfiguration()
        runManager.addConfiguration(configuration)

        val sys = System.getProperty("os.name")
        if (!sys.lowercase(Locale.getDefault()).startsWith("win")) {
            println("Linux or Macos")
            val runner = ProgramRunner.getRunner(EXECUTOR_ID, configuration.configuration)
            val executableName = "aptos"
            println("runner==null${runner == null}")
            val finalExecutor = DefaultRunExecutor.getRunExecutorInstance()
//        finalExecutor.
            println("runner runnerId :${runner?.runnerId}")
            println("finalExecutor ${finalExecutor.description}")
            ProgramRunnerUtil.executeConfiguration(configuration, finalExecutor)
        } else {
            println("Windows")
        }
        var service = project.serviceIfCreated<MoveProjectsService>()
        println(service?.allProjects?.size)
        if (service != null) {
            val findMoveProject = service.findMoveProject(Path.of("../stacoin"))
            println(findMoveProject?.contentRootPath)
        }
        val moveProjects: List<MoveProject> = service?.allProjects ?: listOf()
        println(moveProjects.size)
        var myDialogData = CurrentResult.dialogData("")
        var dialog = CurrentResult(myDialogData)
        dialog.show()
        val jsonpath = myDialogData.JsonPath
        val boolean = File(jsonpath).isFile && File(jsonpath).extension == "json"

        println("jj:$jsonpath")
        // 如果json没有，那就从toml开始解析
        var mvpackages = mutableListOf<TomlDependency>()
        if (!boolean) {
            val finalMoveProjects= mutableListOf<MoveProject>()
            moveProjects.forEach { it ->
                it.movePackages().elementAt(0).moveToml.deps.forEach {
                    mvpackages.add(it.first)
                }
                val tomlfile =
                    it.contentRootPath?.resolve(Consts.MANIFEST_FILE)?.toVirtualFile()?.toPsiFile(project) as TomlFile
                val moveToml = MoveToml.fromTomlFile(tomlfile, it.contentRootPath!!)
                val mpmap = LinkedHashMap<String, List<TomlDependency>>()
                val gmp = GraphForMvPackage()
                gmp.buildGraph(moveToml, it.project.name, mpmap)
                val gerResult = gmp.gerResult(mpmap)



            }


            // val toml=


        } else {
            println("is json")
            return
        }

        // 如果json中没有包含该项目的mvpackage，那就从递归找到最开始没有被解析的项目，这样层层嵌套，保证在解析当前项目时
        // 前面的依赖也已经解析结束。，具体思路时建一张图，找路径然后类比之前的实现前置依赖的加载。
        // 1.第一步从当前目录开始建图


//         for (item in moveProjects) {
//
//
//             val mp = item.movePackages()
//
//             val elementAt = mp.elementAt(0)
//             val deps = elementAt.moveToml.deps
//
//
//             deps.forEach {
//                 println("dep ${it}")
//                 println("dep first ${it.first}")
//                 println("deps second ${it.second}")
//             }
//             // 依赖
//             val externalDep = (deps.map {
//                 it.first.name
//             }).toList()
//
//
//             val sourcesFolder = elementAt.sourcesFolder
//             var Res: MutableList<String> = mutableListOf()
//             var FunInit: MutableList<RSGNode> = mutableListOf()
//             var FunEnd: MutableList<RSGNode> = mutableListOf()
//             var FunCom: MutableList<RSGNode> = mutableListOf()
//             var FunOpts: MutableMap<String, MutableList<Ops>> = mutableMapOf()
//             if (sourcesFolder?.isDirectory() == true) {
//                 // 项目中模块
//                 var modulesInProject = LinkedHashMap<String, String>()
//                 // 获取所有模块
//                 ergodic(modulesInProject, sourcesFolder, project, sourcesFolder.canonicalPath.toString())
//                 var children = sourcesFolder.children
//                 var graph: Array<Array<Int>> =
//                     Array(modulesInProject.size, { Array(modulesInProject.size) { it -> -1 } })
//                 children.forEachIndexed { index, virtualFile ->
//                     if (virtualFile.isMoveFile) {
//                         val moveFile = virtualFile.toMoveFile(project)
//                         moveFile?.modules()?.forEach {
//                             val from =
//                                 modulesInProject.keys.indexOf((it.addressRef?.namedAddress?.text + it.addressRef?.nextSibling?.text + it.addressRef?.nextSibling?.nextSibling?.text).trim())
//                             it.moduleBlock?.useStmtList?.forEach { useitem ->
//                                 if (!externalDep.contains(useitem?.moduleUseSpeck?.fqModuleRef?.addressRef?.text)) {
//                                     val to = modulesInProject.keys.indexOf(useitem?.moduleUseSpeck?.text)
// //                                    调用者指向被调用者
//                                     graph[from][to] = 1
//                                 }
//                             }
//                         }
//                     }
//                 }
//                 var startNode = mutableListOf<Int>()
//                 var endNode = mutableListOf<Int>()
//                 for (node in graph.indices) {
//                     var isStart = true
//                     for (i in graph.indices) {
//                         if (graph[i][node] == 1) {
//                             isStart = false
//                             break
//                         }
//                     }
//                     var isEnd = true
//                     for (i in graph.indices) {
//                         if (graph[node][i] == 1) {
//                             isEnd = false
//                             break
//                         }
//                     }
//                     if (isStart) {
//                         startNode.add(node)
//                     }
//                     if (isEnd) {
//                         endNode.add(node)
//                     }
//                 }
//                 var pathDfs = PathDfs(graph)
//                 var pathSum = mutableListOf<Int>()
//                 for (i in startNode) {
//                     for (j in endNode) {
//                         val findAllPath = pathDfs.findAllPath(i, j)
//                         for (item in findAllPath) {
//                             item.reverse()
//                             item.forEach {
//                                 if (!pathSum.contains(it))
//                                     pathSum.add(it)
//                             }
//                         }
//                     }
//                 }
//                 // 获得一个排好序的模块列表
//                 val keys = modulesInProject.keys.toTypedArray()
//                 for (index in pathSum) {
//                     var targetVF = modulesInProject[keys[index]]
//                     var (filpath, moduleindex) = targetVF!!.split("_")
//                     filpath = sourcesFolder.canonicalPath.toString() + "/" + filpath
//                     var vf = getTargetVirtualFile(sourcesFolder, filpath)
//                     if (vf != null) {
//                         val movefile = vf.toMoveFile(project)
//                         // println("movefile ${movefile?.name}")
// //                        val match = LanguageMatcher.match(movefile!!.language)
//                         val module = movefile?.modules()?.toList()?.get(moduleindex.toInt())
//                         module?.moduleBlock?.let {
//                             it.structList.forEach() { st ->
//                                 val abs = st.abilitiesList?.getAbilityList()
//                                 for (ability in abs.orEmpty()) {
//                                     if (ability.identifier?.getText().equals("key")) {
//                                         Res.add(st.fqName)
//                                     }
//                                 }
//                             }
//                             println("res :${Res}")
//                             if (it.functionList.size > 0) {
//                                 val functionList = it.functionList
//                                 val funSort = getFunSort(functionList, vf)
//                                 // TODO("save dependency to initalize json")
//
//
//                                 val currentResolveResult: MutableMap<String, List<Ops>> = mutableMapOf()
//                                 for (index in funSort) {
//                                     val targetFun = functionList[index]
//                                     findFunction(Res, targetFun, currentResolveResult)
//                                 }
//                                 // println("current: ${currentResolveResult}")
//
//                             }
//                         }
//                     }
//                 }
//
//             }
//         }


    }

    private fun process(mp: MoveProject, path: Path) {
        val content = path.toFile().inputStream().reader(Charset.defaultCharset()).readText()
        val resOps = Json.decodeFromString<ResOps>(content)
        val cuurrentProject = mp.project
        if (resOps.projectName.contains(mp.currentPackage.packageName))
            return
        else {
// 1.处理
//             处理的话就是输入参数为MoveProject,以及一个Funs
            val mp = mp.movePackages()

            val elementAt = mp.elementAt(0)
            val deps = elementAt.moveToml.deps
            // 依赖
            val externalDep = (deps.map {
                it.first.name
            }).toList()
            val sourcesFolder = elementAt.sourcesFolder
            var Res: MutableList<String> = mutableListOf()
            if (sourcesFolder?.isDirectory() == true) {
                // 项目中模块
                var modulesInProject = LinkedHashMap<String, String>()
                // 获取所有模块
                ergodic(modulesInProject, sourcesFolder, cuurrentProject, sourcesFolder.canonicalPath.toString())
                var children = sourcesFolder.children
                var graph: Array<Array<Int>> =
                    Array(modulesInProject.size, { Array(modulesInProject.size) { it -> -1 } })
                children.forEachIndexed { index, virtualFile ->
                    if (virtualFile.isMoveFile) {
                        val moveFile = virtualFile.toMoveFile(cuurrentProject)
                        moveFile?.modules()?.forEach {
                            val from =
                                modulesInProject.keys.indexOf((it.addressRef?.namedAddress?.text + it.addressRef?.nextSibling?.text + it.addressRef?.nextSibling?.nextSibling?.text).trim())
                            it.moduleBlock?.useStmtList?.forEach { useitem ->
                                if (!externalDep.contains(useitem?.moduleUseSpeck?.fqModuleRef?.addressRef?.text)) {
                                    val to = modulesInProject.keys.indexOf(useitem?.moduleUseSpeck?.text)
//                                    调用者指向被调用者
                                    graph[from][to] = 1
                                }
                            }
                        }
                    }
                }
                var startNode = mutableListOf<Int>()
                var endNode = mutableListOf<Int>()
                for (node in graph.indices) {
                    var isStart = true
                    for (i in graph.indices) {
                        if (graph[i][node] == 1) {
                            isStart = false
                            break
                        }
                    }
                    var isEnd = true
                    for (i in graph.indices) {
                        if (graph[node][i] == 1) {
                            isEnd = false
                            break
                        }
                    }
                    if (isStart) {
                        startNode.add(node)
                    }
                    if (isEnd) {
                        endNode.add(node)
                    }
                }
                var pathDfs = PathDfs(graph)
                var pathSum = mutableListOf<Int>()
                for (i in startNode) {
                    for (j in endNode) {
                        val findAllPath = pathDfs.findAllPath(i, j)
                        for (item in findAllPath) {
                            item.reverse()
                            item.forEach {
                                if (!pathSum.contains(it))
                                    pathSum.add(it)
                            }
                        }
                    }
                }
                // 获得一个排好序的模块列表
                val keys = modulesInProject.keys.toTypedArray()
                val mfs = mutableListOf<MvModule>()
                pathSum.forEach { it ->
                    var targetVF = modulesInProject[keys[it]]
                    var (filpath, moduleindex) = targetVF!!.split("_")
                    filpath = sourcesFolder.canonicalPath.toString() + "/" + filpath
                    var vf = getTargetVirtualFile(sourcesFolder, filpath)
                    if (vf != null) {
                        val movefile = vf.toMoveFile(cuurrentProject)

                        val module = movefile?.modules()?.toList()?.get(moduleindex.toInt())
                        module?.let { mfs.add(it) }
                        module?.moduleBlock?.let {
                            it.structList.forEach() { st ->
                                val abs = st.abilitiesList?.getAbilityList()
                                for (ability in abs.orEmpty()) {
                                    if (ability.identifier?.getText().equals("key")) {
                                        Res.add(st.fqName)
                                    }
                                }
                            }
                        }
                    }
                }
                mfs.forEach {
                    val file = it.containingFile.virtualFile
                    val funSort = getFunSort(it.allFunctions(), file)
                    val currentResolveResult: MutableMap<String, List<Ops>> = mutableMapOf()
                    for (index in funSort) {
                        val targetFun = it.allFunctions()[index]
                        findFunction(Res, targetFun, currentResolveResult)
                    }

                }

                // 2.转成json
            }
        }

    }

    private fun getFunSort(funList: List<MvFunction>, vf: VirtualFile): List<Int> {
        val funsize = funList.size
        if (funsize == 0) {
            return listOf()
        }
        var graph: Array<Array<Int>> =
            Array(funsize, { Array(funsize) { it -> -1 } })
        val funnamelist = funList.map { it.name }.toTypedArray()
        println("funnamelist ${funnamelist.toList()}")
// 遍历所有函数建立函数调用图（不包含被其他模块调用的情况）
        funList.forEachIndexed { index, funitem ->
//            val findManager = (FindManager.getInstance(funitem.project) as FindManagerImpl)
//            findManager.findUsagesInScope(/* element = */ funitem, /* searchScope = */
//                GlobalSearchScope.fileScope(funitem.project, vf))
            val funitemIndex = funnamelist.indexOf(funitem.name)
            funitem.usages().forEach {
                // println("usages ${it.element.text}")
                // println("usages item javaclass ${it.javaClass}")
                // 不包含self并且没有::的情况下才是函数调用
                if (it.element.text.indexOf("Self") == -1 && it.element.text.indexOf("::") == -1) {
                    val mvElement = it.element as MvElement?
                    val containingFunction = mvElement?.containingFunction

                    // var clone = it.element.parent
                    // // if (clone is MvPathReference) {
                    // while (!(clone is MvFunction)) {
                    //     clone = clone.parent
                    // }
                    val callindex = funnamelist.indexOf(containingFunction?.nameElement?.text)
                    if (callindex != -1) {
//                        调用者指向被调用者
                        graph[callindex][funitemIndex] = 1
                    }
                }
//                存在Self::表示本模块下调用
                if (it.element.text.indexOf("Self::") != -1) {

                    val mvElement = it.element as MvElement?
                    val containingFunction = mvElement?.containingFunction
                    val callindex = funnamelist.indexOf(containingFunction?.nameElement?.text)
                    if (callindex != -1) {
//                        调用者指向被调用者
                        graph[callindex][funitemIndex] = 1
                    }
                }
            }
        }
        var startNode = mutableListOf<Int>()
        var endNode = mutableListOf<Int>()
        for (node in graph.indices) {
            var isStart = true
            for (i in graph.indices) {
                if (graph[i][node] == 1) {
                    isStart = false
                    break
                }
            }
            var isEnd = true
            for (i in graph.indices) {
                if (graph[node][i] == 1) {
                    isEnd = false
                    break
                }
            }
            if (isStart) {
                startNode.add(node)
            }
            if (isEnd) {
                endNode.add(node)
            }
        }
        var pathDfs = PathDfs(graph)
        var pathSum = mutableListOf<Int>()
        for (i in startNode) {
            for (j in endNode) {
                val findAllPath = pathDfs.findAllPath(i, j)
                for (item in findAllPath) {
                    item.reverse()
                    item.forEach {
                        if (!pathSum.contains(it))
                            pathSum.add(it)
                    }
                }
            }
        }

        return pathSum
    }
//    private fun getFindUsagesHandler(element: PsiElement, forHighlightUsages: Boolean): FindUsagesHandler? {
//        return getFindUsagesHandler(
//            element,
//            if (forHighlightUsages) OperationMode.HIGHLIGHT_USAGES else OperationMode.DEFAULT
//        )
//    }
//
//    private fun getFindUsagesHandler(element: PsiElement, operationMode: OperationMode): FindUsagesHandler? {
//        for (factory in FindUsagesHandlerFactory.EP_NAME.getExtensions(element.project)) {
//            if (factory.canFindUsages(element)) {
//                val handler = factory.createFindUsagesHandler(element, operationMode)
//                if (handler === FindUsagesHandler.NULL_HANDLER) {
//                    return null
//                }
//                if (handler != null) {
//                    return handler
//                }
//            }
//        }
//        return null
//    }
    /**
     * 根据目标获取对应vf
     */
    fun getTargetVirtualFile(root: VirtualFile, target: String): VirtualFile? {
        for (item in root.children) {
            if (!item.isDirectory) {
                if (item.canonicalPath == target) {
                    return item
                }
            } else {
                val temp = getTargetVirtualFile(item, target)
                if (temp != null) {
                    return temp
                }
            }
        }
        return null

    }

    /**
     * 遍历源码目录，获取模块名称
     */
    fun ergodic(modules: LinkedHashMap<String, String>, vf: VirtualFile, project: Project, path: String) {
        if (vf.isDirectory) {
            vf.children.forEach {
                ergodic(modules, it, project, path)
            }
        } else {
            if (vf.isMoveFile) {
                val repath = vf.canonicalPath.toString().substring(path.length + 1)
                val moveFile = vf.toMoveFile(project)
                moveFile?.modules()?.forEachIndexed { index, module ->
                    val key =
                        ((module.addressRef?.namedAddress?.text + module.addressRef?.nextSibling?.text + module.addressRef?.nextSibling?.nextSibling?.text).trim())
                    val value = repath + "_" + index.toString()
                    modules[key] = value
                }

            }
        }
    }


}

private class CurrentResult(var dD: `dialogData`) : DialogWrapper(true) {
    init {
        init()
        title = "Current result json file Path"
    }

    override fun createCenterPanel(): JComponent {
        return panel {
            row("Path:") {
                val resultCheckBox = checkBox("Select Result:")
                    .gap(RightGap.SMALL)
                val textField = textFieldWithBrowseButton()
                    .bindText({ dD.JsonPath }, { dD.JsonPath = it }).enabledIf(resultCheckBox.selected)

            }
        }
    }

    data class dialogData(var JsonPath: String) {
    }
}

private class DefaultRunConfigurationFactory(val runManager: RunManager, val project: Project) {
    private val aptosProjectName = project.name.replace(' ', '_')

    fun createAptosBuildConfiguration(): RunnerAndConfigurationSettings =
        runManager.createConfiguration("Build", AptosCommandConfigurationType::class.java)
            .apply {
                (configuration as? AptosCommandConfiguration)?.apply {
                    command = "move compile"
                    workingDirectory = project.basePath?.toPath()
                }
            }
}

data class RSGNode(val resName: String, val function: MvFunction)
data class Ops(val op: String, val ress: List<String>)


/**
 * 路线算法
 *
 * @author ZJ
 */
class PathDfs(val graph: Array<Array<Int>>) {

    // visit数组，用于在dfs中记录访问过的顶点信息。
    var visit: IntArray? = null

    // 存储每条可能的路径
    var path: MutableList<Int> = mutableListOf()

    // 用于存储所有路径的集合
    var ans: MutableList<MutableList<Int>> = mutableListOf()

    /**
     * 寻找两点之间所有路线
     * @param start 起点
     * @param end 终点
     * @return
     */
    fun findAllPath(start: Int, end: Int): MutableList<MutableList<Int>> {
        ans = mutableListOf()


        // 顶点转换下标

        visit = IntArray(graph.size)

        dfs(start, end)
        return ans
    }

    /**
     *
     */
    private fun dfs(start: Int, end: Int) {
        visit!![start] = 1
        path.add(start)
        if (start == end) {
            ans.add(path.toMutableList())
        } else {
            for (i in graph.indices) {
                if (visit!![i] == 0 && i != start && graph!![start][i] == 1) {
                    dfs(i, end)
                }
            }
        }
        path.removeAt(path.size - 1)
        visit!![start] = 0
    }

    /**
     * 获取所有路线
     * @return
     */
    val allPath: MutableList<MutableList<Int>>
        get() = ans


}

data class funitem(val fname: String, var ops: MutableList<Ops>)

// data class Result(val arr: MutableList<ReItem>)
data class ResOps(val projectName: MutableList<String>, val projects: MutableList<pitem>) {
}

data class pitem(val pname: String, val Funs: MutableList<funitem>) {

}