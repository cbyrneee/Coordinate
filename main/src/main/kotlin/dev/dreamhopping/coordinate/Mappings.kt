package dev.dreamhopping.coordinate

/**
 * Includes all mappings for a certain version
 */
data class Mappings(
    val classes: List<MappedClass>,
    val methods: List<MappedMethod>,
    val fields: List<MappedField>,
) {
    val allMappings: List<MappedNode> = classes + methods + fields

    open class MappedNode(
        open val obfuscatedName: String,
        open val deobfuscatedName: String,
    )

    data class MappedClass(override val obfuscatedName: String, override val deobfuscatedName: String) :
        MappedNode(obfuscatedName, deobfuscatedName)

    data class MappedMethod(
        override var obfuscatedName: String,
        override var deobfuscatedName: String,
        val obfuscatedOwner: String,
        val deobfuscatedOwner: String,
        val obfuscatedDescriptor: String? = null,
        val deobfuscatedDescriptor: String
    ) : MappedNode(obfuscatedName, deobfuscatedName)

    data class MappedField(
        override var obfuscatedName: String,
        override var deobfuscatedName: String,
        val obfuscatedOwner: String,
        val deobfuscatedOwner: String,
    ) : MappedNode(obfuscatedName, deobfuscatedName)
}
