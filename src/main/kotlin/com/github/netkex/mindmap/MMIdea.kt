package com.github.netkex.mindmap

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import org.jetbrains.skia.*
import kotlin.math.*

private const val arrowSize = 25f

class MMIdea(
    text: String,
    val posX: Float,
    val posY: Float,
    val color: Color,
    val fontSize: Float = 25f
) {

    private val subIdeas: MutableList<MMIdea> = mutableListOf()
    private val stroke = 8f
    private val font = Font(Typeface.makeDefault()).apply { size = fontSize }
    private val textLine = TextLine.make(text, font)
    val width = textLine.width * (if (text.length < 4) 2f - text.length / 10 else 1.3f)
    val height = width / 2f

    // ellipse parameters
    private val a
        get() = width / 2
    private val b
        get() = height / 2

    fun addIdea(idea: MMIdea) {
        subIdeas.add(idea)
    }

    fun drawIdea(drawScope: DrawScope, windowWidth: Float, windowHeight: Float) {
        subIdeas.forEach { subIdea ->
            drawEdgeTo(drawScope, subIdea, windowWidth, windowHeight)
        }
        drawBody(drawScope, windowWidth, windowHeight)
    }

    private fun drawBody(drawScope: DrawScope, windowWidth: Float, windowHeight: Float) {
        drawScope.drawOval(
            color = color,
            topLeft = Offset(
                x = windowWidth * posX - width / 2,
                y = windowHeight * posY - height / 2,
            ),
            style = Stroke(width = stroke),
            size = Size(
                width = width,
                height = height
            )
        )
        drawText(drawScope, windowWidth, windowHeight)
    }

    private fun drawText(drawScope: DrawScope, windowWidth: Float, windowHeight: Float) {
        val offsetX = -textLine.width / 2
        val offsetY = font.metrics.capHeight / 2

        drawScope.drawContext.canvas.nativeCanvas.apply {
            drawTextLine(
                line = textLine,
                x = windowWidth * posX + offsetX,
                y = windowHeight * posY + offsetY,
                paint = Paint()
            )
        }
    }

    private fun drawEdgeTo(drawScope: DrawScope, idea: MMIdea, windowWidth: Float, windowHeight: Float) {
        val x0 = posX * windowWidth
        val y0 = posY * windowHeight
        val x1 = idea.posX * windowWidth
        val y1 = idea.posY * windowHeight
        val angle1 = atan2(y1 - y0, x1 - x0)
        val angle2 = atan2(y0 - y1, x0 - x1)
        val r1 = sqrt(1.0 / (cos(angle1).pow(2) / a.pow(2) + sin(angle1).pow(2) / b.pow(2))).toFloat()
        val r2 = sqrt(1.0 / (cos(angle2).pow(2) / idea.a.pow(2) + sin(angle2).pow(2) / idea.b.pow(2))).toFloat()
        val pt1 = Point(
            x0 + r1 * cos(angle1),
            y0 + r1 * sin(angle1)
        )
        val pt2 = Point(
            x1 + r2 * cos(angle2),
            y1 + r2 * sin(angle2)
        )
        val v1 = (pt1 - pt2).normalise().rotate(-PI.toFloat() / 6) * arrowSize
        val v2 = (pt1 - pt2).normalise().rotate(PI.toFloat() / 6) * arrowSize
        val brush = Brush.verticalGradient(
            colors = listOf(
                color,
                idea.color
            ),
            startY = pt1.y,
            endY = pt2.y,
            tileMode = TileMode.Repeated
        )
        drawScope.drawLine(
            start = Offset(pt1.x, pt1.y),
            end = Offset(pt2.x, pt2.y),
//            color = color,
            strokeWidth = stroke,
            brush = brush
        )
        drawScope.drawLine(
            start = Offset(pt2.x, pt2.y),
            end = Offset((pt2 + v1).x, (pt2 + v1).y),
            color = idea.color,
            strokeWidth = stroke / 3
        )
        drawScope.drawLine(
            start = Offset(pt2.x, pt2.y),
            end = Offset((pt2 + v2).x, (pt2 + v2).y),
            color = idea.color,
            strokeWidth = stroke / 3
        )
    }
}


typealias MMPlan = List<MMIdea>