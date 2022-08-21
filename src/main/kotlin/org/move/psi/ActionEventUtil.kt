/*
 *  Copyright (c) 2002 Sabre, Inc. All rights reserved.
 */
package org.move.psi

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

object ActionEventUtil {
    fun getProject(event: AnActionEvent): Project? {
        return PlatformDataKeys.PROJECT.getData(event.dataContext)
    }

    fun getPsiElement(event: AnActionEvent): PsiElement? {
        return LangDataKeys.PSI_ELEMENT.getData(event.dataContext)
    }

    fun getEditor(event: AnActionEvent): Editor? {
        return PlatformDataKeys.EDITOR.getData(event.dataContext)
    }

    fun getPsiFile(event: AnActionEvent): PsiFile? {
        return LangDataKeys.PSI_FILE.getData(event.dataContext)
    }

    fun getVirtualFile(event: AnActionEvent): VirtualFile? {
        return PlatformDataKeys.VIRTUAL_FILE.getData(event.dataContext)
    }
}