package dev.dreamhopping.coordinate.provider.yarn.version

import kotlinx.serialization.Serializable

@Serializable
data class YarnVersionInfo(
    val gameVersion: String? = null,
    val separator: String? = null,
    val build: Int? = null,
    val maven: String? = null,
    val version: String? = null,
    val stable: Boolean? = null
)
