package cn.beingyi.idea.model

import cn.beingyi.idea.utils.upperCaseFirst
import com.intellij.ide.impl.ProjectUtil
import org.apache.commons.io.FileUtils
import org.dom4j.DocumentHelper
import org.dom4j.Element
import java.io.File
import java.lang.Exception
import java.nio.charset.StandardCharsets

/**
 * author: zhengyu
 * date: 2021/8/1 14:54
 */
class ProjectInfo(val basePath: String?) {
    val mainResourcesDir: File
    val mainStringsDir: File
    open val mainLanguageFile: LanguageFile
    val mainConfigFile: File
    val stringConfig: StringConfig
    open val generatedDir:File

    init {
        mainResourcesDir = File(basePath, "src/main/resources")
        mainStringsDir = File(mainResourcesDir, "strings")
        mainConfigFile = File(mainStringsDir, "stringConfig.json")
        if (!mainStringsDir.exists()) {
            mainStringsDir.mkdirs()
        }
        stringConfig = StringConfig(mainConfigFile)
        generatedDir= File(basePath,"build/generated/source/kaptKotlin/main/")

        if(!generatedDir.exists()){
            generatedDir.mkdirs()
        }

        val mainLangFile = File(mainStringsDir, "strings.xml")
        val initedMain = mainLangFile.exists()
        mainLanguageFile = LanguageFile(mainLangFile,"")

        if (!initedMain) {
            mainLanguageFile.checkValue("hello", "Hello")
            stringConfig.getLanguages()?.forEach { lang ->
                if (lang.equals("zh")) {
                    var langFile = LanguageFile(stringConfig.getFileByLang("zh"),"Zh")
                    langFile.checkValue("hello", "你好！")
                } else if (lang.equals("ru")) {
                    var langFile = LanguageFile(stringConfig.getFileByLang("ru"),"Ru")
                    langFile.checkValue("hello", "Здравствыйте")
                }
            }
        }
    }

    open fun getLangFileList():List<LanguageFile>{
        val list=ArrayList<LanguageFile>()
        stringConfig.getLanguages()?.forEach { lang ->
            var langFile = LanguageFile(stringConfig.getFileByLang(lang), lang.upperCaseFirst())
            list.add(langFile)
        }
        return list
    }

    @Synchronized
    open fun checkGlobal() {
        ProjectUtil.getFileAndRefresh(mainStringsDir.toPath())?.refresh(false, true)
        try {
            var document =
                DocumentHelper.parseText(FileUtils.readFileToString(mainLanguageFile.langFile, StandardCharsets.UTF_8))
            val rootElement = document.getRootElement()
            val stringElements = rootElement.elements("string")
            if (stringElements != null && !stringElements.isEmpty()) {
                (stringElements as List<Element>).forEach { element ->
                    val name = element.attributeValue("name")
                    stringConfig.getLanguages()?.forEach { lang ->
                        var langFile = LanguageFile(stringConfig.getFileByLang(lang), lang.upperCaseFirst())
                        langFile.checkValue(name, element.text)

                        langFile.getMap().forEach { key, value ->
                            if (!mainLanguageFile.getMap().containsKey(key)) {
                                langFile.removeKey(key)
                            }
                        }

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        ProjectUtil.getFileAndRefresh(mainStringsDir.toPath())?.refresh(false, true)
    }


}