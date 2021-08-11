package de.trbnb.mvvmbase.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import kotlin.reflect.KMutableProperty0

/**
 * Simple implementation of a mutable state derived from an observable property.
 */
class PropertyMutableState<T>(
    private val state: State<T>,
    private val property: KMutableProperty0<T>
) : MutableState<T> {
    override var value: T
        get() = state.value
        set(value) = property.set(value)

    override fun component1(): T = value
    override fun component2(): (T) -> Unit = { value = it }
}
