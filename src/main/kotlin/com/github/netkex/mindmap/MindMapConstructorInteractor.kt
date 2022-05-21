package com.github.netkex.mindmap

import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile


var cnt = 0
class MindMapConstructorInteractor : LocalInspectionTool() {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
        cnt++
        Context.file = file.virtualFile
        return arrayOf()
    }
}