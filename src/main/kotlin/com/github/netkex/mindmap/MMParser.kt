package com.github.netkex.mindmap

import java.util.*
import kotlin.jvm.Throws
private const val tabLen = 4


class MindMapParserException : Exception("MindMap Parsing error: incorrect input to parse")
class MMParser {
    @Throws(MindMapParserException::class)
    fun parse(input: String): MMPlan {
        val rawLines = input.split("\n").filter { it.isNotEmpty() && !it.all {it == ' '} }
        val lines = if (rawLines.isEmpty() || rawLines[0] != "MindMap") {
            throw MindMapParserException()
        } else {
            rawLines.drop(1)
        }

        var prevTabs = 0
        val plan = mutableListOf<MMIdea>()
        val ideasStack = Stack<MMIdea>()
        lines.forEach { s ->
            val (tabs, idea) = buildIdea(s)
            if (tabs > prevTabs + 1)
                throw MindMapParserException()
            if (tabs > 0)
                ideasStack[tabs - 1].addIdea(idea)
            while (ideasStack.size > tabs) {
                ideasStack.pop()
            }
            ideasStack.push(idea)
            plan.add(idea)
            prevTabs = tabs
        }
        return plan
    }

    private fun buildIdea(line: String) : Pair<Int, MMIdea> {
        val tabs = line.takeWhile { it == ' ' }.length / tabLen
        val info = line.dropWhile { it == ' ' }
        val ideaText = info.takeWhile { it != ':' }

        val coordinates = try {
            info.dropWhile { it != ':' }
                .drop(1)
                .split(' ')
                .filter { it.isNotEmpty() }
                .map { it.toFloat() }
        } catch (e: Exception) {
            throw MindMapParserException()
        }
        if (coordinates.size < 2)
            throw MindMapParserException()
        return Pair(tabs, MMIdea(ideaText, coordinates[0], coordinates[1]))
    }
}