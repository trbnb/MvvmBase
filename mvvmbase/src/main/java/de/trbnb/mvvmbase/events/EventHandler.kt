package de.trbnb.mvvmbase.events

import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

typealias Listener<T> = (T) -> Unit

/**
 * Definition for all implementations that can propagate events to callbacks/listeners.
 *
 * @param T Type of event.
 */
interface EventHandler<T> {
    /**
     * Invokes all listeners with given [param].
     */
    operator fun invoke(param: T)

    /**
     * Registers a new listener.
     */
    fun addListener(listener: Listener<T>): Listener<T>

    /**
     * Removes a listener.
     */
    fun removeListener(listener: Listener<T>)

    /**
     * Registers a new listener.
     *
     * @see [addListener]
     */
    operator fun plusAssign(listener: Listener<T>) {
        addListener(listener)
    }

    /**
     * Removes a listener.
     *
     * @see [removeListener]
     */
    operator fun minusAssign(listener: Listener<T>) {
        removeListener(listener)
    }
}

/**
 * Adds a listener and will remove it when the [Lifecycle] of [lifecycleOwner] is [Lifecycle.State.DESTROYED].
 */
fun <T> EventHandler<T>.addListener(lifecycleOwner: LifecycleOwner, listener: Listener<T>) {
    addListener(listener)

    lifecycleOwner.lifecycle.addObserver(object : GenericLifecycleObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeListener(listener)
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        }
    })
}
