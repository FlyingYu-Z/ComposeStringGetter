package cn.beingyi.idea.task

import cn.beingyi.idea.model.LoadedProject
import com.intellij.openapi.project.Project

abstract class Task(val project: Project, protected val loadedProject: LoadedProject) {

    abstract fun execute()

}