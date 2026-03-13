package cn.beingyi.idea.action

import cn.beingyi.idea.manager.ProjectPool
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.icons.AllIcons

/**
 * author: zhengyu
 * date: 2021/8/6 15:53
 */
class SwitchAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project!!
        val loadedProject = ProjectPool.getLoadedProjectOrCreate(project)
        val enabled = ProjectPool.isProjectEnabled(project)
        loadedProject.enableConfig(!enabled)
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        val project = e.project
        val enabled = ProjectPool.isProjectEnabled(project)
        if (enabled) {
            e.presentation.icon = AllIcons.Actions.Checked
        } else {
            e.presentation.icon = null
        }
    }
}