package dev.dreamhopping.coordinate.psi

import com.intellij.psi.*
import com.intellij.psi.util.TypeConversionUtil
import dev.dreamhopping.coordinate.MappingsHelper
import dev.dreamhopping.coordinate.MappingsHelper.findObfuscatedName

val PsiMember.obfuscatedName
    get() = when (this) {
        is PsiClass ->
            MappingsHelper.mappings.classes.firstOrNull {
                it.deobfuscatedName == owner
            }?.obfuscatedName
        is PsiMethod ->
            (this.findObfuscatedName() ?: containingClass?.findAllSupers()?.mapFirstNotNull {
                this.findObfuscatedName(otherOwner = it.owner)
            })?.obfuscatedName
        is PsiField ->
            MappingsHelper.mappings.fields.firstOrNull {
                it.deobfuscatedName == name && it.deobfuscatedOwner == owner
            }?.obfuscatedName
        else -> null
    }

val PsiMember.owner: String?
    get() =
        containingClass?.qualifiedName?.replace(".", "/")
            ?: (this as? PsiClass)?.qualifiedName?.replace(".", "/")

val PsiMethod.descriptor: String
    get() =
        buildString {
            append('(')
            parameterList.parameters.forEach { append(it.type.descriptor) }
            append(')')
            append((returnType ?: PsiType.VOID).descriptor)
        }

val PsiType.descriptor: String?
    get() = when (this) {
        is PsiArrayType -> "[${componentType.descriptor}"
        is PsiPrimitiveType -> "$descriptorType"
        is PsiClassType -> {
            val clazz = (TypeConversionUtil.erasure(this) as PsiClassType).resolve()
            if (clazz != null) "L${clazz.qualifiedName?.replace(".", "/")};" else null
        }
        else -> null
    }

val PsiPrimitiveType.descriptorType: Char?
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

inline fun <T, R> Collection<T>.mapFirstNotNull(transform: (T) -> R): R? {
    var item: R? = null
    for (entry in this) {
        item = transform(entry)
        if (item != null) break
    }

    return item
}

fun PsiClass.findAllSupers(): MutableList<PsiClass> {
    val list = this.supers.toMutableList()
    list.addAll(this.implementsListTypes.map { (TypeConversionUtil.erasure(it) as PsiClassType).resolve() })

    val supers = list.toTypedArray()
    return arrayOf(*supers, *supers.flatMap { it.findAllSupers().asIterable() }.toTypedArray()).distinct()
        .toMutableList()
}
