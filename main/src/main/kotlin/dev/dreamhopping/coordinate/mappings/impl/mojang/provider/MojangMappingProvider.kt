package dev.dreamhopping.coordinate.mappings.impl.mojang.provider

import dev.dreamhopping.coordinate.mappings.MappingProvider
import dev.dreamhopping.coordinate.mappings.VersionMappings
import dev.dreamhopping.coordinate.mappings.impl.mojang.proguard.ProguardParser
import dev.dreamhopping.coordinate.mappings.impl.mojang.version.MinecraftVersionInfo
import dev.dreamhopping.coordinate.mappings.impl.mojang.version.provider.MinecraftVersionProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

class MojangMappingProvider : MappingProvider("mojang", "Mojang") {
    private val versionProvider = MinecraftVersionProvider()

    override suspend fun fetchLatestMappings(version: String): VersionMappings {
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
