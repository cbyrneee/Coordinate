package dev.dreamhopping.coordinate.mappings.impl.yarn.version.provider

import dev.dreamhopping.coordinate.mappings.impl.yarn.version.YarnMappingVersionInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

/**
 * Fetches information about yarn version from meta.fabricmc.net
 */
class YarnVersionProvider {
    private val mappingManifestUrl = URL("https://meta.fabricmc.net/v1/versions/mappings")
    private val mavenRepoUrl = "https://maven.fabricmc.net/"

    private var versionManifest = listOf<YarnMappingVersionInfo>()

    suspend fun fetchManifest() {
        withContext(Dispatchers.IO) {
            try {
                versionManifest = Json { ignoreUnknownKeys = true }.decodeFromString(mappingManifestUrl.readText())
            } catch (t: Throwable) {
                throw Exception("Failed to get version manifest!", t)
            }
        }
    }

    fun getMappingInfoForVersion(gameVersion: String) = versionManifest.firstOrNull { it.gameVersion == gameVersion }
    fun getMappingJarForVersion(gameVersion: String): ByteArray? {
        val mappingInfo = getMappingInfoForVersion(gameVersion) ?: return null
        return URL(mavenRepoUrl + "net/fabricmc/yarn/${gameVersion}${mappingInfo.separator}${mappingInfo.build}/yarn-${gameVersion}${mappingInfo.separator}${mappingInfo.build}-v2.jar").readBytes()
    }
}
