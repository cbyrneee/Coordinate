package dev.dreamhopping.coordinate.provider.mojang.version

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = MinecraftVersionType.Serializer::class)
enum class MinecraftVersionType(val value: String) {
    OLD_ALPHA("old_alpha"),
    OLD_BETA("old_beta"),
    RELEASE("release"),
    SNAPSHOT("snapshot");

    object Serializer : KSerializer<MinecraftVersionType> {
        override val descriptor: SerialDescriptor
            get() {
                return PrimitiveSerialDescriptor(
                    MinecraftVersionType::class.java.name,
                    PrimitiveKind.STRING
                )
            }

        override fun deserialize(decoder: Decoder): MinecraftVersionType = when (val value = decoder.decodeString()) {
            "old_alpha" -> OLD_ALPHA
            "old_beta" -> OLD_BETA
            "release" -> RELEASE
            "snapshot" -> SNAPSHOT
            else -> throw IllegalArgumentException("Type could not parse: $value")
        }

        override fun serialize(encoder: Encoder, value: MinecraftVersionType) {
            return encoder.encodeString(value.value)
        }
    }
}

@Serializable
data class MinecraftVersionManifest(val latest: Latest, val versions: List<MinecraftVersion>) {
    @Serializable
    data class Latest(val release: String, val snapshot: String)
}

@Serializable
data class MinecraftVersion(
    @SerialName("id")
    val version: String,
    @SerialName("url")
    val versionUrl: String,
    val type: MinecraftVersionType,
)

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
