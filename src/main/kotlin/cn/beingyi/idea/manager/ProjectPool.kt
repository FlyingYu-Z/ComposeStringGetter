package cn.beingyi.idea.manager

import cn.beingyi.idea.model.LoadedProject
import com.intellij.openapi.project.Project
import java.util.concurrent.ConcurrentHashMap

/**
 * author: zhengyu
 * date: 2021/8/6 16:10
 *
 */

object ProjectPool {

    private val projectMap = ConcurrentHashMap<String, LoadedProject>()

    init {

    }

    fun getLoadedProjects():List<LoadedProject>{
        return projectMap.values.toList()
    }

    fun getLoadedProject(project: Project): LoadedProject? {
        return projectMap[project.basePath]
    }
    fun getLoadedProjectOrCreate(project: Project): LoadedProject {
        return projectMap[project.basePath] ?: LoadedProject(project).apply { projectMap[projectDir] = this }
    }

    fun isProjectEnabled(project: Project?): Boolean {
        if (project == null) {
            return false
        }
        return getLoadedProjectOrCreate(project).enabled()
    }


}


