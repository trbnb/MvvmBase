package de.trbnb.mvvmbase.utils

import android.os.Binder
import android.os.Bundle
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import java.io.Serializable

/**
 * Function that specifies which types allow for saving state in [de.trbnb.mvvmbase.bindableproperty.BindableProperty].
 */
inline fun <reified T> savingStateInBindableSupports(sdk: Int): Boolean {
    return when (val clazz = T::class.java) {
        java.lang.Boolean::class.java,
        BooleanArray::class.java,
        java.lang.Byte::class.java,
        ByteArray::class.java,
        java.lang.Character::class.java,
        CharArray::class.java,
        CharSequence::class.java,
        Array<CharSequence>::class.java,
        java.lang.Double::class.java,
        DoubleArray::class.java,
        java.lang.Float::class.java,
        FloatArray::class.java,
        java.lang.Integer::class.java,
        IntArray::class.java,
        java.lang.Long::class.java,
        LongArray::class.java,
        java.lang.Short::class.java,
        ShortArray::class.java,
        String::class.java,
        Array<String>::class.java,
        Binder::class.java,
        Bundle::class.java,
        Parcelable::class.java,
        Array<Parcelable>::class.java,
        Serializable::class.java -> true

        else -> sdk >= 21 && (clazz == Size::class.java || clazz == SizeF::class.java)
    }
}
