package de.trbnb.mvvmbase.events

/**
 * [EventChannel] implementation based on [EventHandler].
 */
class EventChannelImpl : EventChannel {
    private val eventHandler = EventHandler<Event>()

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
