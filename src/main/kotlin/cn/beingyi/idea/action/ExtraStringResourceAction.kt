package cn.beingyi.idea.action

import cn.beingyi.idea.dialog.StringPropertyDialog
import cn.beingyi.idea.manager.ProjectSwitchManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.Notifications
import com.intellij.openapi.ui.MessageType
import com.intellij.openapi.util.TextRange

/**
 * author: zhengyu
 * date: 2021/8/1 14:19
 */
class ExtraStringResourceAction : AnAction() {

    override fun beforeActionPerformedUpdate(e: AnActionEvent) {
        super.beforeActionPerformedUpdate(e)

    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getData(PlatformDataKeys.EDITOR)
        val project = e.getData(PlatformDataKeys.PROJECT)
        if (editor == null || project == null) {
            return
        }
        StringPropertyDialog(e.project,editor).show()


    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val switchEnable=ProjectSwitchManager.instance.isProjectEnabled(e.project!!)

        val editor = e.getData(PlatformDataKeys.EDITOR)
        val project = e.getData(PlatformDataKeys.PROJECT)
        if (editor == null || project == null) {
            return
        }
        val selectionModel = editor.selectionModel
        val document = editor.document
        val leftStr=document.getText(TextRange(selectionModel.selectionStart-1,selectionModel.selectionStart))
        val rightStr=document.getText(TextRange(selectionModel.selectionEnd,selectionModel.selectionEnd+1))

        val isStr=leftStr.equals("\"") && rightStr.equals("\"")

        e.presentation.isEnabled=switchEnable && isStr
        
    }

//    private fun showNotice(msg: String) {
//        val notificationGroup = NotificationGroup("testid", NotificationDisplayType.BALLOON, false)
//        val notification = notificationGroup.createNotification(msg, MessageType.INFO)
//        Notifications.Bus.notify(notification)
//    }
}