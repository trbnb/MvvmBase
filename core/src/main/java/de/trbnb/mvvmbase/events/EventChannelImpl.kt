package de.trbnb.mvvmbase.events

/**
 * [EventChannel] implementation based on [EventHandler].
 *
 * @param memorizeNotReceivedEvents Defines if events that can't be received by listeners because none are registered are sent later
 * when a listener is registered.
 */
internal class EventChannelImpl(memorizeNotReceivedEvents: Boolean = true) : EventChannel {
    private val eventHandler = DefaultEventHandler<Event>(memorizeNotReceivedEvents)

    override operator fun invoke(event: Event) {
        eventHandler(event)
    }

    override fun addListener(eventListener: EventListener): EventListener {
        eventHandler += eventListener
        return eventListener
    }

    override fun removeListener(eventListener: EventListener) {
        eventHandler -= eventListener
    }
}
