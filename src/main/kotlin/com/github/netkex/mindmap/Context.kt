package com.github.netkex.mindmap

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.github.netkex.mindmap.common.standardWindowHeight
import com.github.netkex.mindmap.common.standardWindowWidth
import com.github.netkex.mindmap.map.*
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantLock

enum class OwnerState {
    FileUpdate, ComposeUpdate, Nobody
}

object Context {
    var plan: MMap = mutableStateListOf()
    var file: VirtualFile? = null
    var ownerFlag = OwnerState.Nobody
    val composeUpdated = AtomicBoolean(false)
    var updateFromFileFlag by mutableStateOf( false )
    val lock = ReentrantLock()
    var actualWidth by mutableStateOf( standardWindowWidth.toFloat() )
    var actualHeight by mutableStateOf( standardWindowHeight.toFloat() )
    var fileText: String = ""
    private val parser = MMParser()

    fun invokeUpdate() {
        val curFile = file ?: return
        synchronized(lock) {
            val planDescription = plan.getDescription()
            ApplicationManager.getApplication().runWriteAction {
                VfsUtil.saveText(curFile, planDescription)
                composeUpdated.set(true)
                VfsUtil.markDirtyAndRefresh(true, true, true, curFile)
            }
        }
    }

    fun updatePlan() {
        val curFile = file ?: return
        try {
            val plan = parser.parse(fileText)
            synchronized(lock) {
                Context.plan.replaceMap(plan)
            }
            println("MindMap Plan was updated by file ${curFile.name}")
        } catch (e: MindMapParserException) { }
    }

    @JvmName("getOwnerFlag1")
    fun getOwnerFlag(): OwnerState {
        val res = synchronized(lock) {
            ownerFlag
        }
        return res
    }

    @JvmName("setOwnerFlag1")
    fun setOwnerFlag(value: OwnerState) {
        synchronized(lock) {
            ownerFlag = value
        }
    }

    fun getUpdateFileFlag(): Boolean {
        return synchronized(lock) {
            updateFromFileFlag
        }
    }

    fun setUpdateFileFlag(value: Boolean) {
        synchronized(lock) {
            updateFromFileFlag = value
        }
    }
}