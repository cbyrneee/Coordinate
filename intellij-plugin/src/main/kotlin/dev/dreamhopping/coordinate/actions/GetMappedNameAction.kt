package dev.dreamhopping.coordinate.actions

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiJavaFile
import com.intellij.psi.PsiJvmMember
import com.intellij.psi.PsiMember
import dev.dreamhopping.coordinate.MappingsHelper
import dev.dreamhopping.coordinate.psi.getMember
import dev.dreamhopping.coordinate.psi.obfuscatedName
import java.awt.datatransfer.StringSelection

class GetMappedNameAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val editor = event.getRequiredData(CommonDataKeys.EDITOR)
        val project = event.getRequiredData(CommonDataKeys.PROJECT)

        try {
            val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) as? PsiJavaFile
                ?: error("Could not find the current psi file")
            val member = editor.caretModel.getMember<PsiMember>(psiFile)
                ?: error("Could not find a member to map")
            val obfuscatedName = member.obfuscatedName
                ?: error("Could not find an obfuscated name for ${(member as PsiJvmMember).name}")

            CopyPasteManager.getInstance().setContents(StringSelection(obfuscatedName))
            HintManager.getInstance()
                .showInformationHint(editor, "Obfuscated name is $obfuscatedName (copied to clipboard)")
        } catch (t: Throwable) {
            t.message?.let { HintManager.getInstance().showErrorHint(editor, it) }
        }
    }

    override fun update(e: AnActionEvent) {
        this.isEnabledInModalContext = MappingsHelper.mappings != null
    }

    init {
        MappingsHelper.loadMappings()
    }
}
