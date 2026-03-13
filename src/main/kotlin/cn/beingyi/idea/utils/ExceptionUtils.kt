package cn.beingyi.idea.utils

import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.io.PrintWriter
import java.io.StringWriter

object ExceptionUtils {
    fun getExceptionDetail(ex: Exception): String {
        val out = ByteArrayOutputStream()
        val pout = PrintStream(out)
        ex.printStackTrace(pout)
        val ret = String(out.toByteArray())
        pout.close()
        try {
            out.close()
        } catch (e: Exception) {
        }
        return ret
    }

    fun getDetail(e: Throwable): String {
        val sw = StringWriter()
        val pw = PrintWriter(sw, true)
        e.printStackTrace(pw)
        pw.flush()
        sw.flush()
        return sw.toString()
    }
}
