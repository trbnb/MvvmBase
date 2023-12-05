package de.trbnb.mvvmbase.events

/**
 * [EventChannel] implementation.
 *
 * @param memorizeNotReceivedEvents Defines if events that can't be received by listeners because none are registered are sent later
 * when a listener is registered.
 */
public class EventChannelImpl(memorizeNotReceivedEvents: Boolean = true) : EventChannel {
    private val listeners = mutableListOf<EventListener>()
    private val notReceivedEvents = if (memorizeNotReceivedEvents) mutableListOf<Event>() else null

    override operator fun invoke(event: Event) {
        if (listeners.isEmpty()) {
            notReceivedEvents?.add(event)
        } else {
            listeners.forEach { it.invoke(event) }
        }
    }

    override fun addListener(eventListener: EventListener): EventListener {
        listeners += eventListener
        notReceivedEvents?.apply {
            forEach(eventListener)
            clear()
        }
        return eventListener
    }

    override fun removeListener(eventListener: EventListener) {
        listeners.removeAll { it == eventListener }
    }
}
