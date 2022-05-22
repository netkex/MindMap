package com.github.netkex.mindmap.map

import androidx.compose.runtime.snapshots.SnapshotStateList

typealias MMap = SnapshotStateList<MMIdea>

fun MMap.removeIdea(idea: MMIdea) {
    idea.alife = false
}

fun MMap.addIdea(idea: MMIdea) {
    idea.alife = true
    this.add(idea)
}

fun MMap.getDescription(): String {
    val curMap = this.filter { it.alife }
    return if (curMap.isEmpty())
        "MindMap"
    else
        curMap[0].getDescription().fold("MindMap") { acc, it -> acc + "\n" + it}
}

fun MMap.replaceMap(list: List<MMIdea>) {
    this.forEach { it.alife = false }
    list.forEach { it.alife = true }
    this.addAll(list)
}

fun MMap.copy(): List<MMIdea> {
    val list = mutableListOf<MMIdea>()
    list.addAll(this.toList())
    return list
}