package de.trbnb.mvvmbase.utils

import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.MvvmBase
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KProperty

/**
 * Searches for a given parameterized class type in the receivers type hierachy and returns it if it was found.
 * Returns `null` otherwise.
 */
inline fun <reified T> Any.findGenericSuperclass(): ParameterizedType? {
    val genericSuperClass = javaClass.genericSuperclass

    if (genericSuperClass is ParameterizedType && genericSuperClass.rawType == T::class.java) {
        return genericSuperClass
    }

    return genericSuperClass?.findGenericSuperclass(T::class.java)
}

/**
 * Searches for a given parameterized class type in the receivers hierachy and returns it if it was found.
 * Returns `null` otherwise.
 */
tailrec fun <T> Type.findGenericSuperclass(targetType: Class<T>): ParameterizedType? {
    if (this !is Class<*>) return null
    val genericSuperClass = this.genericSuperclass ?: return null

    if (genericSuperClass is ParameterizedType && genericSuperClass.rawType == targetType) {
        return genericSuperClass
    }

    return genericSuperClass.findGenericSuperclass(targetType)
}

/**
 * Finds the field ID of the given property.
 *
 * @see MvvmBase.init
 */
fun KProperty<*>.resolveFieldId(): Int = when (val detectedFieldId = MvvmBase.lookupFieldIdByName(brFieldName())) {
    null -> BR._all
    else -> detectedFieldId
}

/**
 * Converts a property name to a field name like the data binding compiler.
 *
 * See also:
 * https://android.googlesource.com/platform/frameworks/data-binding/+/master/compiler/src/main/java/android/databinding/annotationprocessor/ProcessBindable.java#216
 */
internal fun KProperty<*>.brFieldName(): String {
    val isBoolean = returnType.classifier == Boolean::class && !returnType.isMarkedNullable
    if (name.startsWith("is") && Character.isJavaIdentifierStart(name[2]) && isBoolean) {
        return name[2].toLowerCase() + name.substring(3)
    }

    return name
}
