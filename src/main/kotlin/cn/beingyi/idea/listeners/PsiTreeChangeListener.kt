package cn.beingyi.idea.listeners

import com.intellij.psi.PsiTreeChangeEvent
import com.intellij.psi.PsiTreeChangeListener
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.File

/**
 * author: zhengyu
 * date: 2021/8/8 14
 */

val psiTreeChangeListener = object : PsiTreeChangeListener {
    override fun beforeChildAddition(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun beforeChildRemoval(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun beforeChildReplacement(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun beforeChildMovement(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun beforeChildrenChange(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun beforePropertyChange(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun childAdded(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun childRemoved(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun childReplaced(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun childrenChanged(event: PsiTreeChangeEvent) {
//        val file=event.file?.virtualFile
//        val content=IOUtils.toString(file?.inputStream,file?.charset)
//        val content2=FileUtils.readFileToString(File(file?.path),file?.charset)
//        System.out.println()
    }

    override fun childMoved(event: PsiTreeChangeEvent) {
        TODO("Not yet implemented")
    }

    override fun propertyChanged(event: PsiTreeChangeEvent) {
    }

}