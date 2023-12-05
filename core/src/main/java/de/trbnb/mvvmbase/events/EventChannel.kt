package de.trbnb.mvvmbase.events

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

public typealias EventListener = (event: Event) -> Unit

/**
 * Base interface that defines interaction for not-state information between [de.trbnb.mvvmbase.ViewModel]
 * and MVVM view components.
 */
public interface EventChannel {
    /**
     * Invokes all listeners with given [event].
     */
    public operator fun invoke(event: Event)

    /**
     * Registers a new listener.
     */
    public fun addListener(eventListener: EventListener): EventListener

    /**
     * Removes a listener.
     */
    public fun removeListener(eventListener: EventListener)

    /**
     * Registers a new listener.
     *
     * @see [addListener]
     */
    public operator fun plusAssign(eventListener: EventListener) {
        addListener(eventListener)
    }

    /**
     * Removes a listener.
     *
     * @see [removeListener]
     */
    public operator fun minusAssign(eventListener: EventListener): Unit = removeListener(eventListener)
}

/**
 * Adds a listener and will remove it when the [Lifecycle] of [lifecycleOwner] is [Lifecycle.State.DESTROYED].
 */
public fun EventChannel.addListener(lifecycleOwner: LifecycleOwner, eventListener: EventListener) {
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
