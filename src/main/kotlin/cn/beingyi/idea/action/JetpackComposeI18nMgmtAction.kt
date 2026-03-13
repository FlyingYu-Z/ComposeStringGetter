package cn.beingyi.idea.action

import cn.beingyi.idea.dialog.ManagementDialog
import cn.beingyi.idea.dialog.YesNoDialog
import cn.beingyi.idea.manager.ProjectPool
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.icons.AllIcons
import com.intellij.openapi.ui.Messages

/**
 * author: zhengyu
 * date: 2021/8/6 15:53
 */
class JetpackComposeI18nMgmtAction : AnAction() {


    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val loadedProject=ProjectPool.getLoadedProjectOrCreate(project)
        val buildConfiguration = loadedProject.getProjectConfig()
        if(buildConfiguration==null){
            enableTip(e)
            return
        }
        if (ProjectPool.isProjectEnabled(e.project!!)) {
            ManagementDialog(e.project!!).show()
        } else {
            Messages.showErrorDialog("You didn't enable the config","Error")
        }
    }

    private fun enableTip(e: AnActionEvent) {
        val project = e.project ?: return
        val loadedProject = ProjectPool.getLoadedProjectOrCreate(project)
        YesNoDialog(e.project!!, "Tip", "Do you want to enable i18n?") {
            loadedProject.enableConfig(true)
            actionPerformed(e)
        }.show()
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val project = e.project ?: return
        //val loadedProject = ProjectPool.getLoadedProject(project)
        val enabled = ProjectPool.isProjectEnabled(project)
        if (enabled) {
            e.presentation.icon = AllIcons.Actions.Checked
        } else {
            e.presentation.icon = null
        }
    }

}