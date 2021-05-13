package dev.dreamhopping.coordinate.provider.mcp.version

import kotlinx.serialization.Serializable

typealias MCPVersionManifest = HashMap<String, MCPVersionManifestVersion>

enum class MCPMappingChannel(val value: String) {
    STABLE("stable"),
    SNAPSHOT("snapshot")
}

@Serializable
data class MCPVersionManifestVersion(
    val snapshot: List<Long>,
    val stable: List<Long>
)

@Serializable
data class MCPMapping(
    val obfuscatedName: String,
    val deobfuscatedName: String,
    val side: String,
    val description: String? = null
)
