package de.trbnb.mvvmbase.events

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import java.lang.ref.WeakReference

typealias Listener<T> = (T) -> Unit

/**
 * Simple callback holder.
 */
class EventHandler<T> {
    private val listeners = mutableListOf<WeakReference<Listener<T>>>()

    /**
     * Invokes all listeners with given [param].
     */
    operator fun invoke(param: T) {
        cleanListenerReferences()
        listeners.forEach { it.get()?.invoke(param) }
    }

    /**
     * Registers a new listener.
     */
    fun addListener(listener: Listener<T>): Listener<T> {
        listeners += WeakReference(listener)
        return listener
    }

    /**
     * Removes a listener.
     */
    fun removeListener(listener: Listener<T>) {
        listeners.removeAll { it.get() == listener }
    }

    private fun cleanListenerReferences() {
        listeners.removeAll { it.get() == null }
    }

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
