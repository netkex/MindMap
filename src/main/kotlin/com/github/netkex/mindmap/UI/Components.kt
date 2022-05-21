package com.github.netkex.mindmap.UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.github.netkex.mindmap.Context
import com.github.netkex.mindmap.map.MMIdea
import kotlin.math.roundToInt

@Composable
fun drawIdeaButton(idea: MMIdea,
                   actionPanel: ContextActionPanel,
                   canvasWidth: Float,
                   canvasHeight: Float) {
    val butSizeX = (idea.width + idea.stroke) * 1.1
    val butSizeY = (idea.height + idea.stroke) * 1.1
    val butSizeXDp = with(LocalDensity.current) {
        butSizeX.toInt().toDp()
    }
    val butSizeYDp = with(LocalDensity.current) {
        butSizeY.toInt().toDp()
    }
    println("I was drown, button: ${idea.text}")
    if (idea.alife) {
        Button(
            onClick = {
                val actionList =
                    mutableListOf(ContextActions.CHANGE_COLOR, ContextActions.ADD_IDEA, ContextActions.RENAME)
                if (Context.plan.size != 1 && idea.isLeaf()) {
                    actionList += ContextActions.REMOVE_IDEA
                }
                actionPanel.pressedIdeaButton(idea, actionList)
            },
            modifier = Modifier
                .offset {
                    IntOffset(
                        (canvasWidth * idea.posX - butSizeX / 2).roundToInt(),
                        (canvasHeight * idea.posY - butSizeY / 2).roundToInt()
                    )
                }
                .size(width = butSizeXDp, height = butSizeYDp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            println("Drag end")
                            println("X: ${idea.posX}; text: ${idea.text}")
                        }, onDragStart = {
                            Context.windowProcessing.set(true)
                        }) { change, dragAmount ->
                        change.consumeAllChanges()
                        println("Drag, ${dragAmount.x} ${dragAmount.y}, ${idea.hashCode()}, ${idea.text}")
                        if (!Context.fileProcessing.get()) {
                            idea.posX += dragAmount.x / canvasWidth
                            idea.posY += dragAmount.y / canvasHeight
                            println("${idea.posX}   ${idea.posY}")
                        }
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
}

@Composable
fun customTextField(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholderText: String = "",
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize,
    onKeyEvent: (KeyEvent, String) -> Pair<String, Boolean> = { _, text -> Pair(text, false) }
) {
    var text by rememberSaveable { mutableStateOf("") }
    BasicTextField(modifier = modifier
        .background(
            MaterialTheme.colors.surface,
            MaterialTheme.shapes.small,
        )
        .fillMaxWidth()
        .onKeyEvent { keyEvent ->
            val (newText, res) = onKeyEvent(keyEvent, text)
            text = newText
            res
        },
        value = text,
        onValueChange = {
            text = it
        },
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onSurface,
            fontSize = fontSize
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.weight(1f)) {
                    if (text.isEmpty()) Text(
                        placeholderText,
                        style = LocalTextStyle.current.copy(
                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                            fontSize = fontSize
                        )
                    )
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}