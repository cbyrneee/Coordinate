package dev.dreamhopping.coordinate.provider.yarn

import dev.dreamhopping.coordinate.Mappings
import dev.dreamhopping.coordinate.provider.MappingProvider

class YarnMappingProvider : MappingProvider("yarn", "Yarn") {
    override suspend fun fetchLatestMappings(version: String): Mappings {
        TODO("Not yet implemented")
    }

    override suspend fun prepareForUsage() {
        TODO("Not yet implemented")
    }
}