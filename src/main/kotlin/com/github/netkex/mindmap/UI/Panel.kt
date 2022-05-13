package com.github.netkex.mindmap.UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposePanel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import javax.swing.JComponent
import com.github.netkex.mindmap.Context
import com.intellij.openapi.project.DumbAwareAction
import kotlin.math.roundToInt


@Composable
fun mindMapApp(context: Context) {
    Surface(modifier = Modifier) {

        BoxWithConstraints(modifier = Modifier.fillMaxHeight().fillMaxWidth().background(color = Color.White)) {
            var canvasWidth_ by remember { mutableStateOf(constraints.maxWidth.toFloat()) }
            var canvasHeight_ by remember { mutableStateOf(constraints.maxHeight.toFloat()) }

            context.plan.value.forEach { idea ->
                val butSizeX_ = (idea.width + idea.stroke) * 1.1
                val butSizeY_ = (idea.height + idea.stroke) * 1.1
                val butSizeX = with(LocalDensity.current) {
                    butSizeX_.toInt().toDp()
                }
                val butSizeY = with(LocalDensity.current) {
                    butSizeY_.toInt().toDp()
                }
                val ideaPosX by remember { idea.posX }
                val ideaPosY by remember { idea.posY }
                Button(
                    onClick = {
                        println("Hei, I was clicked!")
                    },
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (canvasWidth_ * ideaPosX - butSizeX_ / 2).roundToInt(),
                                (canvasHeight_ * ideaPosY - butSizeY_ / 2).roundToInt()
                            )
                        }
                        .size(width = butSizeX, height = butSizeY)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consumeAllChanges()
                                idea.posX.value += dragAmount.x / canvasWidth_
                                idea.posY.value += dragAmount.y / canvasHeight_
                            }
                        },
                    colors = ButtonDefaults.buttonColors(Color.White),
                    border = null,
                    elevation = null,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        idea.drawBody(this)
                    }
                }
            }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                canvasWidth_ = canvasWidth
                canvasHeight_ = canvasHeight
            }
        }
    }
}

class PanelAction : DumbAwareAction() {
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