package com.github.netkex.mindmap.services

import com.intellij.openapi.project.Project
import com.github.netkex.mindmap.MyBundle

class MyProjectService(project: Project) {
    init {
        println(MyBundle.message("projectService", project.name))
    }
}
