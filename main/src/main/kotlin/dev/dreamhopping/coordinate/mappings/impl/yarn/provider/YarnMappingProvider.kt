package dev.dreamhopping.coordinate.mappings.impl.yarn.provider

import dev.dreamhopping.coordinate.mappings.MappingProvider
import dev.dreamhopping.coordinate.mappings.VersionMappings

class YarnMappingProvider : MappingProvider("yarn", "Yarn") {
    override suspend fun fetchLatestMappings(version: String): VersionMappings {
        TODO("Not yet implemented")
    }

    override suspend fun prepareForUsage() {
        TODO("Not yet implemented")
    }
}