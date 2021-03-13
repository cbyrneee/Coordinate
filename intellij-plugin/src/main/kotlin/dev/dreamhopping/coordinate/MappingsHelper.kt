package dev.dreamhopping.coordinate

import com.intellij.psi.PsiMethod
import dev.dreamhopping.coordinate.mappings.VersionMappings
import dev.dreamhopping.coordinate.mappings.impl.mcp.provider.MCPMappingProvider
import dev.dreamhopping.coordinate.psi.descriptor
import dev.dreamhopping.coordinate.psi.owner
import kotlinx.coroutines.runBlocking

object MappingsHelper {
    var areMappingsLoaded = false

    val mappings: VersionMappings by lazy {
        runBlocking {
            val mcpMappingProvider = MCPMappingProvider()
            mcpMappingProvider.prepareForUsage()

            val mappings = mcpMappingProvider.fetchLatestMappings("1.8.9")
            areMappingsLoaded = true

            mappings
        }
    }

    fun PsiMethod.findObfuscatedName(
        otherOwner: String? = null,
        otherName: String? = null,
        otherDesc: String? = null
    ): VersionMappings.MappedMethod? {
        return mappings.methods.firstOrNull {
            it.deobfuscatedName == (otherName ?: name) && it.deobfuscatedOwner == (otherOwner
                ?: owner) && it.deobfuscatedDescriptor == (otherDesc ?: descriptor)
        }
    }
}
