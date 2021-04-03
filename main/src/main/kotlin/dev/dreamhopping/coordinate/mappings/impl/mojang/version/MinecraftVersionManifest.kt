package dev.dreamhopping.coordinate.mappings.impl.mojang.version

import kotlinx.serialization.Serializable

@Serializable
data class MinecraftVersionManifest(val latest: Latest, val versions: List<MinecraftVersion>) {
    @Serializable
    data class Latest(val release: String, val snapshot: String)
}