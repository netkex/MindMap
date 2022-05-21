package com.github.netkex.mindmap.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.github.netkex.mindmap.UI.colorsList
import com.github.netkex.mindmap.common.*
import org.jetbrains.skia.*
import org.jetbrains.skia.Paint
import kotlin.math.*


private const val arrowSize = 25f

class MMIdea(
    var text: String,
    posX_: Float,
    posY_: Float,
    color_: Color = Color.Magenta,
    private val fontSize: Float = 25f,
    val stroke: Float = 8f
) {
    var alife by mutableStateOf( true )
    private var color by mutableStateOf( color_ )
    private val subIdeas: MutableList<MMIdea> = mutableListOf()
    private val font = Font(Typeface.makeDefault()).apply { size = fontSize }
    private var textLine = TextLine.make(text, font)

    // relative coordinates of idea
    var posX by mutableStateOf(posX_)
    var posY by mutableStateOf(posY_)

    // box parameters of idea
    var width by mutableStateOf( recalculateWidth()  )
    var height by mutableStateOf( recalculateHeight() )

    // ellipse parameters
    private val semiMajorAxe
        get() = width / 2
    private val semiMinorAxe
        get() = height / 2

    fun changeColor(newColor: Color) {
        color = newColor
    }

    fun changeText(newText: String) {
        text = newText
        textLine = TextLine.make(text, font)
        width = recalculateWidth()
        height = recalculateHeight()
    }

    fun addSubIdea(idea: MMIdea) {
        subIdeas.add(idea)
    }

    fun removeSubIdea(idea: MMIdea) {
        subIdeas.remove(idea)
    }

    fun drawBody(drawScope: DrawScope) {
        with(drawScope) {
            val windowWidth = size.width
            val windowHeight = size.height
            drawOval(
                color = color,
                topLeft = Offset(
                    x = windowWidth / 2 - width / 2,
                    y = windowHeight / 2 - height / 2,
                ),
                style = Stroke(width = stroke),
                size = Size(
                    width = width,
                    height = height
                )
            )
        }
        drawText(drawScope)
    }

    fun drawEdges(drawScope: DrawScope) {
        if (!alife)
            return
        subIdeas.forEach { subIdea ->
            drawEdgeTo(drawScope, subIdea)
        }
        subIdeas.removeIf { !it.alife }
    }

    fun isLeaf(): Boolean {
        return subIdeas.isEmpty()
    }

    fun copy(): MMIdea {
        return MMIdea(text = text,
            posX_ = posX,
            posY_ = posY,
            color_ = color,
            fontSize = fontSize,
            stroke = stroke)
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    fun getDescription(): List<String> {
        val colorString = if (color in colorsList.map { it.second }) {
            colorsList.map { Pair(it.second, it.first) }.toMap()[color]
        } else {
            color.toArgb().toString(16).toUpperCase()
        }
        val itselfDescription = "text: $text; color: $colorString; X: $posX; Y: $posY"
        val childrenDescription = subIdeas.map { it.getDescription() }.flatten().map { "\t" + it }
        return listOf(itselfDescription) + childrenDescription
    }

    private fun recalculateWidth(): Float {
        return textLine.width * (if (text.length < 4) 2f - text.length / 10 else 1.3f)
    }

    private fun recalculateHeight(): Float {
        return width / 2f
    }

    private fun drawText(drawScope: DrawScope) {
        with(drawScope) {
            val windowWidth = size.width
            val windowHeight = size.height
            val offsetX = -textLine.width / 2
            val offsetY = font.metrics.capHeight / 2

            drawContext.canvas.nativeCanvas.apply {
                drawTextLine(
                    line = textLine,
                    x = windowWidth / 2 + offsetX,
                    y = windowHeight / 2 + offsetY,
                    paint = Paint()
                )
            }
        }
    }

    private fun calcRadiusEllipse(semiMajorAxe: Float, semiMinorAxe: Float, angle: Float): Float {
        return sqrt(1.0 / (cos(angle).pow(2) / semiMajorAxe.pow(2) + sin(angle).pow(2) / semiMinorAxe.pow(2))).toFloat()
    }

    private fun drawEdgeTo(drawScope: DrawScope, subIdea: MMIdea) {
        if  (subIdea.alife) {
            val windowWidth = drawScope.size.width
            val windowHeight = drawScope.size.height

            val ideaCenterX = posX * windowWidth
            val ideaCenterY = posY * windowHeight
            val subIdeaCenterX = subIdea.posX * windowWidth
            val subIdeaCenterY = subIdea.posY * windowHeight

            val angleToSubIdea = atan2(subIdeaCenterY - ideaCenterY, subIdeaCenterX - ideaCenterX)
            val angleToIdea = atan2(ideaCenterY - subIdeaCenterY, ideaCenterX - subIdeaCenterX)
            val ideaRadiusEllipse = calcRadiusEllipse(semiMajorAxe, semiMinorAxe, angleToSubIdea)
            val subIdeaRadiusEllipse = calcRadiusEllipse(subIdea.semiMajorAxe, subIdea.semiMinorAxe, angleToIdea)

            val ideaPointOnEllipse = Point(
                ideaCenterX + ideaRadiusEllipse * cos(angleToSubIdea),
                ideaCenterY + ideaRadiusEllipse * sin(angleToSubIdea)
            )
            val subIdeaPointOnEllipse = Point(
                subIdeaCenterX + subIdeaRadiusEllipse * cos(angleToIdea),
                subIdeaCenterY + subIdeaRadiusEllipse * sin(angleToIdea)
            )

            drawArrowTo(drawScope, subIdea, ideaPointOnEllipse, subIdeaPointOnEllipse, angleToSubIdea)
        }
    }

    private fun drawArrowTo(
        drawScope: DrawScope,
        subIdea: MMIdea,
        ideaPointOnEllipse: Point,
        subIdeaPointOnEllipse: Point,
        angleToSubIdea: Float) {
        with(drawScope) {
            val arrowVector1 =
                (ideaPointOnEllipse - subIdeaPointOnEllipse).normalise().rotate(-PI.toFloat() / 6) * arrowSize
            val arrowVector2 =
                (ideaPointOnEllipse - subIdeaPointOnEllipse).normalise().rotate(PI.toFloat() / 6) * arrowSize
            val vectorToSubIdea = subIdeaPointOnEllipse - ideaPointOnEllipse

            val brush = if (abs(abs(angleToSubIdea) - PI / 2) < PI / 4) {
                Brush.verticalGradient(
                    colors = listOf(
                        color,
                        subIdea.color
                    ),
                    startY = ideaPointOnEllipse.y,
                    endY = subIdeaPointOnEllipse.y,
                    tileMode = TileMode.Repeated
                )
            } else {
                Brush.horizontalGradient(
                    colors = listOf(
                        color,
                        subIdea.color
                    ),
                    startX = ideaPointOnEllipse.x,
                    endX = subIdeaPointOnEllipse.x,
                    tileMode = TileMode.Repeated
                )
            }

            drawLine(
                start = Offset(ideaPointOnEllipse.x, ideaPointOnEllipse.y),
                end = Offset(subIdeaPointOnEllipse.x, subIdeaPointOnEllipse.y),
                strokeWidth = stroke,
                brush = brush
            )
            drawLine(
                start = Offset(ideaPointOnEllipse.x, ideaPointOnEllipse.y),
                end = Offset(
                    (ideaPointOnEllipse + vectorToSubIdea * 0.03f).x,
                    (ideaPointOnEllipse + vectorToSubIdea * 0.03f).y),
                color = color,
                strokeWidth = stroke
            )

            drawLine(
                start = Offset(subIdeaPointOnEllipse.x, subIdeaPointOnEllipse.y),
                end = Offset((subIdeaPointOnEllipse + arrowVector1).x, (subIdeaPointOnEllipse + arrowVector1).y),
                color = subIdea.color,
                strokeWidth = stroke / 3
            )
            drawLine(
                start = Offset(subIdeaPointOnEllipse.x, subIdeaPointOnEllipse.y),
                end = Offset((subIdeaPointOnEllipse + arrowVector2).x, (subIdeaPointOnEllipse + arrowVector2).y),
                color = subIdea.color,
                strokeWidth = stroke / 3
            )

            drawLine(
                start = Offset(subIdeaPointOnEllipse.x, subIdeaPointOnEllipse.y),
                end = Offset(
                    (subIdeaPointOnEllipse - vectorToSubIdea * 0.03f).x,
                    (subIdeaPointOnEllipse - vectorToSubIdea * 0.03f).y),
                color = subIdea.color,
                strokeWidth = stroke
            )
        }
    }
}


