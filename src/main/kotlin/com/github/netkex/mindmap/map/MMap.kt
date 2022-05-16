package com.github.netkex.mindmap.map

typealias MMap = List<MMIdea>

fun MMap.removeIdea(idea: MMIdea): MMap {
    this.forEach { it.removeSubIdea(idea) }
    return this.filter { it != idea }
}

fun MMap.addIdea(idea: MMIdea): MMap {
    return this + idea
}

fun MMap.getDescription(): String {
    return if (this.isEmpty())
        "MindMap"
    else
        this[0].getDescription().fold("MindMap") { acc, it -> acc + "\n" + it}
}
