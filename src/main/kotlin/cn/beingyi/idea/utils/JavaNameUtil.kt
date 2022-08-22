package cn.beingyi.idea.utils

/**
 * author: zhengyu
 * date: 2021/9/29 12:59
 *
 */
object JavaNameUtil {


    fun isValidJavaIdentifier(className: String): Boolean {
        //确定是否允许将指定字符作为 Java 标识符中的首字符。
        if (className.length == 0
            || !Character.isJavaIdentifierStart(className[0])
        ) return false
        val name = className.substring(1)
        for (i in 0 until name.length)  //确定指定字符是否可以是 Java 标识符中首字符以外的部分。
            if (!Character.isJavaIdentifierPart(name[i])) return false
        return true
    }

    fun isValidJavaFullClassName(fullName: String): Boolean {
        if (fullName == "") {
            return false
        }
        var flag = true
        try {
            if (!fullName.endsWith(".")) {
                val index = fullName.indexOf(".")
                if (index != -1) {
                    val str = fullName.split(".").toTypedArray()
                    for (name in str) {
                        if (name == "") {
                            flag = false
                            break
                        } else if (!isValidJavaIdentifier(name)) {
                            flag = false
                            break
                        }
                    }
                } else if (!isValidJavaIdentifier(fullName)) {
                    flag = false
                }
            } else {
                flag = false
            }
        } catch (ex: Exception) {
            flag = false
            ex.printStackTrace()
        }
        return flag
    }

}