package de.trbnb.mvvmbase.test

import androidx.lifecycle.Lifecycle
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.events.Event
import org.junit.jupiter.api.Test

class NestedViewModelTests {
    @Test
    fun `autoDestroy() destroys ViewModels in a parent ViewModel`() {
        val childViewModel = SimpleViewModel()
        val parentViewModel = object : BaseViewModel() {
            val items = listOf(childViewModel).autoDestroy()
        }

        parentViewModel.onDestroy()
        assert(childViewModel.lifecycle.currentState == Lifecycle.State.DESTROYED)
    }

    @Test
    fun `bindEvents() redirects events from child ViewModels to eventChannel of parent ViewModel`() {
        val event = object : Event {}
        val childViewModel = SimpleViewModel()
        val parentViewModel = object : BaseViewModel() {
            val items = listOf(childViewModel).bindEvents()
        }

        var eventWasReceived = false

        parentViewModel.eventChannel.addListener { newEvent ->
            if (event == newEvent) {
                eventWasReceived = true
            }
        }

        childViewModel.eventChannel(event)
        assert(eventWasReceived)
    }
}

class SimpleViewModel : BaseViewModel()
