package de.trbnb.mvvmbase.utils

import androidx.lifecycle.LifecycleOwner
import de.trbnb.mvvmbase.OnPropertyChangedCallback
import de.trbnb.mvvmbase.observable.ObservableContainer
import de.trbnb.mvvmbase.observable.addOnPropertyChangedCallback
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KProperty0

/**
 * Invokes [action] everytime notifyPropertyChanged is called for the receiver property.
 */
internal inline fun <T> KProperty0<T>.observe(
    lifecycleOwner: LifecycleOwner? = null,
    invokeImmediately: Boolean = false,
    crossinline action: (T) -> Unit
): () -> Unit {
    val observableContainer = (this as? CallableReference)?.boundReceiver?.let { it as? ObservableContainer }
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
