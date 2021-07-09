package de.trbnb.mvvmbase.test.events

import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.events.EventChannel
import de.trbnb.mvvmbase.events.EventChannelImpl
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class EventChannelImplTests {
    @Test
    fun `events are forwarded`() {
        val eventChannel: EventChannel = EventChannelImpl()
        var eventWasReceived = false
        eventChannel.addListener {
            eventWasReceived = true
        }
        eventChannel(TestEvent())
        Assertions.assertEquals(eventWasReceived, true)
    }

    @Test
    fun `memorized events are forwarded`() {
        val eventChannel: EventChannel = EventChannelImpl(memorizeNotReceivedEvents = true)
        var eventWasReceived = false
        eventChannel(TestEvent())

        eventChannel.addListener {
            eventWasReceived = true
        }

        Assertions.assertEquals(eventWasReceived, true)
    }

    @Test
    fun `event aren't forwarding if memorizing is deactivated`() {
        val eventChannel: EventChannel = EventChannelImpl(memorizeNotReceivedEvents = false)
        var eventWasReceived = false
        eventChannel(TestEvent())

        eventChannel.addListener {
            eventWasReceived = true
        }

        Assertions.assertEquals(eventWasReceived, false)
    }

    class TestEvent : Event
}