package dev.dreamhopping.coordinate.mappings.impl.mojang.version

import kotlinx.serialization.KSerializer
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
