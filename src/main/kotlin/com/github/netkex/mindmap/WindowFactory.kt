package com.github.netkex.mindmap

import androidx.compose.runtime.*
import com.github.netkex.mindmap.UI.WindowAction
import com.github.netkex.mindmap.map.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import java.util.concurrent.atomic.AtomicBoolean


object Context {
    var plan by mutableStateOf( listOf<MMIdea>() )
    var file: VirtualFile? = null
    var newFile: AtomicBoolean = AtomicBoolean(false)

    fun invokeUpdate() {
        if (newFile.get())
            return
        val planDescription = plan.getDescription()
        val curFile = file ?: return
        ApplicationManager.getApplication().runWriteAction {
            VfsUtil.saveText(curFile, planDescription)
            VfsUtil.markDirtyAndRefresh(true, true, true, curFile)
        }
    }
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