package com.github.netkex.mindmap.UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.*
import com.github.netkex.mindmap.Context
import com.github.netkex.mindmap.OwnerState
import com.github.netkex.mindmap.common.buttonSizeCoefficient
import com.github.netkex.mindmap.map.MMIdea
import com.intellij.openapi.application.constrainedReadAction
import kotlin.math.roundToInt

@Composable
fun drawIdeaButton(idea: MMIdea,
                   actionPanel: ContextActionPanel,
                   context: Context) {
    val butSizeX = (idea.width + idea.stroke) * buttonSizeCoefficient
    val butSizeY = (idea.height + idea.stroke) * buttonSizeCoefficient
    val butSizeXDp = with(LocalDensity.current) {
        butSizeX.toInt().toDp()
    }
    val butSizeYDp = with(LocalDensity.current) {
        butSizeY.toInt().toDp()
    }
    if (idea.alife) {
        Button(
            onClick = action@{
                if (context.getOwnerFlag() == OwnerState.FileUpdate) {
                    return@action
                } else {
                    context.setOwnerFlag(OwnerState.ComposeUpdate)
                }
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
                        (context.actualWidth * idea.posX - butSizeX / 2).roundToInt(),
                        (context.actualHeight * idea.posY - butSizeY / 2).roundToInt()
                    )
                }
                .size(width = butSizeXDp, height = butSizeYDp)
                .pointerInput(Unit) {
                    detectDragGestures(onDragStart = {
                        actionPanel.closeAll()
                        if (context.getOwnerFlag() != OwnerState.FileUpdate) {
                            context.setOwnerFlag(OwnerState.ComposeUpdate)
                        }
                    }, onDragEnd = {
                        if (context.getOwnerFlag() != OwnerState.FileUpdate) {
                            context.invokeUpdate()
                            context.setOwnerFlag(OwnerState.Nobody)
                        }
                    }) { change: PointerInputChange, dragAmount: Offset ->
                        change.consumeAllChanges()
                        if (context.getOwnerFlag() != OwnerState.FileUpdate) {
                            idea.posX += dragAmount.x / context.actualWidth
                            idea.posY += dragAmount.y / context.actualHeight
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
fun toolBarButton(text: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.height(30.dp),
        onClick = onClick,
        shape =  RoundedCornerShape(50),
        border = null,
        elevation = null,
        contentPadding = PaddingValues(0.dp)) {
        Text(text, fontSize = 13.sp, modifier = Modifier.padding(0.dp))
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