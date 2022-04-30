package com.github.netkex.mindmap

import androidx.compose.runtime.*
import androidx.compose.ui.awt.ComposePanel
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.awt.Dimension


class Context {
    val countAdd = mutableStateOf(0)
    val countDelete = mutableStateOf(0)
    val keyboardFlag = mutableStateOf(false)
    val keyboardAction = mutableStateOf({ text: String -> Unit } )
    val keyboardActionName = mutableStateOf("")
    val texts = mutableStateOf(mutableListOf<String>())
}

class ComposeToolWindow : ToolWindowFactory, DumbAware {


    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Start Plugin")
        val context = Context()
        val content = ContentFactory.SERVICE.getInstance().createContent(
            PanelAction.createPanel(context),
            "MindMap",
            true)

        println("In project: $project")
        println(content.displayName)
        println(content.description)
        println(content.toolwindowTitle)
        println(content.place)
        toolWindow.contentManager.addContent(content, 0)
        val content_ = toolWindow.contentManager.getContent(0)
        println(content_?.displayName)
    }
}