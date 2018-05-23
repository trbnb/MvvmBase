package de.trbnb.mvvmbase

import kotlin.reflect.KProperty

/**
 * Converts a property name to a field name like the data binding compiler.
 *
 * See also:
 * https://android.googlesource.com/platform/frameworks/data-binding/+/master/compiler/src/main/java/android/databinding/annotationprocessor/ProcessBindable.java#216
 */
internal fun KProperty<*>.brFieldName(isBoolean: Boolean): String {
    if (name.startsWith("is") && Character.isJavaIdentifierStart(name[2]) && isBoolean) {
        return name[2].toLowerCase() + name.substring(3)
    }

    return name
}
