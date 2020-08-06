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
    fun `after onBind()`() {
        val observer = LifecycleObserver()
        val viewModel = ViewModel().apply {
            lifecycle.addObserver(observer)
        }
        viewModel.onBind()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.RESUMED)

        assert(observer.events[0] == Lifecycle.Event.ON_CREATE)
        assert(observer.events[1] == Lifecycle.Event.ON_START)
        assert(observer.events[2] == Lifecycle.Event.ON_RESUME)
    }

    @Test
    fun `after onUnbind()`() {
        val observer = LifecycleObserver()
        val viewModel = ViewModel().apply {
            lifecycle.addObserver(observer)
        }
        viewModel.onBind()
        viewModel.onUnbind()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.STARTED)

        assert(observer.events[0] == Lifecycle.Event.ON_CREATE)
        assert(observer.events[1] == Lifecycle.Event.ON_START)
        assert(observer.events[2] == Lifecycle.Event.ON_RESUME)
        assert(observer.events[3] == Lifecycle.Event.ON_PAUSE)
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

    @Test
    fun `after onDestroy() with binding`() {
        val observer = LifecycleObserver()
        val viewModel = ViewModel().apply {
            lifecycle.addObserver(observer)
        }
        viewModel.onBind()
        viewModel.onUnbind()
        viewModel.destroy()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.DESTROYED)

        assert(observer.events[0] == Lifecycle.Event.ON_CREATE)
        assert(observer.events[1] == Lifecycle.Event.ON_START)
        assert(observer.events[2] == Lifecycle.Event.ON_RESUME)
        assert(observer.events[3] == Lifecycle.Event.ON_PAUSE)
        assert(observer.events[4] == Lifecycle.Event.ON_STOP)
        assert(observer.events[5] == Lifecycle.Event.ON_DESTROY)
    }

    @Test
    fun `onUnbind() called if ViewModel is bound and about to be destroyed`() {
        var onUnbindCalled = false
        val viewModel = object : BaseViewModel() {
            override fun onUnbind() {
                super.onUnbind()
                onUnbindCalled = true
            }
        }

        viewModel.onBind()
        viewModel.destroy()

        assert(onUnbindCalled)
    }

    class LifecycleObserver : LifecycleEventObserver {
        val events = mutableListOf<Lifecycle.Event>()
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            events += event
        }
    }
}
