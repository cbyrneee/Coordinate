package dev.dreamhopping.coordinate.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.ui.DialogWrapper
import dev.dreamhopping.coordinate.mappings.impl.mcp.provider.MCPMappingProvider
import kotlinx.coroutines.runBlocking
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel


class GetMappedNameAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val editor: Editor = e.getRequiredData(CommonDataKeys.EDITOR)
        val selectedText = editor.caretModel.currentCaret.selectedText ?: return

        SampleDialogWrapper(selectedText).showAndGet()
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getRequiredData(CommonDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible = editor.caretModel.currentCaret.selectedText?.isNotEmpty() ?: false
    }

    internal class SampleDialogWrapper(val name: String) : DialogWrapper(true) {
        override fun createCenterPanel(): JComponent {
            val mappings = runBlocking {
                val mcpMappingProvider = MCPMappingProvider()
                mcpMappingProvider.prepareForUsage()

                return@runBlocking mcpMappingProvider.fetchLatestMappings("1.8.9")
            }

            val found = mappings.classes.values.firstOrNull { it.deobfuscatedName == name }?.obfuscatedName
                ?: mappings.fields.values.firstOrNull { it.deobfuscatedName == name }?.obfuscatedName
                ?: mappings.methods.values.firstOrNull { it.deobfuscatedName == name }?.obfuscatedName

            val dialogPanel = JPanel(BorderLayout())
            val label = JLabel("$name -> ${found ?: "Couldn't find method, class or field"}")
            label.preferredSize = Dimension(100, 100)

            dialogPanel.add(label, BorderLayout.CENTER)
            return dialogPanel
        }

        init {
            init()
            title = "Test DialogWrapper"
        }
    }
}
