package com.github.netkex.mindmap.UI

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.github.netkex.mindmap.Context
import com.github.netkex.mindmap.map.MMIdea
import kotlin.math.roundToInt

enum class ContextActionState {
    CLOSED, MAIN_MENU, COLOR_MENU, NEW_IDEA_MENU
}

enum class ContextActions {
    CHANGE_COLOR, ADD_IDEA, REMOVE_IDEA
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
val panelWidth = 150.dp
val panelHeight = 75.dp

class ContextActionPanel {
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
    private val switchToRemoveIdeaPanel: () -> Unit = {
        val curIdea = currentAttachedIdea
        if (curIdea == null) {
            closeAll()
        } else {
            Context.plan.value.remove(curIdea)
        }
    }
    private val contextActionsMap = mapOf(
        Pair(ContextActions.CHANGE_COLOR, PanelMenuAction("Change color", switchToColorPanel)),
        Pair(ContextActions.ADD_IDEA, PanelMenuAction("Add idea", switchToAddIdeaPanel)),
        Pair(ContextActions.REMOVE_IDEA, PanelMenuAction("Remove idea", switchToRemoveIdeaPanel))
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
            else -> {}
        }
    }

    @Composable
    private fun drawMaiMenu(idea: MMIdea, canvasWidth: Float, canvasHeight: Float) {
        LazyColumn(modifier = Modifier.offset {
            IntOffset(
                (canvasWidth * idea.posX.value).roundToInt(),
                (canvasHeight * idea.posY.value).roundToInt()
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
                (canvasWidth * idea.posX.value).roundToInt(),
                (canvasHeight * idea.posY.value).roundToInt()
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

    @Composable
    private fun drawNewIdeaMenu(idea: MMIdea, canvasWidth: Float, canvasHeight: Float) {
        TODO()
    }
}