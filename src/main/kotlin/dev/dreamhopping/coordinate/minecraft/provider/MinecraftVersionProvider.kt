package dev.dreamhopping.coordinate.minecraft.provider

import dev.dreamhopping.coordinate.Coordinate
import dev.dreamhopping.coordinate.minecraft.MinecraftVersion
import dev.dreamhopping.coordinate.minecraft.MinecraftVersionManifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import java.net.URL

/**
 * Fetches the latest minecraft versions from version_manifest_v2.json
 */
class MinecraftVersionProvider {
    private val versionManifestUrl = URL("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json")
    lateinit var versionManifest: MinecraftVersionManifest

    suspend fun fetchManifest() {
        withContext(Dispatchers.IO) {
            try {
                versionManifest = Coordinate.json.decodeFromString(versionManifestUrl.readText())
            } catch (t: Throwable) {
                throw Exception("Failed to get version manifest!", t)
            }
        }
    }

    fun getVersion(version: String): MinecraftVersion? = versionManifest.versions.firstOrNull { it.version == version }
}