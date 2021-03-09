package dev.dreamhopping.coordinate.mappings.impl.mcp.srg

import dev.dreamhopping.coordinate.mappings.VersionMappings

class SRGParser(private val srgText: String) {
    fun parse(): SRGInformation {
        val srgInformation = SRGInformation()
        srgText.lineSequence().forEach { line ->
            if (line.startsWith("PK: ") || line.isEmpty()) return@forEach

            val (type, info) = line.split(": ")
            val typeInformation = info.split(" ").toMutableList()

            when (type) {
                "CL" -> {
                    // This line is a class
                    // Format: obfName deobfName
                    srgInformation.classes.add(VersionMappings.MappedClass(typeInformation[0], typeInformation[1]))
                }
                "FD" -> {
                    // This line is a field
                    // Format: obfOwner/obfName deobfOwner/deobfName

                    val (obfOwner, obfName) = typeInformation[0].split("/")
                    val deobfOwner = typeInformation[1].substringBeforeLast("/")
                    val deobfName = typeInformation[1].substringAfterLast("/")
                    srgInformation.fields.add(VersionMappings.MappedField(obfOwner, obfName, deobfOwner, deobfName))
                }
                "MD" -> {
                    // This line is a method
                    // Format: obfName obfDescriptor deobfOwner/deobfName deobfDescriptor
                    val (obfOwner, obfName) = typeInformation[0].split("/")
                    val deobfOwner = typeInformation[2].substringBeforeLast("/")
                    val deobfName = typeInformation[2].substringAfterLast("/")

                    srgInformation.methods.add(
                        VersionMappings.MappedMethod(
                            obfName,
                            deobfName,
                            obfOwner,
                            deobfOwner,
                            typeInformation[1],
                            typeInformation[3]
                        )
                    )
                }
            }
        }

        return srgInformation
    }

    data class SRGInformation(
        val classes: MutableList<VersionMappings.MappedClass> = mutableListOf(),
        val methods: MutableList<VersionMappings.MappedMethod> = mutableListOf(),
        val fields: MutableList<VersionMappings.MappedField> = mutableListOf()
    )
}
