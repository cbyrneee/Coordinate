package dev.dreamhopping.coordinate.mappings.impl.mojang.version

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MinecraftVersion(
    @SerialName("id")
    val version: String,
    @SerialName("url")
    val versionUrl: String,
    val type: MinecraftVersionType,
)