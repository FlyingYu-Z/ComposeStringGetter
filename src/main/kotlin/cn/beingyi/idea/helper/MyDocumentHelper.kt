package cn.beingyi.idea.helper

import org.dom4j.Document
import org.dom4j.DocumentHelper
import org.dom4j.io.OutputFormat
import org.dom4j.io.XMLWriter
import java.io.StringWriter

/**
 * author: zhengyu
 * date: 2021/8/1 17:01
 *
 */


inline fun getContent(document:Document):String{
    val format = OutputFormat.createPrettyPrint()
    val stringWriter = StringWriter()
    val xmlWriter = XMLWriter(stringWriter, format)
    xmlWriter.write(document)
    xmlWriter.close()
    val result = stringWriter.toString()
    return result
}