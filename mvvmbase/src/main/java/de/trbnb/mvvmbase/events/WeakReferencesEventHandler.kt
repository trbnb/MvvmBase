package de.trbnb.mvvmbase.events

import java.lang.ref.WeakReference

/**
 * Implementation of [EventHandler] that stores listeners as [WeakReference].
 *
 * @param memorizeNotReceivedEvents Defines if events that can't be received by listeners because none are registered are sent later
 * when a listener is registered.
 */
class WeakReferencesEventHandler<T>(memorizeNotReceivedEvents: Boolean = false) : EventHandler<T> {
    private val listeners = mutableListOf<WeakReference<Listener<T>>>()
    private val notReceivedEvents = if (memorizeNotReceivedEvents) mutableListOf<T>() else null

    override operator fun invoke(param: T) {
        cleanListenerReferences()
        if (listeners.isEmpty()) {
            notReceivedEvents?.add(param)
        } else {
            listeners.forEach { it.get()?.invoke(param) }
        }
    }

    override fun addListener(listener: Listener<T>): Listener<T> {
        listeners += WeakReference(listener)
        notReceivedEvents?.apply {
            forEach(listener)
            clear()
        }
        return listener
    }

    override fun removeListener(listener: Listener<T>) {
        listeners.removeAll { it.get() == listener }
    }

    private fun cleanListenerReferences() {
        listeners.removeAll { it.get() == null }
    }
}
