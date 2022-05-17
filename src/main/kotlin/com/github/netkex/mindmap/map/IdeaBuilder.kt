package com.github.netkex.mindmap.map

import androidx.compose.ui.graphics.Color

class IdeaBuilder {
    private var color = Color.Magenta
    private var posX = 0.5f
    private var posY = 0.5f
    private var text = "Main"

    fun setColor(newColor: Color) {
        color = newColor
    }

    fun setX(newPosX: Float) {
        posX = newPosX
    }

    fun setY(newPosY: Float) {
        posY = newPosY
    }

    fun setText(newText: String) {
        text = newText
    }

    fun getIdea(): MMIdea {
        return MMIdea(text = text,
            posX_ = posX,
            posY_ = posY,
            color_ = color)
    }
}