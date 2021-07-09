package de.trbnb.mvvmbase.compose

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import kotlin.reflect.KMutableProperty0

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