package com.github.netkex.mindmap

import com.github.netkex.mindmap.map.MMParser
import com.github.netkex.mindmap.map.MindMapParserException
//import com.github.netkex.mindmap.map.replaceMap
import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile


var cnt = 0
class MindMapConstructorInteractor : LocalInspectionTool() {
    private val parser = MMParser()
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
        cnt++
        Context.file = file.virtualFile
        return arrayOf()
    }
}