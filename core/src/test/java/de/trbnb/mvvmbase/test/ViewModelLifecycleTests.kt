package de.trbnb.mvvmbase.test

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ViewModelLifecycleTests {
    class ViewModel : BaseViewModel()

    @Test
    fun `initial state`() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
        val observer = LifecycleObserver()
        val viewModel = ViewModel().apply {
            lifecycle.addObserver(observer)
        }
        assert(viewModel.lifecycle.currentState == Lifecycle.State.RESUMED)
    }

    @Test
    fun `after onDestroy() without binding`() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
        val observer = LifecycleObserver()
        val viewModel = ViewModel().apply {
            lifecycle.addObserver(observer)
        }
        viewModel.destroy()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.DESTROYED)

        assertEquals(Lifecycle.Event.ON_CREATE, observer.events[0])
        assertEquals(Lifecycle.Event.ON_START, observer.events[1])
        assertEquals(Lifecycle.Event.ON_RESUME, observer.events[2])
        assertEquals(Lifecycle.Event.ON_PAUSE, observer.events[3])
        assertEquals(Lifecycle.Event.ON_STOP, observer.events[4])
        assertEquals(Lifecycle.Event.ON_DESTROY, observer.events[5])
    }

    class LifecycleObserver : LifecycleEventObserver {
        val events = mutableListOf<Lifecycle.Event>()
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            events += event
        }
    }
}
