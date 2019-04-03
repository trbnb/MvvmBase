package de.trbnb.mvvmbase.commons

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

inline fun <reified T> Any.findGenericSuperclass(): ParameterizedType? {
    val genericSuperClass = javaClass.genericSuperclass

    if (genericSuperClass is ParameterizedType && genericSuperClass.rawType == T::class.java) {
        return genericSuperClass
    }

    return genericSuperClass?.findGenericSuperclass(T::class.java)
}

tailrec fun <T> Type.findGenericSuperclass(targetType: Class<T>): ParameterizedType? {
    if (this !is Class<*>) return null
    val genericSuperClass = this.genericSuperclass ?: return null

    if (genericSuperClass is ParameterizedType && genericSuperClass.rawType == targetType) {
        return genericSuperClass
    }

    return genericSuperClass.findGenericSuperclass(targetType)
}
