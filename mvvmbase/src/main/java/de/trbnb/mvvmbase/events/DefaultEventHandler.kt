package de.trbnb.mvvmbase.events

/**
 * Default implementation of [EventHandler].
 *
 * @param memorizeNotReceivedEvents Defines if events that can't be received by listeners because none are registered are sent later
 * when a listener is registered.
 */
class DefaultEventHandler<T>(memorizeNotReceivedEvents: Boolean = false) : EventHandler<T> {
    private val listeners = mutableListOf<Listener<T>>()
    private val notReceivedEvents = if (memorizeNotReceivedEvents) mutableListOf<T>() else null

    override operator fun invoke(param: T) {
        if (listeners.isEmpty()) {
            notReceivedEvents?.add(param)
        } else {
            listeners.forEach { it.invoke(param) }
        }
    }

    override fun addListener(listener: Listener<T>): Listener<T> {
        listeners += listener
        notReceivedEvents?.apply {
            forEach(listener)
            clear()
        }
        return listener
    }

    override fun removeListener(listener: Listener<T>) {
        listeners.removeAll { it == listener }
    }
}
