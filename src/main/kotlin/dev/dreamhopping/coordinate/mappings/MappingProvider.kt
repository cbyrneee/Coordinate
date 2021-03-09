package dev.dreamhopping.coordinate.mappings

import dev.dreamhopping.coordinate.minecraft.MinecraftVersion

/**
 * A class which all mapping providers will extend
 * This will allow Coordinate to get mappings from this source
 */
abstract class MappingProvider(val identifier: String, val displayName: String) {
    abstract suspend fun fetchLatestMappings(version: MinecraftVersion): VersionMappings
    abstract suspend fun prepareForUsage()
}
