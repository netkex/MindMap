package com.github.netkex.mindmap.UI

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import javax.swing.JComponent
import com.github.netkex.mindmap.Context
import com.github.netkex.mindmap.common.standardWindowHeight
import com.github.netkex.mindmap.common.standardWindowWidth
import com.intellij.openapi.project.DumbAwareAction


@Preview
@Composable
fun mindMapApp(context: Context) {
    Surface(modifier = Modifier) {
        val actionPanel = ContextActionPanel(context)
        BoxWithConstraints(modifier = Modifier.fillMaxHeight().fillMaxWidth().background(color = Color.White)
            .pointerInput(Unit) {
                detectTapGestures { _ ->
                    actionPanel.closeAll()
                }
            }) {

            context.plan.forEach { idea ->
                drawIdeaButton(idea, actionPanel, context)
            }
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                context.actualWidth = canvasWidth
                context.actualHeight = canvasHeight

                context.plan.forEach { idea ->
                    idea.drawEdges(this)
                }
            }

            actionPanel.drawPanel()
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.size(1.dp))
                toolBarButton("Save") { context.invokeUpdate() }
                toolBarButton("Load") { context.updatePlan() }
                Spacer(modifier = Modifier.weight(1f))
            }
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
                setBounds(0, 0, standardWindowWidth, standardWindowHeight)
                setContent {
                    mindMapApp(context)
                }
            }
        }
    }
}