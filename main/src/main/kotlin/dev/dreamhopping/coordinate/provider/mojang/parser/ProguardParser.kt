package dev.dreamhopping.coordinate.provider.mojang.parser

import dev.dreamhopping.coordinate.Mappings

class ProguardParser(private var proguardText: String) {
    private val validName = "[a-zA-Z0-9_\\-.$<>]+"
    private val aType = "$validName(?:\\[])*"
    private val aTypeList = "|(?:(?:$aType,)*$aType)"
    private val aClass = "($validName) -> ($validName):".toPattern()
    private val aField = " {4}($aType) ($validName) -> ($validName)".toPattern()
    private val aMethod = " {4}(?:[0-9]+:[0-9]+:)?($aType) ($validName)\\(($aTypeList)\\) -> ($validName)".toPattern()

    fun parse(): Mappings {
        val classes = mutableListOf<Mappings.MappedClass>()
        val methods = mutableListOf<Mappings.MappedMethod>()
        val fields = mutableListOf<Mappings.MappedField>()
        var currentClass: Mappings.MappedClass? = null

        proguardText.lineSequence().forEach { line ->
            if (line.isEmpty() || line.startsWith("#")) return@forEach

            val classMatcher = aClass.matcher(line)
            val methodMatcher = aMethod.matcher(line)
            val fieldMatcher = aField.matcher(line)

            when {
                classMatcher.matches() -> {
                    val deobfuscatedName = classMatcher.group(1).replace(".", "/")
                    val obfuscatedName = classMatcher.group(2)

                    currentClass = Mappings.MappedClass(obfuscatedName, deobfuscatedName)
                    classes.add(currentClass!!)
                }

                methodMatcher.matches() -> {
                    val returnType = methodMatcher.group(1).replace(".", "/")
                    val deobfuscatedName = methodMatcher.group(2).replace(".", "/")
                    val params = methodMatcher.group(3).split(",")
                    val obfuscatedName = methodMatcher.group(4)

                    methods.add(
                        Mappings.MappedMethod(
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
                        Mappings.MappedField(
                            currentClass?.obfuscatedName ?: "",
                            obfuscatedName,
                            currentClass?.deobfuscatedName ?: "",
                            deobfuscatedName
                        )
                    )
                }
            }
        }

        return Mappings(classes, methods, fields)
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