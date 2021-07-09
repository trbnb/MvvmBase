package de.trbnb.mvvmbase.events

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

typealias EventListener = (event: Event) -> Unit

/**
 * Base interface that defines interaction for not-state information between [de.trbnb.mvvmbase.ViewModel]
 * and MVVM view components.
 */
interface EventChannel {
    /**
     * Invokes all listeners with given [event].
     */
    operator fun invoke(event: Event)

    /**
     * Registers a new listener.
     */
    fun addListener(eventListener: EventListener): EventListener

    /**
     * Removes a listener.
     */
    fun removeListener(eventListener: EventListener)

    /**
     * Registers a new listener.
     *
     * @see [addListener]
     */
    operator fun plusAssign(eventListener: EventListener) {
        addListener(eventListener)
    }

    /**
     * Removes a listener.
     *
     * @see [removeListener]
     */
    operator fun minusAssign(eventListener: EventListener) = removeListener(eventListener)
}

/**
 * Adds a listener and will remove it when the [Lifecycle] of [lifecycleOwner] is [Lifecycle.State.DESTROYED].
 */
fun EventChannel.addListener(lifecycleOwner: LifecycleOwner, eventListener: EventListener) {
    addListener(eventListener)

    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeListener(eventListener)
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        }
    })
}
