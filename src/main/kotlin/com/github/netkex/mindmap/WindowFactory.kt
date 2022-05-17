package com.github.netkex.mindmap

import androidx.compose.runtime.*
import com.github.netkex.mindmap.UI.WindowAction
import com.github.netkex.mindmap.map.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


object Context {
    var plan: MMap = mutableStateListOf()
    var file: VirtualFile? = null
    lateinit var composeThread: Thread
    var fileProcessing: AtomicBoolean = AtomicBoolean(false)
    var windowProcessing: AtomicBoolean = AtomicBoolean(false)

    fun invokeUpdate() {
        for (idea in plan) {
            println("${idea.text}: ${idea.hashCode()} ${idea.javaClass.name}")
        }
        if (fileProcessing.get())
            return
        windowProcessing.set(true)
        val planDescription = plan.getDescription()
        println(planDescription)
        val curFile = file ?: return
        ApplicationManager.getApplication().runWriteAction {
            VfsUtil.saveText(curFile, planDescription)
            windowProcessing.set(false)
            VfsUtil.markDirtyAndRefresh(true, true, true, curFile)
        }
    }
}

class ComposeToolWindow : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        println("Start Plugin")
        val mainIdea = MMIdea("Main idea", 0.5f, 0.5f)
        val plan = listOf( mainIdea )
        Context.plan.replaceMap( plan )
        val content = ContentFactory.SERVICE.getInstance().createContent(
            WindowAction.createPanel(Context),
            "MindMap",
            true)
        toolWindow.contentManager.addContent(content, 0)
    }
}