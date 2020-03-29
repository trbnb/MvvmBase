package de.trbnb.mvvmbase.utils

import android.annotation.SuppressLint
import android.os.Binder
import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import java.io.Serializable

/**
 * Function that specifies which types allow for saving state in [de.trbnb.mvvmbase.bindableproperty.BindableProperty].
 */
@SuppressLint("NewApi")
inline fun <reified T> savingStateInBindableSupports(sdk: Int): Boolean {
    val clazz = T::class.java
    return when {
        BooleanArray::class.java.isAssignableFrom(clazz) ||
        ByteArray::class.java.isAssignableFrom(clazz) ||
        CharArray::class.java.isAssignableFrom(clazz) ||
        CharSequence::class.java.isAssignableFrom(clazz) ||
        Array<CharSequence>::class.java.isAssignableFrom(clazz) ||
        DoubleArray::class.java.isAssignableFrom(clazz) ||
        FloatArray::class.java.isAssignableFrom(clazz) ||
        IntArray::class.java.isAssignableFrom(clazz) ||
        LongArray::class.java.isAssignableFrom(clazz) ||
        ShortArray::class.java.isAssignableFrom(clazz) ||
        String::class.java.isAssignableFrom(clazz) ||
        Array<String>::class.java.isAssignableFrom(clazz) ||
        Binder::class.java.isAssignableFrom(clazz) ||
        Bundle::class.java.isAssignableFrom(clazz) ||
        Parcelable::class.java.isAssignableFrom(clazz) ||
        Array<Parcelable>::class.java.isAssignableFrom(clazz) ||
        Serializable::class.java.isAssignableFrom(clazz) -> true

        else -> sdk >= 21 && (Size::class.java.isAssignableFrom(clazz) || SizeF::class.java.isAssignableFrom(clazz))
    }
}
