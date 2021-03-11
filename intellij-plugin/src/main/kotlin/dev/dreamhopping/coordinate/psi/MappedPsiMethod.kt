package dev.dreamhopping.coordinate.psi

import com.intellij.psi.*
import com.intellij.psi.util.TypeConversionUtil
import dev.dreamhopping.coordinate.MappingsHelper

class MappedPsiMethod(val psiMethod: PsiMethod) {
    val owner: String?
        get() = psiMethod.containingClass?.qualifiedName?.replace(".", "/")

    val obfuscatedName: String?
        get() =
            MappingsHelper.mappings.methods.values.firstOrNull {
                it.deobfuscatedName == psiMethod.name && it.deobfuscatedOwner == owner && it.deobfuscatedDescriptor == descriptor
            }?.obfuscatedName

    val descriptor: String
        get() =
            buildString {
                append('(')
                psiMethod.parameterList.parameters.forEach { append(it.type.descriptor) }
                append(')')
                append((psiMethod.returnType ?: PsiType.VOID).descriptor)
            }

    private val PsiType.descriptor: String?
        get() = when (this) {
            is PsiArrayType -> "[${componentType.descriptor}"
            is PsiPrimitiveType -> "$descriptorType"
            is PsiClassType -> {
                val clazz = (TypeConversionUtil.erasure(this) as PsiClassType).resolve()
                if (clazz != null) "L${clazz.qualifiedName?.replace(".", "/")};" else null
            }
            else -> null
        }

    private val PsiPrimitiveType.descriptorType: Char?
        get() =
            when (this) {
                PsiType.LONG -> 'J'
                PsiType.SHORT -> 'S'
                PsiType.BOOLEAN -> 'Z'
                PsiType.VOID -> 'V'
                PsiType.BYTE -> 'B'
                PsiType.CHAR -> 'C'
                PsiType.DOUBLE -> 'D'
                PsiType.FLOAT -> 'F'
                PsiType.INT -> 'I'
                else -> null
            }
}
