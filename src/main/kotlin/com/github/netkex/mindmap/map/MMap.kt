package com.github.netkex.mindmap.map

typealias MMap = MutableList<MMIdea>

fun MMap.removeIdea(idea: MMIdea) {
    this.remove(idea)
    this.forEach { curIdea -> curIdea.removeSubIdea(idea) }
}
fun MMap.addIdea(idea: MMIdea) {
    this.add(idea)
}