package dev.dreamhopping.coordinate.actions

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiMethod
import com.intellij.psi.util.PsiTreeUtil
import dev.dreamhopping.coordinate.MappingsHelper
import dev.dreamhopping.coordinate.psi.MappedPsiMethod
import java.awt.datatransfer.StringSelection

class GetMappedNameAction : AnAction() {
    init {
        MappingsHelper.mappings
    }

    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val project = event.getRequiredData(CommonDataKeys.PROJECT)
        val caret = editor.caretModel.offset

        try {
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) as? PsiJavaFile
                ?: error("Could not find the current psi file")
            val method = PsiTreeUtil.getParentOfType(psiFile.findElementAt(caret), PsiMethod::class.java) ?: return

            val mappedMethod = MappedPsiMethod(method)
            if (mappedMethod.obfuscatedName == null) error("Could not find an obfuscated name for ${mappedMethod.psiMethod.name}")

            CopyPasteManager.getInstance().setContents(StringSelection(mappedMethod.obfuscatedName))
            HintManager.getInstance()
                .showInformationHint(editor, "Obfuscated name is ${mappedMethod.obfuscatedName} (copied to clipboard)")
        } catch (e: Error) {
            if (e.message != null) HintManager.getInstance().showErrorHint(editor, e.message!!)
        }
    }
}

