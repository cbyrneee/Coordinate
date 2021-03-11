package dev.dreamhopping.coordinate

import dev.dreamhopping.coordinate.mappings.VersionMappings
import dev.dreamhopping.coordinate.mappings.impl.mcp.provider.MCPMappingProvider
import kotlinx.coroutines.runBlocking

object MappingsHelper {
    val mappings: VersionMappings by lazy {
        runBlocking {
            val mcpMappingProvider = MCPMappingProvider()
            mcpMappingProvider.prepareForUsage()

            return@runBlocking mcpMappingProvider.fetchLatestMappings("1.8.9")
        }
    }
}
