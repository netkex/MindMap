package com.github.netkex.mindmap.UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import javax.swing.JComponent
import com.github.netkex.mindmap.Context
import com.intellij.openapi.project.DumbAwareAction


@Composable
fun mindMapApp(context: Context) {
    Surface(modifier = Modifier) {
        val actionPanel by remember { mutableStateOf(ContextActionPanel()) }
        val plan by remember { context.plan }
        BoxWithConstraints(modifier = Modifier.fillMaxHeight().fillMaxWidth().background(color = Color.White)
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    actionPanel.closeAll()
                }
            }) {
            var canvasWidth_ by remember { mutableStateOf(constraints.maxWidth.toFloat()) }
            var canvasHeight_ by remember { mutableStateOf(constraints.maxHeight.toFloat()) }

            plan.forEach { idea ->
                drawIdeaButton(idea, actionPanel, canvasWidth_, canvasHeight_)
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                canvasWidth_ = canvasWidth
                canvasHeight_ = canvasHeight

                context.plan.value.forEach { idea ->
                    idea.drawEdges(this)
                }
            }

            actionPanel.drawPanel(canvasWidth_, canvasHeight_)
        }
    }
}

class WindowAction : DumbAwareAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val p = e.project
        println("Project is: $p")
        if (p == null) {
            return
        }
        ToolWindowManager.getInstance(p).getToolWindow("MindMap")?.show()
    }

    override fun update(e: AnActionEvent) {
        println("WAS ACTION WITH UPDATE $e")
        super.update(e)
    }

    companion object {
        fun createPanel(context: Context): JComponent {
            return ComposePanel().apply {
                setBounds(0, 0, 1920, 1080)
                setContent {
                    mindMapApp(context)
                }
            }
        }
    }
}