package de.trbnb.mvvmbase.test

import androidx.lifecycle.Lifecycle
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.events.Event
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.Closeable

class NestedViewModelTests {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            MvvmBase.disableViewModelLifecycleThreadConstraints()
        }
    }

    @Test
    fun `autoDestroy() destroys ViewModels in a parent ViewModel`() {
        val childViewModel = SimpleViewModel()
        val parentViewModel = object : BaseViewModel() {
            val items = listOf(childViewModel).autoDestroy()
        }

        parentViewModel.destroy()
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

    @Test
    fun `destroy() closes all tags`() {
        var tagIsClosed = false
        val viewModel = object : BaseViewModel() {
            init {
                initTag("foo", object : Closeable {
                    override fun close() {
                        tagIsClosed = true
                    }
                })
            }
        }

        viewModel.destroy()
        assert(tagIsClosed)
    }
}

class SimpleViewModel : BaseViewModel()
