package cn.beingyi.idea.listeners

import cn.beingyi.idea.manager.ProjectPool
import cn.beingyi.idea.model.ProjectConfig
import cn.beingyi.idea.task.BuildTask
import cn.beingyi.idea.task.TaskPool
import cn.beingyi.idea.utils.ExceptionUtils
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.ProjectLocator
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.isFile
import com.intellij.util.ExceptionUtil
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


class MyFileDocumentListener : FileDocumentManagerListener {

//    override fun beforeDocumentSaving(document: Document) {
//        super.beforeDocumentSaving(document)
//        try {
//            val file = FileDocumentManager.getInstance().getFile(document) ?: return
//            if (!file.isFile) return
//            val project = ProjectLocator.getInstance().guessProjectForFile(file) ?: return
//            val loadedProject = ProjectPool.getLoadedProjectOrCreate(project) ?: return
//            if (!loadedProject.enabled()) return
//            synchronized(MyFileDocumentListener::class.java) {
//                val projectConfig: ProjectConfig = loadedProject.getProjectConfig() ?: return
//                with(projectConfig) {
//                    val mainLanguageFilePath = toProjectFile(projectConfig.mainStringXmlFile)
//                    if (file.path == mainLanguageFilePath.absolutePath) {
//                        TaskPool.executeBuildTask(project, loadedProject)
//                    }
//                    projectConfig.mappings.forEach { mappingBean ->
//                        val stringXmlFile = toProjectFile(mappingBean.stringXmlFile)
//                        if (file.path == stringXmlFile.absolutePath) {
//                            TaskPool.executeBuildTask(project, loadedProject)
//                        }
//                    }
//                }
//            }
//        } catch (e: Throwable) {
//            Messages.showErrorDialog(ExceptionUtils.getDetail(e), "Error")
//        }
//    }


}