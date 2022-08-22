package cn.beingyi.idea.action

import cn.beingyi.idea.manager.ProjectSwitchManager
import cn.beingyi.idea.manager.mappingList
import cn.beingyi.idea.model.LanguageFile
import cn.beingyi.idea.model.ProjectConfig
import cn.beingyi.idea.utils.upperCaseFirst
import com.google.common.collect.Lists
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.vfs.VirtualFileManager
import com.squareup.kotlinpoet.*
import java.io.File

/**
 * author: zhengyu
 * date: 2021/8/6 15:53
 */
class JetpackComposeBuildI18nAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project == null) {
            return
        }
        //project?.save()
        //ApplicationManager.getApplication().saveAll()


        FileDocumentManager.getInstance().saveAllDocuments()
//        val projectInfo = ProjectSwitchManager.instance.getProjectInfo(project)
//        projectInfo?.checkGlobal()
//
//        val app = ApplicationManager.getApplication()
//        WriteCommandAction.runWriteCommandAction(project) {
//            app.runWriteAction {
//                createMain(projectInfo!!)
//                projectInfo?.getLangFileList()?.forEach {
//                    val langFile = it
//                    createObject(projectInfo,langFile)
//                }
//
//                DaemonCodeAnalyzer.getInstance(project).restart()
//            }
//        }
//        VirtualFileManager.getInstance().findFileByNioPath(projectInfo?.generatedDir?.toPath()!!)?.refresh(false,true)

        val buildConfiguration= ProjectSwitchManager.instance.getProjectConfig(project) ?: return
        val app = ApplicationManager.getApplication()
        WriteCommandAction.runWriteCommandAction(project) {
            app.runWriteAction {
                for(mapping in mappingList){
                    val langFile=LanguageFile(File(mapping.srcXmlPath),mapping.languageTag.upperCaseFirst())
                    if(mapping.defaultSrcXml){
                        createMain(buildConfiguration,langFile)
                    }
                    createObject(buildConfiguration,langFile)
                }
                DaemonCodeAnalyzer.getInstance(project).restart()
            }
        }
        VirtualFileManager.getInstance().findFileByNioPath(File(buildConfiguration.destKotlinPath).toPath()!!)?.refresh(false,true)

    }


    private fun createMain(buildConfiguration: ProjectConfig, langFile: LanguageFile) {
        try {
            val fields = ArrayList<PropertySpec>()
            langFile.getMap().forEach { key, value ->
                val field = PropertySpec.builder(key, String::class.asTypeName().copy(nullable = false))
                    .mutable(false)
                    .build()
                fields.add(field)
            }

            val stringsInterfaceName = ClassName(buildConfiguration.packageName, "Strings")
            val stringsInterface = TypeSpec.interfaceBuilder(stringsInterfaceName)
                .addProperties(fields)
                .build()

//            val strings = PropertySpec.builder("strings", stringsInterfaceName.copy(nullable = false))
//                .mutable(false)
//                .getter(
//                    FunSpec.getterBuilder()
//                        .addCode(
//                            "return when (Locale.getDefault().language) {\n" +
//                                    "            \"en\" -> EnStrings\n" +
//                                    "            \"zh\" -> ZhStrings\n" +
//                                    "            else -> EnStrings\n" +
//                                    "        }"
//                        )
//                        .build()
//                )
//                .build()

            val stringFile = FileSpec.builder(stringsInterfaceName.packageName, stringsInterfaceName.simpleName)
                .addImport("java.util", Lists.newArrayList("Locale"))
                //.addProperty(strings)
                .addType(stringsInterface)
                .build()
            stringFile.writeTo(File(buildConfiguration.destKotlinPath))
        }catch (e:Exception){
            e.printStackTrace()
        }

    }


    fun createObject(buildConfiguration: ProjectConfig, langFile: LanguageFile) {
        try {
            val packageName = buildConfiguration.packageName
            val stringsInterfaceName = ClassName(packageName, "Strings")

            val fields = ArrayList<PropertySpec>()
            langFile.getMap().forEach { key, value ->
                val field = PropertySpec.builder(key, String::class.asTypeName().copy(nullable = false))
                    .mutable(false)
                    .addModifiers(KModifier.OVERRIDE)
                    .initializer("%S", value)
                    .build()
                fields.add(field)
            }

            val stringsClassName = ClassName(packageName, langFile.prefixString + "Strings")

            val stringsClass = TypeSpec.objectBuilder(stringsClassName)
                .addSuperinterface(stringsInterfaceName)
                .addProperties(fields)
                .build()

            val stringFile = FileSpec.builder(packageName, stringsClassName.simpleName)
                .addImport("java.util", Lists.newArrayList("Locale"))
                .addType(stringsClass)
                .build()
            stringFile.writeTo(File(buildConfiguration.destKotlinPath))
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabled = ProjectSwitchManager.instance.isProjectEnabled(e.project!!)
    }

}