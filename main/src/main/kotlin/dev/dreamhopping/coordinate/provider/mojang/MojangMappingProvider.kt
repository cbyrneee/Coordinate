package dev.dreamhopping.coordinate.provider.mojang

import dev.dreamhopping.coordinate.Mappings
import dev.dreamhopping.coordinate.provider.MappingProvider
import dev.dreamhopping.coordinate.provider.mojang.parser.ProguardParser
import dev.dreamhopping.coordinate.provider.mojang.version.MinecraftVersionInfo
import dev.dreamhopping.coordinate.provider.mojang.version.MinecraftVersionProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

class MojangMappingProvider : MappingProvider("mojang", "Mojang") {
    private val versionProvider = MinecraftVersionProvider()

    override suspend fun fetchLatestMappings(version: String): Mappings {
        val versionManifest = versionProvider.getVersion(version) ?: error("No mappings available for $version")
        val versionInfo: MinecraftVersionInfo =
            Json { ignoreUnknownKeys = true }
                .decodeFromString(URL(versionManifest.versionUrl).readText())
                ?: error("No mappings available for $version")

        val clientMappingInfo = versionInfo.downloads?.clientMappings
            ?: error("$version does not support mojang mappings")

        val proguardParser = ProguardParser(URL(clientMappingInfo.url).readText())
        return proguardParser.parse()
    }

    override suspend fun prepareForUsage() {
        versionProvider.fetchManifest()
    }
}
