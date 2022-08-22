package cn.beingyi.idea.action

import cn.beingyi.idea.dialog.ManagementDialog
import cn.beingyi.idea.dialog.YesNoDialog
import cn.beingyi.idea.manager.ProjectSwitchManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.icons.AllIcons

/**
 * author: zhengyu
 * date: 2021/8/6 15:53
 */
class JetpackComposeI18nMgmtAction : AnAction() {


    override fun actionPerformed(e: AnActionEvent) {
        val project= e.project ?: return
        ProjectSwitchManager.instance.refreshMappingList(project)
        if(ProjectSwitchManager.instance.isProjectEnabled(e.project!!)) {
            ManagementDialog(e.project!!).show()
        }else{
            enableTip(e)
        }
    }

    private fun enableTip(e: AnActionEvent){
        YesNoDialog(e.project!!,"Tip","Do you want to enable i18n?"){
            ProjectSwitchManager.instance.setProjectEnabled(e.project!!,true)
            actionPerformed(e)
        }.show()
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val project = e.project
        val enabled = ProjectSwitchManager.instance.isProjectEnabled(project!!)
        if (enabled) {
            e.presentation.icon = AllIcons.Actions.Checked
        } else {
            e.presentation.icon= null
        }
    }

}