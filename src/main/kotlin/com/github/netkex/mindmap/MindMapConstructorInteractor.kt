package com.github.netkex.mindmap

import com.github.netkex.mindmap.map.MindMapParserException
import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile

//import com.github.netkex.mindmap.map.MMParser
//import com.github.netkex.mindmap.map.MindMapParserException

/**
 * Implements an inspection to detect imports.
 */

var cnt = 0

class MindMapConstructorInteractor : LocalInspectionTool() {
//    private val parser = MMParser()
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
        cnt++
        if (Context.file?.name != file.virtualFile.name) {
//            Context.newFile.set(true)
        }
        Context.file = file.virtualFile
        println("In file ${file.name} was update: ${cnt}")
//        try {
//            val plan = parser.parse(file.text)
//            Context.plan = plan
//            println("MindMap Plan was updated by file ${file.name}")
//            Context.newFile.set(false)
//        } catch (e: MindMapParserException) { }

        return arrayOf()
    }
}