package com.github.netkex.mindmap.map

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import com.github.netkex.mindmap.UI.colorMap
import com.github.netkex.mindmap.UI.colorsList
import java.util.*
import kotlin.jvm.Throws

class MindMapParserException : Exception("MindMap Parsing error: incorrect input to parse")
class MMParser {
    private val colorTabel = mapOf(
        Pair("red", Color.Red),
        Pair("black", Color.Black),
        Pair("white", Color.White),
        Pair("magenta", Color.Magenta),
        Pair("yellow", Color.Yellow),
        Pair("blue", Color.Blue)
    )

    @Throws(MindMapParserException::class)
    fun parse(input: String): List<MMIdea> {
        val rawLines = input.split("\n").filter { it.isNotEmpty() && !it.all {it == ' '} }
        val lines = if (rawLines.isEmpty() || rawLines[0] != "MindMap") {
            throw MindMapParserException()
        } else {
            rawLines.drop(1)
        }
        var prevTabs = 0
        val plan = mutableListOf<MMIdea>()
        val ideasStack = Stack<MMIdea>()

        lines.filter { !it.all { it == ' '} }.forEach { s ->
            println("I am parsing idea: $s")
            val idea = parseIdea(s)
            val tabs = s.takeWhile { it == '\t' }.length
            println(tabs)
            if (tabs > prevTabs + 1)
                throw MindMapParserException()
            if (tabs > 0)
                ideasStack[tabs - 1].addSubIdea(idea)
            while (ideasStack.size > tabs) {
                ideasStack.pop()
            }
            ideasStack.push(idea)
            plan.add(idea)
            prevTabs = tabs
        }
        return plan
    }

    private fun parseIdea(line: String): MMIdea {
        val builder = IdeaBuilder()
        val parts = line.split(";")
        parts.forEach { part ->
            val head = removeSpaces( part.takeWhile { it != ':' }.toLowerCase() )
            val body = removeSpaces( part.takeLastWhile { it != ':' } )
            when (head) {
                "color" -> parseColor(builder, body)
                "x" -> parseX(builder, body)
                "y" -> parseY(builder, body)
                "text" -> parseText(builder, body)
                else -> throw MindMapParserException()
            }
        }
        return builder.getIdea()
    }

    private fun parseColor(builder: IdeaBuilder, colorString: String) {
        if (colorString in colorsList.map { it.first }) {
            colorMap[colorString]?.let { builder.setColor(it) }
        } else {
            try {
                if (colorString.length < 6)
                    throw MindMapParserException()
                builder.setColor(Color(Integer.valueOf(colorString, 16)))
            } catch (e: Exception) {
                throw MindMapParserException()
            }
        }
    }

    private fun parseX(builder: IdeaBuilder, XString: String) {
        try {
            builder.setX(XString.toFloat())
        } catch (e: Exception) {
            throw MindMapParserException()
        }
    }

    private fun parseY(builder: IdeaBuilder, YString: String) {
        try {
            builder.setY(YString.toFloat())
        } catch (e: Exception) {
            throw MindMapParserException()
        }
    }

    private fun parseText(builder: IdeaBuilder, text: String) {
        builder.setText(text)
    }

    private fun removeSpaces(s: String): String {
        return s.dropLastWhile { it == ' ' || it == '\t' }.dropWhile { it == ' ' || it == '\t' }
    }
}