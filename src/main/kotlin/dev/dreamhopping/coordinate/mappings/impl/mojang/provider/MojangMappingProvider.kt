package dev.dreamhopping.coordinate.mappings.impl.mojang.provider

import dev.dreamhopping.coordinate.mappings.MappingProvider
import dev.dreamhopping.coordinate.mappings.VersionMappings
import dev.dreamhopping.coordinate.mappings.impl.mojang.version.MinecraftVersionInfo
import dev.dreamhopping.coordinate.mappings.impl.mojang.version.provider.MinecraftVersionProvider
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

class MojangMappingProvider : MappingProvider("mojang", "Mojang") {
    private val versionProvider = MinecraftVersionProvider()
    private val validName = "[a-zA-Z0-9_\\-.$<>]+"
    private val aType = "$validName(?:\\[])*"
    private val aTypeList = "|(?:(?:$aType,)*$aType)"
    private val aClass = "($validName) -> ($validName):".toPattern()
    private val aField = " {4}($aType) ($validName) -> ($validName)".toPattern()
    private val aMethod = " {4}(?:[0-9]+:[0-9]+:)?($aType) ($validName)\\(($aTypeList)\\) -> ($validName)".toPattern()

    override suspend fun fetchLatestMappings(version: String): VersionMappings {
        val versionManifest = versionProvider.getVersion(version) ?: error("No mappings available for $version")
        val versionInfo: MinecraftVersionInfo =
            Json { ignoreUnknownKeys = true }
                .decodeFromString(URL(versionManifest.versionUrl).readText())
                ?: error("No mappings available for $version")

        val clientMappingInfo = versionInfo.downloads?.clientMappings
            ?: error("$version does not support mojang mappings")

        val classes = mutableListOf<VersionMappings.MappedClass>()
        val methods = mutableListOf<VersionMappings.MappedMethod>()
        val fields = mutableListOf<VersionMappings.MappedField>()
        var currentClass: VersionMappings.MappedClass? = null

        URL(clientMappingInfo.url).readText().lineSequence().forEach { line ->
            if (line.isEmpty() || line.startsWith("#")) return@forEach

            val classMatcher = aClass.matcher(line)
            val methodMatcher = aMethod.matcher(line)
            val fieldMatcher = aField.matcher(line)

            when {
                classMatcher.matches() -> {
                    val deobfuscatedName = classMatcher.group(1).replace(".", "/")
                    val obfuscatedName = classMatcher.group(2)

                    currentClass = VersionMappings.MappedClass(obfuscatedName, deobfuscatedName)
                    classes.add(currentClass!!)
                }

                methodMatcher.matches() -> {
                    val returnType = methodMatcher.group(1).replace(".", "/")
                    val deobfuscatedName = methodMatcher.group(2).replace(".", "/")
                    val params = methodMatcher.group(3).split(",")
                    val obfuscatedName = methodMatcher.group(4)

                    methods.add(
                        VersionMappings.MappedMethod(
                            obfuscatedName,
                            deobfuscatedName,
                            currentClass?.obfuscatedName ?: "",
                            currentClass?.deobfuscatedName ?: "",
                            deobfuscatedDescriptor = getMethodDescriptor(params, returnType)
                        )
                    )
                }

                fieldMatcher.matches() -> {
                    val deobfuscatedName = fieldMatcher.group(2).replace(".", "/")
                    val obfuscatedName = fieldMatcher.group(3)

                    fields.add(
                        VersionMappings.MappedField(
                            currentClass?.obfuscatedName ?: "",
                            obfuscatedName,
                            currentClass?.deobfuscatedName ?: "",
                            deobfuscatedName
                        )
                    )
                }
            }
        }

        return VersionMappings(classes, methods, fields)
    }

    override suspend fun prepareForUsage() {
        versionProvider.fetchManifest()
    }

    private fun getMethodDescriptor(params: List<String>, returnType: String) =
        buildString {
            append('(')
            params.forEach {
                append(it.replace('.', '/').toDescriptor())
            }
            append(')')
            append(returnType.toDescriptor())
        }

    private fun String.toDescriptor() =
        buildString {
            var typeString = this@toDescriptor
            if (typeString.isEmpty()) return@buildString

            if (this.endsWith("[]")) {
                append('[')
                typeString = this@toDescriptor.take(typeString.length - 2)
            }

            val primitive = typeString.primitiveType
            if (primitive == null) append('L')
            append(primitive ?: typeString)
            if (primitive == null) append(';')
        }

    private val String.primitiveType: String?
        get() {
            return when (this) {
                "byte" -> "B"
                "char" -> "C"
                "short" -> "S"
                "int" -> "I"
                "long" -> "J"
                "float" -> "F"
                "double" -> "D"
                "boolean" -> "Z"
                "void" -> "V"
                else -> null
            }
        }
}
