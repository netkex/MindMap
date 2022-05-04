package com.github.netkex.mindmap

import androidx.compose.runtime.*
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


object Context {
    val plan: MutableState<MMPlan> = mutableStateOf( mutableListOf() )
}

class ComposeToolWindow : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Start Plugin")
        val content = ContentFactory.SERVICE.getInstance().createContent(
            PanelAction.createPanel(Context),
            "MindMap",
            true)

        toolWindow.contentManager.addContent(content, 0)
    }
}