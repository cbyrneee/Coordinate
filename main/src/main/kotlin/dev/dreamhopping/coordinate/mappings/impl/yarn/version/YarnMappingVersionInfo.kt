package dev.dreamhopping.coordinate.mappings.impl.yarn.version

import kotlinx.serialization.Serializable

@Serializable
data class YarnMappingVersionInfo(
    val gameVersion: String? = null,
    val separator: String? = null,
    val build: Int? = null,
    val maven: String? = null,
    val version: String? = null,
    val stable: Boolean? = null
)
