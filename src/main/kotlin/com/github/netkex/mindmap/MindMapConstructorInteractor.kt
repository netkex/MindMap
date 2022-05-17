package com.github.netkex.mindmap

import com.github.netkex.mindmap.map.MMParser
import com.github.netkex.mindmap.map.MindMapParserException
import com.github.netkex.mindmap.map.replaceMap
import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile


var cnt = 0
class MindMapConstructorInteractor : LocalInspectionTool() {
    private val parser = MMParser()
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor> {
        cnt++
        println("In file ${file.name} was update: ${cnt}")
        println("${Thread.currentThread()}")
        println("WindowProcessing: ${Context.windowProcessing}")
        if (Context.windowProcessing.get())
            return arrayOf()

        Context.fileProcessing.set(true)
        Context.file = file.virtualFile
        println("In file ${file.name} was update: ${cnt}")
        try {
            val plan = parser.parse(file.text)

            println("Parsed plan:")
            plan.forEach { it -> println("${it.text}: ${it.hashCode()}") }
            println()

            Context.plan.replaceMap(plan)
            println("MindMap Plan was updated by file ${file.name}")

        } catch (e: MindMapParserException) { }
        Context.fileProcessing.set(false)
        return arrayOf()
    }
}