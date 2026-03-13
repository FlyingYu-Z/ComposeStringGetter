package cn.beingyi.idea.model

import com.alibaba.fastjson.JSON
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.openapi.vfs.writeText
import org.apache.commons.io.FileUtils
import java.io.File
import java.nio.charset.StandardCharsets


class LoadedProject(private val project: Project) {

    val projectDir: String = project.basePath!!

    private val configFile = File(projectDir, configFileName)

    private var mainLanguageFile: LanguageFile? = null

    @Synchronized
    fun enabled(): Boolean {
        val projectConfig = getProjectConfig() ?: return false
        return projectConfig.enabled
    }

    @Synchronized
    fun enableConfig(enabled: Boolean) {
        if (enabled) {
            try {
                val content = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8)
                JSON.parseObject(content, ProjectConfig::class.java)
            } catch (e: Exception) {
                val projectConfig = ProjectConfig()
                projectConfig.mappings.add(MappingBean())
                write(configFile, JSON.toJSONString(projectConfig))
            }
            val projectConfig = getProjectConfig()!!
            projectConfig.enabled = enabled
            write(configFile, JSON.toJSONString(projectConfig))
        } else {
            getProjectConfig()?.apply {
                this.enabled = enabled
                write(configFile, JSON.toJSONString(this))
            }
        }
    }

    fun getProjectConfig(): ProjectConfig? {
        return ProjectConfig.readFromFile(configFile)
    }

    @Synchronized
    fun setBuildConfiguration(destKotlinPath: String, packageName: String) {
        if (!enabled()) {
            return
        }
        val projectConfig = getProjectConfig() ?: return
        projectConfig.destKotlinPath = destKotlinPath
        projectConfig.packageName = packageName
        write(configFile, JSON.toJSONString(projectConfig))
    }

    @Synchronized
    fun isExistMapping(mappingBean: MappingBean): Boolean {
        if (!enabled()) {
            return false
        }
        val projectConfig = getProjectConfig() ?: return false
        for (bean in projectConfig.mappings) {
            if (bean.languageTag == mappingBean.languageTag) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun putMapping(mappingBean: MappingBean) {
        if (!enabled()) {
            return
        }
        val projectConfig = getProjectConfig()!!
        projectConfig.mappings.add(mappingBean)
        write(configFile, JSON.toJSONString(projectConfig))
    }

    @Synchronized
    fun removeMapping(mappingBean: MappingBean) {
        if (!enabled()) {
            return
        }
        val projectConfig = getProjectConfig() ?: return
        for (bean in projectConfig.mappings) {
            if (bean.languageTag == mappingBean.languageTag) {
                projectConfig.mappings.remove(bean)
                break
            }
        }
        write(configFile, JSON.toJSONString(projectConfig))
    }

    @Synchronized
    fun getMainLanguageFile(): LanguageFile {
        if (mainLanguageFile != null) {
            return mainLanguageFile!!
        }
        val projectConfig = getProjectConfig() ?: throw RuntimeException("project config not initialized")
        mainLanguageFile = LanguageFile(this, File(projectDir, projectConfig.mainStringXmlFile), "default", "Default")
        return mainLanguageFile!!
    }

    @Synchronized
    fun write(file: File, content: String) {
        if (project == null) {
            return
        }
        var virtualFile = VirtualFileManager.getInstance().findFileByNioPath(file.toPath())
        if (virtualFile == null) {
            file.createNewFile()
            virtualFile = VirtualFileManager.getInstance().refreshAndFindFileByNioPath(file.toPath())
        }
        val document = FileDocumentManager.getInstance().getDocument(virtualFile!!)!!
        document.setText(content)
    }
//    @Synchronized
//    fun write(file: File, content: String) {
//        if (project == null) {
//            return
//        }
//        val app = ApplicationManager.getApplication()
//        WriteCommandAction.runWriteCommandAction(project) {
//            app.runWriteAction {
//                FileDocumentManager.getInstance()
//                    .getDocument(VirtualFileManager.getInstance().findFileByNioPath(file.toPath())!!)
//                    ?.setText(content)
//            }
//        }
//    }

    @Synchronized
    fun read(file: File): String? {
        if (project == null) {
            return null
        }
        return FileDocumentManager.getInstance()
            .getDocument(VirtualFileManager.getInstance().findFileByNioPath(file.toPath())!!)
            ?.getText()
    }

    companion object {
        const val configFileName = "i18n.json"

    }
}