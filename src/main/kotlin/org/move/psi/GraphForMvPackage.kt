package org.move.psi

import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.roots.ModuleRootManager
import org.move.cli.Consts
import org.move.cli.MovePackage
import org.move.cli.MoveProject
import org.move.cli.RawAddressMap
import org.move.cli.manifest.MoveToml
import org.move.cli.manifest.TomlDependency
import org.move.lang.toNioPathOrNull
import org.move.lang.toTomlFile
import org.move.openapiext.modules
import org.move.stdext.iterateFiles

class GraphForMvPackage {
    fun gerResult(map: LinkedHashMap<String, List<TomlDependency>>){
        val arr=Array<Array<Int>>(map.size){Array(map.size){0}}
       map.toList().forEachIndexed { index, (key, value) ->
           value.forEach {
               val index2=map.keys.indexOf(it.name)
               if(index2!=-1){
                   arr[index][index2]=1
               }
           }
       }
        for (i in 0 until map.size) {
            for (j in 0 until map.size) {
                if (arr[i][j] == 1) {
                    println("$i $j")
                }
            }
        }

       }
    fun buildGraph(
        toml: MoveToml, currProject: String,
        map:LinkedHashMap <String, List<TomlDependency>>,
    ) {
        val deps = toml.deps

        if (deps.isNotEmpty()) {
            map.put(currProject, deps.map { it.first })
            deps.forEach {
                fromTdTotoml(it.first)?.let { it1 -> buildGraph(it1, it.first.name, map) }
            }
        } else {
            map.put(currProject, emptyList())
            return
        }

    }

    fun fromTdTotoml(td: TomlDependency): MoveToml? {
        val path = td.localPath()
        val loadProject = ProjectManagerEx.getInstanceEx().loadProject(path)

        val contentRoots = loadProject.modules.toList().asSequence()
            .flatMap { ModuleRootManager.getInstance(it).contentRoots.asSequence() }
        val projects = mutableListOf<MoveProject>()
        val tomls = mutableListOf<MoveToml>()
        for (contentRoot in contentRoots) {
            contentRoot.iterateFiles({ it.name == Consts.MANIFEST_FILE }) {
                val rawDepQueue = ArrayDeque<Pair<TomlDependency, RawAddressMap>>()
                val root = it.parent?.toNioPathOrNull() ?: return@iterateFiles true
                val tomlFile = it.toTomlFile(loadProject) ?: return@iterateFiles true
                val moveToml = MoveToml.fromTomlFile(tomlFile, root)
                tomls.add(moveToml)
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
        return if (projects.size == 1) {

            tomls.first()
        } else
            null
    }
    // data class node(var name:String, var dependencies:List<TomlDependency>)


}