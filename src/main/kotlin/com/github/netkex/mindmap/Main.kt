package com.github.netkex.mindmap

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.netkex.mindmap.UI.mindMapApp
import com.github.netkex.mindmap.map.MMIdea
import com.github.netkex.mindmap.map.replaceMap

fun main() {
    val plan = listOf(MMIdea("Main idea", 0.5f, 0.5f))
    Context.plan.replaceMap(plan)
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Mind Map",
            state = rememberWindowState(width = 480.dp, height = 720.dp)
        ) {
            mindMapApp(Context)
        }
    }
}