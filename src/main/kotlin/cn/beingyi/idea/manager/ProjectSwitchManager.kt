package cn.beingyi.idea.manager

import androidx.compose.runtime.mutableStateListOf
import cn.beingyi.idea.model.MappingBean
import cn.beingyi.idea.model.ProjectConfig
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.lang.Exception
import java.nio.charset.StandardCharsets

/**
 * author: zhengyu
 * date: 2021/8/6 16:10
 *
 */

val mappingList = mutableStateListOf<MappingBean>()

class ProjectSwitchManager {

    private val ConfigFileName = "i18n.json"

    init {

    }

    fun getProjectConfig(project: Project?): ProjectConfig? {
        if (project == null) {
            return null
        }
        val configFile = File(project.basePath, ConfigFileName)
        val projectConfig = getProjectConfig(project, configFile)
        return projectConfig
    }

    @Synchronized
    private fun getProjectConfig(project: Project, configFile: File): ProjectConfig {
        try {
            val content = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8)
            val projectConfig = JSON.parseObject(content, ProjectConfig::class.java)
            if (projectConfig == null) {
                val projectConfig = ProjectConfig()
                write(project, JSON.toJSONString(projectConfig), configFile)
                return projectConfig
            }
            return projectConfig
        } catch (e: Exception) {
            e.printStackTrace()
            val projectConfig = ProjectConfig()
            write(project, JSON.toJSONString(projectConfig), configFile)
            return projectConfig
        }
    }

    @Synchronized
    private fun write(project: Project?, content: String, configFile: File) {
        if (project == null) {
            return
        }
        val app = ApplicationManager.getApplication()
        WriteCommandAction.runWriteCommandAction(project) {
            app.runWriteAction {
                FileUtils.writeStringToFile(configFile, content, StandardCharsets.UTF_8)
                ProjectUtil.getFileAndRefresh(configFile.toPath())?.refresh(false, true)
            }
        }
    }

    fun setProjectEnabled(project: Project?, enabled: Boolean) {
        if (project == null) {
            return
        }
        val configFile = File(project.basePath, ConfigFileName)
        val projectConfig = getProjectConfig(project, configFile)
        projectConfig.enabled = enabled
        write(project, JSON.toJSONString(projectConfig), configFile)
    }


    fun setBuildConfiguration(project: Project?, destKotlinPath: String, packageName: String) {
        if (project == null) {
            return
        }
        val configFile = File(project.basePath, ConfigFileName)
        val projectConfig = getProjectConfig(project, configFile)
        projectConfig.destKotlinPath = destKotlinPath
        projectConfig.packageName = packageName
        write(project, JSON.toJSONString(projectConfig), configFile)
    }

    fun hasBuildConfiguration(project: Project?): Boolean {
        if (project == null) {
            return false
        }
        val configFile = File(project.basePath, ConfigFileName)
        val projectConfig = getProjectConfig(project, configFile)
        return !StringUtils.isAnyEmpty(projectConfig.destKotlinPath, projectConfig.packageName)
    }

    fun isProjectEnabled(project: Project?): Boolean {
        if (project == null) {
            return false
        }
        val configFile = File(project.basePath, ConfigFileName)
        if (!configFile.exists()) {
            return false
        }
        return getProjectConfig(project, configFile).enabled
    }

    @Synchronized
    fun isExistMapping(project: Project?, mappingBean: MappingBean): Boolean {
        if (project == null) {
            return false
        }
        val configFile = File(project.basePath, ConfigFileName)
        val projectConfig = getProjectConfig(project, configFile)
        for (bean in projectConfig.mappingList) {
            if (bean.languageTag == mappingBean.languageTag || (bean.defaultSrcXml && mappingBean.defaultSrcXml)) {
                return true
            }
        }
        return false
    }

    @Synchronized
    fun removeMapping(project: Project?, mappingBean: MappingBean) {
        if (project == null) {
            return
        }
        val configFile = File(project.basePath, ConfigFileName)
        val projectConfig = getProjectConfig(project, configFile)
        for (bean in projectConfig.mappingList) {
            if (bean.languageTag == mappingBean.languageTag) {
                projectConfig.mappingList.remove(bean)
                break
            }
        }
        write(project, JSON.toJSONString(projectConfig), configFile)
    }

    @Synchronized
    fun putMapping(project: Project?, mappingBean: MappingBean) {
        if (project == null) {
            return
        }
        val configFile = File(project.basePath, ConfigFileName)
        val projectConfig = getProjectConfig(project, configFile)
        projectConfig.mappingList.add(mappingBean)
        write(project, JSON.toJSONString(projectConfig), configFile)
    }

    @Synchronized
    fun refreshMappingList(project: Project?) {
        if (project == null) {
            return
        }
        if (!mappingList.isEmpty()) {
            mappingList.clear()
        }
        val configFile = File(project.basePath, ConfigFileName)
        val projectConfig = getProjectConfig(project, configFile)
        for (bean in projectConfig.mappingList) {
            mappingList.add(bean)
        }
    }

    companion object {
        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { ProjectSwitchManager() }

    }

}


