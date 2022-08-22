package cn.beingyi.idea.listeners

import cn.beingyi.idea.manager.ProjectSwitchManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener
import com.intellij.openapi.wm.impl.FocusManagerImpl
import java.awt.event.FocusEvent
import java.lang.reflect.Proxy

/**
 * author: zhengyu
 * date: 2021/8/1 16:24
 *
 */
class MyFocusChangeListener : FocusChangeListener {

    override fun focusGained(editor: Editor) {
//        val projectInfo=ProjectSwitchManager.instance.getProjectInfo(editor.project)
//        projectInfo?.checkGlobal()
    }

    override fun focusLost(editor: Editor, event: FocusEvent) {
        super.focusLost(editor, event)
    }
}