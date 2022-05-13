package com.github.netkex.mindmap

import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile
//import com.github.netkex.mindmap.map.MMParser
//import com.github.netkex.mindmap.map.MindMapParserException

/**
 * Implements an inspection to detect imports.
 */
class MindMapConstructorInteractor : LocalInspectionTool() {
//    val parser = MMParser()
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
//        println("Was update: ${file.name}")
//        try {
//            val plan = parser.parse(file.text)
//            Context.plan.value = plan
//            println("MindMap Plan was updated by file ${file.name}")
//        } catch (e: MindMapParserException) {}
        return arrayOf()
    }
}