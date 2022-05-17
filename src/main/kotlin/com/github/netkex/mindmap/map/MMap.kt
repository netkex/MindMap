package com.github.netkex.mindmap.map

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList

typealias MMap = SnapshotStateList<MMIdea>

fun MMap.removeIdea(idea: MMIdea) {
    this.forEach { it.removeSubIdea(idea) }
    this.remove(idea)
}

fun MMap.addIdea(idea: MMIdea) {
    this.add(idea)
}

fun MMap.getDescription(): String {
    return if (this.isEmpty())
        "MindMap"
    else
        this[0].getDescription().fold("MindMap") { acc, it -> acc + "\n" + it}
}

fun MMap.replaceMap(list: List<MMIdea>) {
    this.clear()
    list.forEach { this.add(it) }
}
