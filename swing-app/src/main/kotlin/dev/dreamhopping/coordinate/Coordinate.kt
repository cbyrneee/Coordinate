package dev.dreamhopping.coordinate

import com.formdev.flatlaf.FlatDarkLaf
import dev.dreamhopping.coordinate.mappings.VersionMappings
import dev.dreamhopping.coordinate.mappings.impl.mcp.provider.MCPMappingProvider
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.ExperimentalSerializationApi
import java.awt.Component
import java.awt.Dimension
import java.awt.GridBagLayout
import javax.swing.*

/**
 * Coordinate is a Minecraft Mapping Viewer with support for MCP, Yarn and Mojang mappings
 * @author Conor Byrne (dreamhopping)
 */
@ExperimentalSerializationApi
object Coordinate {
    @JvmStatic
    fun main(args: Array<String>) {
        FlatDarkLaf.install()

        val frame = CoordinateFrame()
        frame.isVisible = true

        runBlocking {
            val mcpMappingProvider = MCPMappingProvider()
            mcpMappingProvider.prepareForUsage()

            frame.versionMappings = mcpMappingProvider.fetchLatestMappings("1.8.9")
            frame.contentPane = CoordinateFrame.SearchPanel()

            frame.invalidate()
            frame.revalidate()
        }
    }
}

class CoordinateFrame : JFrame() {
    lateinit var versionMappings: VersionMappings

    init {
        setDefaultLookAndFeelDecorated(true)

        title = "Coordinate"
        defaultCloseOperation = EXIT_ON_CLOSE

        setSize(640, 360)
        setLocationRelativeTo(null)

        contentPane = LoadingPanel()
    }

    class LoadingPanel : JPanel() {
        init {
            layout = GridBagLayout()

            val label = JLabel("Loading mappings for 1.8.9...")
            add(label)
        }
    }

    class SearchPanel : JPanel() {
        init {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            val result = JTextArea("Result: ")
            result.alignmentX = Component.CENTER_ALIGNMENT
            result.lineWrap = true
            result.isVisible = false

            val label = JLabel("Search for a method, class or field")
            label.alignmentX = Component.CENTER_ALIGNMENT

            val textInput = JTextField()
            textInput.addActionListener {
                val mappings = (SwingUtilities.getWindowAncestor(this) as CoordinateFrame).versionMappings

                result.text =
                    mappings.classes[textInput.text]?.toString() ?: mappings.methods[textInput.text]?.toString()
                            ?: mappings.fields[textInput.text]?.toString() ?: "Couldn't find a method, class or field"

                result.isVisible = true
            }


            add(Box.createVerticalGlue())
            add(Box.createRigidArea(Dimension(0, 10)))
            add(label)
            add(Box.createRigidArea(Dimension(0, 10)))
            add(textInput)
            add(Box.createRigidArea(Dimension(0, 10)))
            add(result)
            add(Box.createRigidArea(Dimension(0, 10)))
            add(Box.createVerticalGlue())
        }
    }
}