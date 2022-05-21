package com.github.netkex.mindmap

import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile


var cnt = 0
class MindMapConstructorInteractor : LocalInspectionTool() {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
        cnt++
        Context.file = file.virtualFile
        Context.fileText = file.text
        if (Context.getOwnerFlag() == OwnerState.ComposeUpdate || Context.composeUpdated.get()) {
            Context.composeUpdated.set(false)
            return arrayOf()
        }
        Context.setOwnerFlag(OwnerState.FileUpdate)
        Context.setUpdateFileFlag(true)
        return arrayOf()
    }
}