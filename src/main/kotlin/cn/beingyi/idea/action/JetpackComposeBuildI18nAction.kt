package cn.beingyi.idea.action

import cn.beingyi.idea.manager.ProjectPool
import cn.beingyi.idea.model.LanguageFile
import cn.beingyi.idea.model.LoadedProject
import cn.beingyi.idea.model.ProjectConfig
import cn.beingyi.idea.task.BuildTask
import cn.beingyi.idea.task.TaskPool
import cn.beingyi.idea.utils.upperCaseFirst
import com.google.common.collect.Lists
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager
import com.squareup.kotlinpoet.*
import java.io.File

/**
 * author: zhengyu
 * date: 2021/8/6 15:53
 */
class JetpackComposeBuildI18nAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val loadedProject = ProjectPool.getLoadedProjectOrCreate(project)

        TaskPool.executeBuildTask(project, loadedProject)
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabled = ProjectPool.isProjectEnabled(e.project!!)
    }

}