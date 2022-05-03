package com.github.netkex.mindmap

import com.intellij.codeInspection.*
import com.intellij.psi.PsiFile
import org.jetbrains.kotlin.idea.structuralsearch.visitor.KotlinRecursiveElementVisitor
import org.jetbrains.kotlin.idea.util.ProjectRootsUtil
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtImportDirective


/**
 * Implements an inspection to detect imports.
 */
class MindMapConstructorInteractor : LocalInspectionTool() {
    override fun checkFile(file: PsiFile, manager: InspectionManager, isOnTheFly: Boolean): Array<ProblemDescriptor>? {
        println("Please do anything")
        println(file)
        println(file.fileType.description)
        println(file.text)
        println()
        println(file.project)
        return arrayOf()
    }

}