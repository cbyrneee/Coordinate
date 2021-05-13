package dev.dreamhopping.coordinate.provider.mcp

import dev.dreamhopping.coordinate.Mappings
import dev.dreamhopping.coordinate.provider.MappingProvider
import dev.dreamhopping.coordinate.provider.mcp.parser.SRGParser
import dev.dreamhopping.coordinate.provider.mcp.version.MCPMapping
import dev.dreamhopping.coordinate.provider.mcp.version.MCPMappingChannel
import dev.dreamhopping.coordinate.provider.mcp.version.MCPVersionManifest
import dev.dreamhopping.coordinate.util.ZipFileUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

@ExperimentalSerializationApi
class MCPMappingProvider : MappingProvider("mcp", "MCP") {
    private lateinit var versionManifest: MCPVersionManifest

    private val versionManifestUrl = URL("http://export.mcpbot.bspk.rs/versions.json")

    private val mappingUrlTemplate =
        "http://export.mcpbot.bspk.rs/mcp_CHANNEL/MAPPINGVERSION-MCVERSION/mcp_CHANNEL-MAPPINGVERSION-MCVERSION.zip"
    private val srgUrlTemplate = "http://export.mcpbot.bspk.rs/mcp/MCVERSION/mcp-MCVERSION-srg.zip"

    override suspend fun fetchLatestMappings(version: String): Mappings {
        val mcpMappings = fetchLatestStableMappings(version)
        val srgMappings = fetchLatestSrgMappings(version)

        val methods = srgMappings.methods.map {
            // A "deobfuscated name" for srg is an "obfuscated name" for mcp
            val betterDeobfName =
                mcpMappings.methods.firstOrNull { mapping -> mapping.obfuscatedName == it.deobfuscatedName }?.deobfuscatedName
                    ?: it.deobfuscatedName

            it.obfuscatedName = it.deobfuscatedName
            it.deobfuscatedName = betterDeobfName
            it
        }

        val fields = srgMappings.fields.map {
            // A "deobfuscated name" for srg is an "obfuscated name" for mcp
            val betterDeobfName =
                mcpMappings.fields.firstOrNull { mapping -> mapping.obfuscatedName == it.deobfuscatedName }?.deobfuscatedName
                    ?: it.deobfuscatedName

            it.obfuscatedName = it.deobfuscatedName
            it.deobfuscatedName = betterDeobfName
            it
        }

        return Mappings(
            srgMappings.classes,
            methods,
            fields
        )
    }

    override suspend fun prepareForUsage() {
        withContext(Dispatchers.IO) {
            try {
                versionManifest = Json.decodeFromString(versionManifestUrl.readText())
            } catch (t: Throwable) {
                throw Exception("Failed to get MCP Versions!", t)
            }
        }
    }

    private suspend fun fetchLatestStableMappings(version: String) =
        withContext(Dispatchers.IO) {
            val manifestVersion = versionManifest[version]
                ?: error("There are no mappings available for $version")
            val latestStable = manifestVersion.stable.firstOrNull()?.toString()
                ?: error("There are no stable mappings available for $version")
            val mappingsUrl = URL(getMCPMappingUrl(MCPMappingChannel.STABLE, version, latestStable))
            val mcpMappings = MCPMappings()

            try {
                ZipFileUtil(mappingsUrl).use {
                    mcpMappings.fields = Csv.decodeFromString(it.readEntryText("fields.csv"))
                    mcpMappings.methods = Csv.decodeFromString(it.readEntryText("methods.csv"))
                    mcpMappings.params = Csv.decodeFromString(it.readEntryText("params.csv"))
                }
            } catch (t: Throwable) {
                error("Failed to get mappings for $version (stable ${latestStable})\n${t.stackTraceToString()}")
            }

            mcpMappings
        }

    private suspend fun fetchLatestSrgMappings(version: String) =
        withContext(Dispatchers.IO) {
            val srgUrl = URL(getSrgMappingUrl(version))
            var srgInformation: SRGParser.SRGInformation

            ZipFileUtil(srgUrl).use {
                srgInformation = SRGParser(it.readEntryText("joined.srg")).parse()
            }

            srgInformation
        }

    private fun getMCPMappingUrl(
        channel: MCPMappingChannel,
        minecraftVersion: String,
        mappingVersion: String
    ) = mappingUrlTemplate
        .replace("CHANNEL", channel.value)
        .replace("MAPPINGVERSION", mappingVersion)
        .replace("MCVERSION", minecraftVersion)

    private fun getSrgMappingUrl(minecraftVersion: String) = srgUrlTemplate.replace("MCVERSION", minecraftVersion)

    data class MCPMappings(
        var methods: List<MCPMapping> = listOf(),
        var fields: List<MCPMapping> = listOf(),
        var params: List<MCPMapping> = listOf()
    )
}
