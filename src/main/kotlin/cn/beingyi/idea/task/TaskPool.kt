package cn.beingyi.idea.task

import cn.beingyi.idea.model.LoadedProject
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import java.util.LinkedList
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

object TaskPool {

    private val pendingTask = ArrayBlockingQueue<BuildTask>(1)

    init {
        val app = ApplicationManager.getApplication()
        thread {
            while (true) {
                val task = pendingTask.take()
                WriteCommandAction.runWriteCommandAction(task.project) {
                    app.runWriteAction {
                        task.execute()
                    }
                }
                Thread.sleep(1000)
            }
        }
    }

    @Synchronized
    fun executeBuildTask(project: Project, loadedProject: LoadedProject) {
        val projectDir = project.basePath ?: return
        val buildTask = BuildTask(project, loadedProject)
        pendingTask.put(buildTask)
        return
    }


}