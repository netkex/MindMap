package com.github.netkex.mindmap

import androidx.compose.runtime.*
import com.github.netkex.mindmap.map.*
import com.github.netkex.mindmap.UI.WindowAction
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


object Context {
    var plan by mutableStateOf( listOf<MMIdea>() )
}

class ComposeToolWindow : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Start Plugin")
        val mainIdea = MMIdea("Main idea", 0.5f, 0.5f)
        val plan = listOf( mainIdea )
        Context.plan = plan
        val content = ContentFactory.SERVICE.getInstance().createContent(
            WindowAction.createPanel(Context),
            "MindMap",
            true)
        toolWindow.contentManager.addContent(content, 0)
    }
}