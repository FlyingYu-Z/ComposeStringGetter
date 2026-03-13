package cn.beingyi.idea.model

import com.alibaba.fastjson.JSON
import com.intellij.openapi.ui.Messages
import org.apache.commons.io.FileUtils
import java.io.File
import java.lang.Exception
import java.nio.charset.StandardCharsets

/**
 * author: zhengyu
 * date: 2021/9/27 20:34
 *
 */
class ProjectConfig {
    constructor()

    var enabled = false
    var mainStringXmlFile = ""
    var destKotlinPath = ""
    var packageName = ""

    val mappings = ArrayList<MappingBean>()

    companion object {
        fun readFromFile(file: File): ProjectConfig? {
            try {
                val content = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
                return JSON.parseObject(content, ProjectConfig::class.java)
            } catch (e: Exception) {
                return null
            }
        }
    }

}