package com.github.netkex.mindmap.UI

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.netkex.mindmap.Context
import com.github.netkex.mindmap.map.MMIdea
import com.github.netkex.mindmap.map.addIdea
import com.github.netkex.mindmap.map.removeIdea
import kotlin.math.roundToInt

enum class ContextActionState {
    CLOSED, MAIN_MENU, COLOR_MENU, NEW_IDEA_MENU, RENAME_MENU
}

enum class ContextActions {
    CHANGE_COLOR, ADD_IDEA, REMOVE_IDEA, RENAME
}

data class PanelMenuAction(val text: String, val onClick: () -> Unit)

val colorsList = listOf (
    Pair("Red", Color.Red),
    Pair("Blue", Color.Blue),
    Pair("Green", Color.Green),
    Pair("Yellow", Color.Yellow),
    Pair("Magenta", Color.Magenta),
    Pair("Black", Color.Black),
    Pair("Gray", Color.Gray),
    Pair("Cyan", Color.Cyan))
val colorMap = colorsList.toMap()
val panelWidth = 150.dp
val panelHeight = 75.dp

class ContextActionPanel(context: Context) {
    private var currentAttachedIdea: MMIdea? by mutableStateOf( null )
    private var currentActionState by mutableStateOf( ContextActionState.CLOSED )
    private var mainMenuActionList: List<PanelMenuAction> by mutableStateOf( listOf() )
    private val switchToColorPanel: () -> Unit = {
        println("HEI! COLOR WAS CLICKED!")
        currentActionState = ContextActionState.COLOR_MENU
    }
    private val switchToAddIdeaPanel: () -> Unit = {
        currentActionState = ContextActionState.NEW_IDEA_MENU
    }
    private val switchToRenamePanel: () -> Unit = {
        currentActionState = ContextActionState.RENAME_MENU
    }
    private val switchToRemoveIdeaPanel: () -> Unit = {
        val curIdea = currentAttachedIdea
        closeAll()
        if (curIdea != null) {
            context.plan.removeIdea(curIdea)
        }
    }
    private val contextActionsMap = mapOf(
        Pair(ContextActions.CHANGE_COLOR, PanelMenuAction("Change color", switchToColorPanel)),
        Pair(ContextActions.ADD_IDEA, PanelMenuAction("Add idea", switchToAddIdeaPanel)),
        Pair(ContextActions.REMOVE_IDEA, PanelMenuAction("Remove idea", switchToRemoveIdeaPanel)),
        Pair(ContextActions.RENAME, PanelMenuAction("Rename idea", switchToRenamePanel))
        )

    fun pressedIdeaButton(idea: MMIdea, actionList: List<ContextActions>) {
        if (currentActionState == ContextActionState.CLOSED || (currentAttachedIdea != idea)) {
            currentAttachedIdea = idea
            currentActionState = ContextActionState.MAIN_MENU
            mainMenuActionList = actionList.mapNotNull { contextActionsMap[it] }
        } else {
            closeAll()
        }
    }

    fun closeAll() {
        currentAttachedIdea = null
        currentActionState = ContextActionState.CLOSED
        Context.windowProcessing.set(false)
    }

    @Composable
    fun drawPanel(canvasWidth: Float, canvasHeight: Float) {
        if (currentActionState == ContextActionState.CLOSED)
            return
        val curIdea = let {
            val curIdea_ = currentAttachedIdea
            if (curIdea_ == null) {
                closeAll()
                return@drawPanel
            } else {
                curIdea_
            }
        }

        when(currentActionState) {
            ContextActionState.MAIN_MENU -> drawMaiMenu(curIdea, canvasWidth, canvasHeight)
            ContextActionState.COLOR_MENU -> drawColorMenu(curIdea, canvasWidth, canvasHeight)
            ContextActionState.NEW_IDEA_MENU -> drawNewIdeaMenu(curIdea, canvasWidth, canvasHeight)
            ContextActionState.RENAME_MENU -> drawRenameMenu(curIdea, canvasWidth, canvasHeight)
            else -> {}
        }
    }

    @Composable
    private fun drawMaiMenu(idea: MMIdea, canvasWidth: Float, canvasHeight: Float) {
        LazyColumn(modifier = Modifier.offset {
            IntOffset(
                (canvasWidth * idea.posX).roundToInt(),
                (canvasHeight * idea.posY).roundToInt()
            ) }
            .size(panelWidth, panelHeight),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(mainMenuActionList) { action ->
                Button(modifier = Modifier.fillMaxWidth().height(25.dp),
                    onClick = action.onClick,
                    shape = RoundedCornerShape(0),
                    border = null,
                    elevation = null,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE0FFFF))) {
                    Text(modifier = Modifier.padding(0.dp), text = action.text, textAlign = TextAlign.Left)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    @Composable
    private fun drawColorMenu(idea: MMIdea, canvasWidth: Float, canvasHeight: Float) {
        LazyColumn(modifier = Modifier.offset {
            IntOffset(
                (canvasWidth * idea.posX).roundToInt(),
                (canvasHeight * idea.posY).roundToInt()
            ) }
            .size(panelWidth, panelHeight),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            items(colorsList) { color ->
                Button(modifier = Modifier.fillMaxWidth().height(25.dp),
                    onClick = {
                        println("Was pressed ${color.first}")
                        idea.changeColor(color.second)
                        closeAll()
                    },
                    shape = RoundedCornerShape(0),
                    border = null,
                    elevation = null,
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFFE0FFFF))) {
                    Canvas(modifier = Modifier.size(width = 10.dp, height = panelHeight)) {
                        drawCircle(
                            radius = size.width / 2,
                            color = color.second,
                            center = Offset(size.width / 2, size.height / 2)
                        )
                    }
                    Text(modifier = Modifier.padding(0.dp).offset(x = 5.dp), text = color.first, textAlign = TextAlign.Left)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun drawNewIdeaMenu(idea: MMIdea, canvasWidth: Float, canvasHeight: Float) {
        panelKeyBoard(idea, canvasWidth, canvasHeight, "Insert name of new idea", "New Idea Name") { keyEvent, text ->
            when {
                (keyEvent.key == Key.Enter) -> {
                    if (text == "") {
                        Pair(text, false)
                    } else {
                        println("New idea name: ${text}")
                        closeAll()
                        val newIdea = idea.copy()
                        newIdea.posX += 0.05f
                        newIdea.posY += 0.05f
                        newIdea.changeText(text)
                        idea.addSubIdea(newIdea)
                        Context.plan.addIdea(newIdea)
                        Pair("", true)
                    }
                }
                else -> Pair(text, false)
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    private fun drawRenameMenu(idea: MMIdea, canvasWidth: Float, canvasHeight: Float) {
        panelKeyBoard(idea, canvasWidth, canvasHeight, "Insert name of idea", "Idea New Name") { keyEvent, text ->
            when {
                (keyEvent.key == Key.Enter) -> {
                    if (text == "") {
                        Pair(text, false)
                    } else {
                        println("Idea new name: ${text}")
                        closeAll()

                        idea.changeText(text)
                        Pair("", true)
                    }
                }
                else -> Pair(text, false)
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    fun panelKeyBoard(idea: MMIdea,
                      canvasWidth: Float,
                      canvasHeight: Float,
                      windowText: String = "",
                      placeholderText: String = "",
                      onKeyEvent: (KeyEvent, String) -> Pair<String, Boolean> = { _, text -> Pair(text, false) }) {
        Box(modifier = Modifier.offset {
            IntOffset(
                (canvasWidth * idea.posX).roundToInt(),
                (canvasHeight * idea.posY).roundToInt())
        }
            .width(panelWidth)
            .background(color = Color(0xFFE0FFFF))) {
            Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(5.dp)) {
                Text(modifier = Modifier.align(Alignment.CenterHorizontally), text = windowText)
                customTextField(
                    modifier = Modifier.background(Color(0xFFE0FFFF).copy(alpha = 0.5f)),
                    onKeyEvent = { keyEvent, s ->
                        if (keyEvent.key == Key.Semicolon) {
                            Pair(s.dropLast(1), false)
                        } else {
                            onKeyEvent(keyEvent, s)
                        }
                    },
                    placeholderText = placeholderText
                )
            }
        }
    }
}