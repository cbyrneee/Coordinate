package dev.dreamhopping.coordinate.mappings.impl.mcp

import kotlinx.serialization.Serializable

@Serializable
data class MCPMapping(
    val obfuscatedName: String,
    val deobfuscatedName: String,
    val side: String,
    val description: String? = null
)
