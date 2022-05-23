package com.github.netkex.mindmap

import com.github.netkex.mindmap.UI.WindowAction
import com.github.netkex.mindmap.common.initMap
import com.github.netkex.mindmap.map.*
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class ComposeToolWindow : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Start Plugin")
        Context.plan.replaceMap(initMap)
        val content = ContentFactory.SERVICE.getInstance().createContent(
            WindowAction.createPanel(Context),
            "MindMap",
            true)
        toolWindow.contentManager.addContent(content, 0)
    }
}