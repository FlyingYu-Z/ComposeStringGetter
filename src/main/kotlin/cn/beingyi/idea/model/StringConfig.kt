package cn.beingyi.idea.model

import cn.beingyi.idea.helper.getContent
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.fastjson.parser.Feature
import org.apache.commons.io.FileUtils
import org.dom4j.DocumentHelper
import java.io.File
import java.nio.charset.StandardCharsets

/**
 * author: zhengyu
 * date: 2021/8/1 17:09
 *
 */
class StringConfig(val configFile: File) {

    init {
        if(!configFile.exists()){
            initConfig()
        }
    }

    private fun initConfig(){
        val json=JSONObject(true)
        val langArray=JSONArray()
        langArray.add("zh")
        langArray.add("ru")
        json.put("languages",langArray)
        val str=JSONObject.toJSONString(json,true)
        FileUtils.writeStringToFile(configFile,str,StandardCharsets.UTF_8)
    }

    private fun getConfigJson():JSONObject?{
        try {
            val str = FileUtils.readFileToString(configFile, StandardCharsets.UTF_8)
            val json = JSON.parseObject(str, Feature.OrderedField)
            return json
        }catch (e:Exception){
            e.printStackTrace()
        }
        return null
    }


    fun getLanguages():List<String>?{
        val list=getConfigJson()?.getJSONArray("languages")?.toJavaList(String::class.java)
        return list
    }

    fun getFileByLang(lang:String):File{
        val file=File(configFile.parent,"strings-${lang}.xml")
        try {
            if (file.exists()) {
                return file
            }
            var document = DocumentHelper.createDocument()
            val rootElement = document.addElement("resources")
            FileUtils.writeStringToFile(file, getContent(document), StandardCharsets.UTF_8)
        }catch (e:Exception){
            e.printStackTrace()
        }
        return file
    }

}