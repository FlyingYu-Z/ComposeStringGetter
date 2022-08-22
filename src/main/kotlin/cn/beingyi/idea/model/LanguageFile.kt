package cn.beingyi.idea.model

import cn.beingyi.idea.helper.getContent
import org.apache.commons.io.FileUtils
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.DocumentHelper
import org.dom4j.Element
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.Throws

/**
 * author: zhengyu
 * date: 2021/8/6 16:57
 *
 */
class LanguageFile(file: File,prefixStr:String) {

    val langFile: File
    val prefixString:String

    init {
        langFile = file
        if (!langFile.exists()) {
            try {
                var document = DocumentHelper.createDocument()
                val rootElement = document.addElement("resources")
                FileUtils.writeStringToFile(langFile, getContent(document), StandardCharsets.UTF_8)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        prefixString=prefixStr

    }


    @Throws(DocumentException::class)
    @Synchronized
    fun getMap(): ConcurrentHashMap<String, String> {
        checkRepeatValue()
        val valueMap = ConcurrentHashMap<String, String>()
        val document = getDocument()
        val rootElement = document.getRootElement()
        val stringElements = rootElement.elements("string")
        if (stringElements != null && !stringElements.isEmpty()) {
            (stringElements as List<Element>).forEach { element ->
                val key = element.attributeValue("name")
                val value = element.text
                valueMap.put(key, value)
            }
            return valueMap
        } else {
            return ConcurrentHashMap<String, String>()
        }
    }

    @Synchronized
    fun checkValue(key: String, value: String) {
        try {
            val document = getDocument()
            val rootElement = document.getRootElement()
            val stringElements = rootElement.elements("string")
            var found = false
            if (stringElements != null && !stringElements.isEmpty()) {
                (stringElements as List<Element>).forEach { element ->
                    if (element.attributeValue("name").equals(key)) {
                        found = true
                    }
                }
            }
            if (!found) {
                val stringElement = rootElement.addElement("string")
                stringElement.addAttribute("name", key)
                stringElement.text = value
                FileUtils.writeStringToFile(langFile, getContent(document), StandardCharsets.UTF_8)
            }
            checkRepeatValue()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Synchronized
    fun removeKey(key: String) {
        try {
            val document = getDocument()
            val rootElement = document.getRootElement()
            val stringElements = rootElement.elements("string")
            if (stringElements != null && !stringElements.isEmpty()) {
                (stringElements as List<Element>).forEach { element ->
                    if (element.attributeValue("name").equals(key)) {
                        rootElement.remove(element)
                    }
                }
            }
            FileUtils.writeStringToFile(langFile, getContent(document), StandardCharsets.UTF_8)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkRepeatValue() {
        val valueMap = ConcurrentHashMap<String, String>()
        val document = getDocument()
        val rootElement = document.getRootElement()
        val stringElements = rootElement.elements("string")
        if (stringElements != null && !stringElements.isEmpty()) {
            (stringElements as List<Element>).forEach { element ->
                val key = element.attributeValue("name")
                val value = element.text
                if (!valueMap.containsKey(key)) {
                    valueMap.put(key, value)
                } else {
                    rootElement.remove(element)
                }
            }
            FileUtils.writeStringToFile(langFile, getContent(document), StandardCharsets.UTF_8)
        }
    }

    @Throws(DocumentException::class)
    private fun getDocument(): Document {
        var document = DocumentHelper.parseText(FileUtils.readFileToString(langFile, StandardCharsets.UTF_8))
        return document
    }

}