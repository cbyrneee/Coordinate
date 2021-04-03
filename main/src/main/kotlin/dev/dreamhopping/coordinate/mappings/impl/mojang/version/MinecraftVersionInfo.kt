package dev.dreamhopping.coordinate.mappings.impl.mojang.version

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MinecraftVersionInfo(val downloads: Downloads? = null) {
    @Serializable
    data class Downloads(
        @SerialName("client_mappings")
        val clientMappings: Download? = null
    )

    @Serializable
    data class Download(val url: String? = null)
}
