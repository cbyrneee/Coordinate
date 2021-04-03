package dev.dreamhopping.coordinate.mappings.impl.mcp

import kotlinx.serialization.Serializable

typealias MCPVersionManifest = HashMap<String, MCPVersionManifestVersion>

@Serializable
data class MCPVersionManifestVersion(
    val snapshot: List<Long>,
    val stable: List<Long>
)
