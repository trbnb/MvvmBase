package de.trbnb.mvvmbase.test

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import de.trbnb.mvvmbase.BaseViewModel
import org.junit.jupiter.api.Test

class ViewModelLifecycleTests {
    class ViewModel : BaseViewModel()

    @Test
    fun `initial state`() {
        val observer = LifecycleObserver()
        val viewModel = ViewModel().apply {
            lifecycle.addObserver(observer)
        }
        assert(viewModel.lifecycle.currentState == Lifecycle.State.STARTED)

        assert(observer.events[0] == Lifecycle.Event.ON_CREATE)
        assert(observer.events[1] == Lifecycle.Event.ON_START)
    }

    @Test
    fun `after onDestroy() without binding`() {
        val observer = LifecycleObserver()
        val viewModel = ViewModel().apply {
            lifecycle.addObserver(observer)
        }
        viewModel.destroy()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.DESTROYED)

        assert(observer.events[0] == Lifecycle.Event.ON_CREATE)
        assert(observer.events[1] == Lifecycle.Event.ON_START)
        assert(observer.events[2] == Lifecycle.Event.ON_STOP)
        assert(observer.events[3] == Lifecycle.Event.ON_DESTROY)
    }

    class LifecycleObserver : LifecycleEventObserver {
        val events = mutableListOf<Lifecycle.Event>()
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            events += event
        }
    }
}
