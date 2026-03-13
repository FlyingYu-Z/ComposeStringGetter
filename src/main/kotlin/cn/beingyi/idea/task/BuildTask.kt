package cn.beingyi.idea.task

import cn.beingyi.idea.model.LanguageFile
import cn.beingyi.idea.model.LoadedProject
import cn.beingyi.idea.model.ProjectConfig
import cn.beingyi.idea.utils.upperCaseFirst
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.util.ExceptionUtil
import com.squareup.kotlinpoet.*
import java.io.File
import java.io.StringWriter

class BuildTask(project: Project, loadedProject: LoadedProject) : Task(project, loadedProject) {

    @Synchronized
    override fun execute() {
        try {
            val projectConfig = loadedProject.getProjectConfig() ?: return
            if (!loadedProject.enabled()) {
                Messages.showErrorDialog("i18n disabled", "Error")
                return
            }
            //FileDocumentManager.getInstance().saveAllDocuments()
            checkValueWithMain(loadedProject)
            createFieldNames(loadedProject)

            createStringsInterface(projectConfig, loadedProject.getMainLanguageFile())
            createStringsImpl(loadedProject, projectConfig, loadedProject.getMainLanguageFile())
            for (mapping in projectConfig.mappings) {
                val langFile = LanguageFile(
                    loadedProject,
                    File(loadedProject.projectDir, mapping.stringXmlFile),
                    mapping.languageTag,
                    mapping.languageTag.upperCaseFirst()
                )
                createStringsImpl(loadedProject, projectConfig, langFile)
            }
            DaemonCodeAnalyzer.getInstance(project).restart()
            VirtualFileManager.getInstance().findFileByNioPath(File(projectConfig.destKotlinPath).toPath()!!)
                ?.refresh(false, true)
        } catch (e: Throwable) {
            Messages.showErrorDialog(ExceptionUtil.getMessage(e), "Error")
        }
    }


    @Synchronized
    private fun checkValueWithMain(loadedProject: LoadedProject) {
        val projectConfig = loadedProject.getProjectConfig() ?: return
        for (mapping in projectConfig.mappings) {
            val langFile = LanguageFile(
                loadedProject,
                File(loadedProject.projectDir, mapping.stringXmlFile),
                mapping.languageTag,
                mapping.languageTag.upperCaseFirst()
            )
            loadedProject.getMainLanguageFile().getMap().forEach { (key, value) ->
                langFile.checkValue(key, value)
            }
            loadedProject.getMainLanguageFile().getSortedKeys()?.apply {
                langFile.sortWithMain(this)
            }
        }
    }

    private fun createStringsInterface(projectConfig: ProjectConfig, langFile: LanguageFile) {
        try {
            val fields = ArrayList<PropertySpec>()
            langFile.getMap().forEach { key, value ->
                val field = PropertySpec.builder(key, String::class.asTypeName().copy(nullable = false))
                    .mutable(false)
                    .build()
                fields.add(field)
            }

            val stringsInterfaceName = ClassName(projectConfig.packageName, "Strings")
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
                //.addImport("java.util", Lists.newArrayList("Locale"))
                //.addProperty(strings)
                .addType(stringsInterface)
                .build()
            val stringWriter = StringWriter()
            stringFile.writeTo(stringWriter)
            val ktDir=File(loadedProject.projectDir, projectConfig.destKotlinPath)
            val ktFile=File(ktDir,"${projectConfig.packageName.replace(".",File.separator)}/${stringsInterfaceName.simpleName}.kt")
            loadedProject.write(ktFile, stringWriter.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    private fun createStringsImpl(loadedProject: LoadedProject, projectConfig: ProjectConfig, langFile: LanguageFile) {
        try {
            val packageName = projectConfig.packageName
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

            val stringsClassName = ClassName(packageName, langFile.classPrefix + "Strings")

            val stringsClass = TypeSpec.objectBuilder(stringsClassName)
                .addSuperinterface(stringsInterfaceName)
                .addProperties(fields)
                .build()

            val stringFile = FileSpec.builder(packageName, stringsClassName.simpleName)
                //.addImport("java.util", Lists.newArrayList("Locale"))
                .addType(stringsClass)
                .build()
            val stringWriter = StringWriter()
            stringFile.writeTo(stringWriter)
            val ktDir=File(loadedProject.projectDir, projectConfig.destKotlinPath)
            val ktFile=File(ktDir,"${projectConfig.packageName.replace(".",File.separator)}/${stringsClassName.simpleName}.kt")
            this.loadedProject.write(ktFile, stringWriter.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
    private fun createFieldNames(loadedProject: LoadedProject) {
        try {
            val projectConfig = loadedProject.getProjectConfig() ?: return
            val packageName = projectConfig.packageName
            val className = ClassName(packageName, "FieldNames")

            val fields = ArrayList<PropertySpec>()
            loadedProject.getMainLanguageFile().getMap().forEach { (key, value) ->
                val field = PropertySpec.builder(key, String::class.asTypeName().copy(nullable = false))
                    .mutable(false)
                    .addModifiers(KModifier.CONST)
                    .initializer("%S", key)
                    .build()
                fields.add(field)
            }
            val fieldNamesClass = TypeSpec.objectBuilder(className)
                .addProperties(fields)
                .build()
            val stringFile = FileSpec.builder(packageName, className.simpleName)
                .addType(fieldNamesClass)
                .build()
            val stringWriter = StringWriter()
            stringFile.writeTo(stringWriter)
            val ktDir=File(loadedProject.projectDir, projectConfig.destKotlinPath)
            val ktFile=File(ktDir,"${projectConfig.packageName.replace(".",File.separator)}/${className.simpleName}.kt")
            loadedProject.write(ktFile, stringWriter.toString())
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


}