package de.trbnb.mvvmbase.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.LifecycleOwner
import de.trbnb.mvvmbase.OnPropertyChangedCallback
import de.trbnb.mvvmbase.observable.ObservableContainer
import de.trbnb.mvvmbase.observable.addOnPropertyChangedCallback
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KMutableProperty0
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
 * Invokes [action] everytime notifyPropertyChanged is called for the receiver property.
 */
internal inline fun <T> KProperty0<T>.observe(
    lifecycleOwner: LifecycleOwner? = null,
    invokeImmediately: Boolean = false,
    crossinline action: (T) -> Unit
): () -> Unit {
    val observableContainer = castSafely<CallableReference>()?.boundReceiver?.castSafely<ObservableContainer>()
        ?: throw IllegalArgumentException("Property receiver is not an Observable")

    val onPropertyChangedCallback = OnPropertyChangedCallback { _, propertyName ->
        if (propertyName == name) {
            action(get())
        }
    }

    if (lifecycleOwner != null) {
        observableContainer.addOnPropertyChangedCallback(lifecycleOwner, onPropertyChangedCallback)
    } else {
        observableContainer.addOnPropertyChangedCallback(onPropertyChangedCallback)
    }

    if (invokeImmediately) {
        action(get())
    }

    return { observableContainer.removeOnPropertyChangedCallback(onPropertyChangedCallback) }
}

@Composable
fun <T> KProperty0<T>.observeAsState(): State<T> {
    val state = remember { mutableStateOf(get()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = this, lifecycleOwner) {
        val dispose = observe(lifecycleOwner, false) { state.value = it }
        onDispose(dispose::invoke)
    }
    return state
}

@Composable
fun <T> KMutableProperty0<T>.observeAsMutableState(): MutableState<T> {
    return ViewModelMutableState(observeAsState(), this)
}

class ViewModelMutableState<T>(
    private val state: State<T>,
    private val property: KMutableProperty0<T>
) : MutableState<T>, State<T> by state {
    private val setter: (T) -> Unit = { value: T -> property.setter.call(value) }

    override var value: T
        get() = state.value
        set(value) {
            setter(value)
        }

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = setter
}

inline fun <reified T> Any?.cast() = this as T
inline fun <reified T> Any?.castSafely() = this as? T

inline val <T> MutableState<T>.setter: (T) -> Unit get() = component2()