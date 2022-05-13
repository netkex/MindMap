package com.github.netkex.mindmap.UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.netkex.mindmap.map.MMIdea
import kotlin.math.roundToInt

@Composable
fun drawIdeaButton(idea: MMIdea, actionPanel: ContextActionPanel, canvasWidth: Float, canvasHeight: Float) {
    val butSizeX = (idea.width + idea.stroke) * 1.1
    val butSizeY = (idea.height + idea.stroke) * 1.1
    val butSizeXDp = with(LocalDensity.current) {
        butSizeX.toInt().toDp()
    }
    val butSizeYDp = with(LocalDensity.current) {
        butSizeY.toInt().toDp()
    }
    val ideaPosX by remember { idea.posX }
    val ideaPosY by remember { idea.posY }
    Button(
        onClick = {
            println("Hei, I ${idea.text} and I was clicked!")
            val actionList = listOf(ContextActions.CHANGE_COLOR, ContextActions.ADD_IDEA)
            actionPanel.pressedIdeaButton(idea, actionList)
        },
        modifier = Modifier
            .offset {
                IntOffset(
                    (canvasWidth * ideaPosX - butSizeX / 2).roundToInt(),
                    (canvasHeight * ideaPosY - butSizeY / 2).roundToInt()
                )
            }
            .size(width = butSizeXDp, height = butSizeYDp)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consumeAllChanges()
                    idea.posX.value += dragAmount.x / canvasWidth
                    idea.posY.value += dragAmount.y / canvasHeight
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