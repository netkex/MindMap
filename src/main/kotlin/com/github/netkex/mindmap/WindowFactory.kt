package com.github.netkex.mindmap

import androidx.compose.runtime.*
import com.github.netkex.mindmap.UI.WindowAction
import com.github.netkex.mindmap.common.initMap
import com.github.netkex.mindmap.common.standardWindowHeight
import com.github.netkex.mindmap.common.standardWindowWidth
import com.github.netkex.mindmap.map.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

object Context {
    var plan: MMap = mutableStateListOf()
    var file: VirtualFile? = null
    var actualWidth by mutableStateOf( standardWindowWidth.toFloat() )
    var actualHeight by mutableStateOf( standardWindowHeight.toFloat() )
    private val parser = MMParser()

    fun invokeUpdate() {
        val curFile = file ?: return
        val planDescription = plan.getDescription()
        ApplicationManager.getApplication().runWriteAction {
            VfsUtil.saveText(curFile, planDescription)
            VfsUtil.markDirtyAndRefresh(true, true, true, curFile)
        }
    }

    fun updatePlan() {
        val curFile = file ?: return
        val fileText = VfsUtil.loadText(curFile)
        try {
            val plan = parser.parse(fileText)
            Context.plan.replaceMap(plan)
            println("MindMap Plan was updated by file ${curFile.name}")
        } catch (e: MindMapParserException) { }
    }
}

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