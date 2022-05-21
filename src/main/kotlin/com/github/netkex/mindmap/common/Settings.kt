package com.github.netkex.mindmap.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.netkex.mindmap.map.MMIdea

val standardWindowWidth = 1920
val standardWindowHeight = 1080

const val buttonSizeCoefficient = 1.1

val lightCyanColor = Color(0xFFE0FFFF)
val colorsList = listOf (
    Pair("Red", Color.Red),
    Pair("Blue", Color.Blue),
    Pair("Green", Color.Green),
    Pair("Yellow", Color.Yellow),
    Pair("Magenta", Color.Magenta),
    Pair("Black", Color.Black),
    Pair("Gray", Color.Gray),
    Pair("Cyan", Color.Cyan))
val colorsMap = colorsList.toMap()

val defaultPanelColor = lightCyanColor
val defaultPanelWidth = 150.dp
val defaultPanelHeight = 100.dp

val defaultIdeaColor = Color.Black
val defaultIdeaFontSize = 25f
val defaultIdeaStroke = 8f

val initIdea = MMIdea("Main idea", 0.5f, 0.5f)
val initMap = listOf ( initIdea )