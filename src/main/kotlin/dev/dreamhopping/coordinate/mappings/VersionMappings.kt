package dev.dreamhopping.coordinate.mappings

/**
 * Includes all mappings for a certain version
 */
data class VersionMappings(
    val classes: Map<String, MappedClass>,
    val methods: Map<String, MappedMethod>,
    val fields: Map<String, MappedField>
) {
    data class MappedClass(val obfuscatedName: String, val deobfuscatedName: String)

    data class MappedMethod(
        var obfuscatedName: String,
        var deobfuscatedName: String,
        val obfuscatedOwner: String,
        val deobfuscatedOwner: String,
        val obfuscatedDescriptor: String,
        val deobfuscatedDescriptor: String
    )

    data class MappedField(
        val obfuscatedOwner: String,
        val obfuscatedName: String,
        val deobfuscatedOwner: String,
        val deobfuscatedName: String
    )
}
