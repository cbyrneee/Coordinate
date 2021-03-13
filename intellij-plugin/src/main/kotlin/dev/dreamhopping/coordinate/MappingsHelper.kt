package dev.dreamhopping.coordinate

import com.intellij.psi.PsiMethod
import dev.dreamhopping.coordinate.mappings.VersionMappings
import dev.dreamhopping.coordinate.mappings.impl.mcp.provider.MCPMappingProvider
import dev.dreamhopping.coordinate.psi.descriptor
import dev.dreamhopping.coordinate.psi.owner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object MappingsHelper {
    var mappings: VersionMappings? = null

    fun loadMappings() {
        GlobalScope.launch(Dispatchers.IO) {
            with(MCPMappingProvider()) {
                prepareForUsage()
                mappings = fetchLatestMappings("1.8.9")
            }
        }
    }

    fun PsiMethod.findObfuscatedName(
        otherOwner: String? = null,
        otherName: String? = null,
        otherDesc: String? = null
    ): VersionMappings.MappedMethod? {
        return mappings?.methods?.firstOrNull {
            it.deobfuscatedName == (otherName ?: name) && it.deobfuscatedOwner == (otherOwner
                ?: owner) && it.deobfuscatedDescriptor == (otherDesc ?: descriptor)
        }
    }
}
