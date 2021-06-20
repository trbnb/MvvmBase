package de.trbnb.mvvmbase.events

interface EventChannelOwner {
    val eventChannel: EventChannel

    fun Event.send() = eventChannel(this)
}