package de.trbnb.mvvmbase.databinding.test

import android.os.Build
import java.lang.reflect.Field
import java.lang.reflect.Modifier

fun setSdkVersion(version: Int) {
    val field = Build.VERSION::class.java.getField(Build.VERSION::SDK_INT.name).apply {
        isAccessible = true
    }

    Field::class.java.getDeclaredField("modifiers").apply {
        isAccessible = true
    }.setInt(field, field.modifiers and Modifier.FINAL.inv())

    field.setInt(null, version)
}
