package dev.dreamhopping.coordinate

import dev.dreamhopping.coordinate.provider.MappingProviderType
import kotlinx.coroutines.runBlocking

object Coordinate {
    @JvmStatic
    suspend fun getMappingsAsync(version: String, providerType: MappingProviderType): Mappings {
        return providerType.provider.fetchLatestMappings(version)
    }

    /**
     * This method will automatically choose a mapping provider depending on the version
     * - Anything below 1.14: MCP
     * - 1.14 -> 1.16: Yarn
     * - 1.16+: Mojang
     */
    @JvmStatic
    suspend fun getMappingsAsync(version: String): Mappings {
        val parsedVersion = version.take(3).replace(".", "").toIntOrNull() ?: error("Failed to parse version $version")

        val provider = when {
            parsedVersion > 114 -> {
                MappingProviderType.MCP
            }
            parsedVersion > 116 -> {
                MappingProviderType.YARN
            }
            else -> {
                MappingProviderType.MOJANG
            }
        }

        return getMappingsAsync(version, provider)
    }

    /**
     * This method will automatically choose a mapping provider depending on the version
     * - Anything below 1.14: MCP
     * - 1.14 -> 1.16: Yarn
     * - 1.16+: Mojang
     *
     * This method will get mappings the same as [getMappingsAsync] but it will be blocking
     */
    @JvmStatic
    fun getMappings(version: String) = runBlocking { return@runBlocking getMappingsAsync(version) }

    /**
     * This method will get mappings the same as [getMappingsAsync] but it will be blocking
     */
    @JvmStatic
    fun getMappings(version: String, providerType: MappingProviderType) =
        runBlocking { return@runBlocking getMappingsAsync(version, providerType) }
}