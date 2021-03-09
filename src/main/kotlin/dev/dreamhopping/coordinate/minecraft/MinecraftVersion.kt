package dev.dreamhopping.coordinate.minecraft

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MinecraftVersion(
    @SerialName("id")
    val version: String,
    val type: MinecraftVersionType
)