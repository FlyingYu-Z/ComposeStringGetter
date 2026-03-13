package cn.beingyi.idea.model

import cn.beingyi.idea.helper.getContent
import org.dom4j.Document
import org.dom4j.DocumentException
import org.dom4j.DocumentHelper
import org.dom4j.Element
import java.io.File
import java.io.IOException
import java.util.LinkedList
import java.util.concurrent.ConcurrentHashMap
import kotlin.Exception
import kotlin.jvm.Throws

/**
 * author: zhengyu
 * date: 2021/8/6 16:57
 *
 */
class LanguageFile(
    val loadedProject: LoadedProject,
    val langFile: File,
    val languageTag: String,
    val classPrefix: String
) {
    init {
        synchronized(LanguageFile::class) {
            if (!langFile.exists()) {
                try {
                    var document = DocumentHelper.createDocument()
                    val rootElement = document.addElement("resources")
                    loadedProject.write(langFile, getContent(document))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
    @Synchronized
    fun isContentValid(): Boolean {
        try {
            getDocument()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    @Synchronized
    fun getMap(): ConcurrentHashMap<String, String> {
        checkRepeatValue()
        val valueMap = ConcurrentHashMap<String, String>()
        val document = getDocument()
        val rootElement = document.rootElement
        val stringElements = rootElement.elements("string")
        stringElements.forEach { stringElement ->
            val keyName = stringElement.attributeValue("name")
            val value = stringElement.text
            valueMap[keyName] = value
        }
        return valueMap
    }

    fun getSortedKeys(): List<String>? {
        try {
            val document = getDocument()
            val rootElement = document.rootElement
            val stringElements = rootElement.elements("string")
            val list = LinkedList<String>()
            stringElements.forEach { stringElement ->
                val keyName = stringElement.attributeValue("name")
                list.add(keyName)
            }
            return list
        } catch (e: Exception) {
            return null
        }
    }

    @Synchronized
    fun checkValue(key: String, value: String) {
        try {
            val document = getDocument()
            val rootElement = document.rootElement
            val stringElements = rootElement.elements("string")
            var found = false
            stringElements.forEach { element ->
                if (element.attributeValue("name").equals(key)) {
                    found = true
                }
            }
            if (!found) {
                val stringElement = rootElement.addElement("string")!!
                stringElement.addAttribute("name", key)
                stringElement.text = value
                loadedProject.write(langFile, getContent(document))
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
            val rootElement = document.rootElement
            val stringElements = rootElement.elements("string")
            if (stringElements != null && !stringElements.isEmpty()) {
                (stringElements as List<Element>).forEach { element ->
                    if (element.attributeValue("name").equals(key)) {
                        rootElement.remove(element)
                    }
                }
            }
            loadedProject.write(langFile, getContent(document))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

//    @Synchronized
//    fun removeNotExistMain(mainKeys: List<String>) {
//        try {
//            val document = getDocument()
//            val rootElement = document.rootElement
//            val stringElements = rootElement.elements("string")
//            stringElements.forEach { stringElement ->
//                val keyName = stringElement.attributeValue("name")
//                if (!mainKeys.contains(keyName)) {
//                    rootElement.remove(stringElement)
//                }
//            }
//            loadedProject.write(langFile, getContent(document))
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    @Synchronized
    fun sortWithMain(mainKeys: List<String>) {
        try {
            val document = getDocument()
            val rootElement = document.rootElement
            val stringElements = rootElement.elements("string").toList()
            val stringElementMap = mutableMapOf<String, Element>()
            stringElements.forEach { stringElement ->
                rootElement.remove(stringElement)
                val keyName = stringElement.attributeValue("name")
                stringElementMap[keyName] = stringElement
            }
            mainKeys.forEach { keyName ->
                rootElement.add(stringElementMap[keyName])
            }
            loadedProject.write(langFile, getContent(document))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Synchronized
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
            loadedProject.write(langFile, getContent(document))
        }
    }

    private fun getDocument(): Document {
        var document = DocumentHelper.parseText(loadedProject.read(langFile))
        return document
    }

}