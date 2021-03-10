package dev.dreamhopping.coordinate.actions

import com.intellij.codeInsight.hint.HintManager
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ide.CopyPasteManager
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.psi.util.TypeConversionUtil
import dev.dreamhopping.coordinate.mappings.impl.mcp.provider.MCPMappingProvider
import kotlinx.coroutines.runBlocking
import java.awt.datatransfer.StringSelection


class GetMappedNameAction : AnAction() {
    companion object {
        val mappings = runBlocking {
            val mcpMappingProvider = MCPMappingProvider()
            mcpMappingProvider.prepareForUsage()

            return@runBlocking mcpMappingProvider.fetchLatestMappings("1.8.9")
        }
    }

    override fun actionPerformed(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val project = e.getRequiredData(CommonDataKeys.PROJECT)
        val caret = editor.caretModel.offset

        val psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.document) as? PsiJavaFile ?: return
        val method = PsiTreeUtil.getParentOfType(psiFile.findElementAt(caret), PsiMethod::class.java) ?: return
        val owner = method.containingClass?.qualifiedName?.replace(".", "/") ?: return

        var descriptor = "("
        method.parameterList.parameters.forEach {
            descriptor += it.type.descriptor
        }
        descriptor += ")${(method.returnType ?: PsiType.VOID).descriptor}"

        val obfName =
            mappings.methods.values.firstOrNull {
                it.deobfuscatedName == method.name && it.deobfuscatedOwner == owner && it.deobfuscatedDescriptor == descriptor
            }?.obfuscatedName

        if (obfName != null) {
            CopyPasteManager.getInstance().setContents(StringSelection(obfName))
            HintManager.getInstance()
                .showInformationHint(editor, "Obfuscated name is $obfName (copied to clipboard)")
        } else {
            HintManager.getInstance().showErrorHint(editor, "Couldn't find obfuscated name for ${method.name}")
        }
    }

    private val PsiType.descriptor: String?
        get() = when (this) {
            is PsiArrayType -> "[${this.componentType.descriptor}"
            is PsiPrimitiveType -> this.descriptorName.toString()
            is PsiClassType -> {
                val clazz = (TypeConversionUtil.erasure(this) as PsiClassType).resolve()
                if (clazz != null) "L${clazz.qualifiedName?.replace(".", "/")};" else null
            }
            else -> null
        }

    private val PsiPrimitiveType.descriptorName: Char?
        get() =
            when (this) {
                PsiType.LONG -> 'J'
                PsiType.SHORT -> 'S'
                PsiType.BOOLEAN -> 'Z'
                PsiType.VOID -> 'V'
                PsiType.BYTE -> 'B'
                PsiType.CHAR -> 'C'
                PsiType.DOUBLE -> 'D'
                PsiType.FLOAT -> 'F'
                PsiType.INT -> 'I'
                else -> null
            }
}
