package de.trbnb.mvvmbase.utils

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.databinding.Observable
import androidx.lifecycle.LifecycleOwner
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.MvvmBase
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0

/**
 * Searches for a given parameterized class type in the receivers type hierachy and returns it if it was found.
 * Returns `null` otherwise.
 */
inline fun <reified T> Any.findGenericSuperclass(): ParameterizedType? {
    return javaClass.findGenericSuperclass(T::class.java)
}

/**
 * Searches for a given parameterized class type in the receivers hierachy and returns it if it was found.
 * Returns `null` otherwise.
 */
tailrec fun <T> Type.findGenericSuperclass(targetType: Class<T>): ParameterizedType? {
    val genericSuperClass = ((this as? Class<*>)?.genericSuperclass) ?: return null

    if ((genericSuperClass as? ParameterizedType)?.rawType == targetType) {
        return genericSuperClass
    }

    return genericSuperClass.findGenericSuperclass(targetType)
}

/**
 * Finds the field ID of the given property.
 *
 * @see MvvmBase.init
 */
fun KProperty<*>.resolveFieldId(): Int = MvvmBase.lookupFieldIdByName(brFieldName()) ?: BR._all

/**
 * Converts a property name to a field name like the data binding compiler.
 *
 * See also:
 * https://android.googlesource.com/platform/frameworks/data-binding/+/master/compiler/src/main/java/android/databinding/annotationprocessor/ProcessBindable.java#216
 */
@Suppress("MagicNumber")
internal fun KProperty<*>.brFieldName(): String {
    val isBoolean = returnType.classifier == Boolean::class && !returnType.isMarkedNullable
    if (name.startsWith("is") && Character.isJavaIdentifierStart(name[2]) && isBoolean) {
        return name[2].toLowerCase() + name.substring(3)
    }

    return name
}

/**
 * Invokes [action] everytime notifyPropertyChanged is called for the receiver property.
 */
internal inline fun <T> KProperty0<T>.observeBindable(
    lifecycleOwner: LifecycleOwner,
    invokeImmediately: Boolean = true,
    crossinline action: (T) -> Unit
): () -> Unit {
    val observableOwner = castSafely<CallableReference>()?.boundReceiver?.castSafely<Observable>()
        ?: throw IllegalArgumentException("Property receiver is not an Observable")

    val propertyId = resolveFieldId().takeUnless { it == BR._all } ?: throw IllegalArgumentException("Property isn't bindable")
    val onPropertyChangedCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, changedPropertyId: Int) {
            if (changedPropertyId == propertyId) {
                action(get())
            }
        }
    }
    observableOwner.addOnPropertyChangedCallback(lifecycleOwner, onPropertyChangedCallback)

    if (invokeImmediately) {
        action(get())
    }

    return { observableOwner.removeOnPropertyChangedCallback(onPropertyChangedCallback) }
}

@Composable
fun <T> KProperty0<T>.observeAsState(): State<T> {
    val state = remember { mutableStateOf(get()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = this, lifecycleOwner) {
        val dispose = observeBindable(lifecycleOwner, false) { state.value = it }
        onDispose { dispose() }
    }
    return state
}

internal inline fun <reified T> Any?.cast() = this as T
internal inline fun <reified T> Any?.castSafely() = this as? T