package cn.beingyi.idea.listeners

import cn.beingyi.idea.manager.ProjectSwitchManager
import cn.beingyi.idea.service.MyProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.project.ProjectManagerListener

class MyProjectManagerListener : ProjectManagerListener {

    override fun projectOpened(project: Project) {
        project.service<MyProjectService>()


    }

    override fun projectClosed(project: Project) {
        super.projectClosed(project)
        //ProjectSwitchManager.instance.setProjectEnabled(project,false)
    }
}