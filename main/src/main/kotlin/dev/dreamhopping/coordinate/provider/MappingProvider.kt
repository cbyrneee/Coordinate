package dev.dreamhopping.coordinate.provider

import dev.dreamhopping.coordinate.Mappings
import dev.dreamhopping.coordinate.provider.mcp.MCPMappingProvider
import dev.dreamhopping.coordinate.provider.mojang.MojangMappingProvider
import dev.dreamhopping.coordinate.provider.yarn.YarnMappingProvider

/**
 * A class which all mapping providers will extend
 * This will allow Coordinate to get mappings from this source
 */
abstract class MappingProvider(val identifier: String, val displayName: String) {
    abstract suspend fun fetchLatestMappings(version: String): Mappings
    abstract suspend fun prepareForUsage()
}

enum class MappingProviderType(val provider: MappingProvider) {
    YARN(YarnMappingProvider()),
    MCP(MCPMappingProvider()),
    MOJANG(MojangMappingProvider())
}