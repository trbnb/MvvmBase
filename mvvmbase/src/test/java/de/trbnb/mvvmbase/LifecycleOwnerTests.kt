package de.trbnb.mvvmbase

import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import org.junit.Test

class ViewModelLifecycleTests {
    /**
     * Test [BaseViewModel] class that can be destroyed at will.
     */
    class TestViewModel: BaseViewModel() {
        fun destroy() {
            onCleared()
        }
    }

    /**
     * Test if [GenericLifecycleObserver]s will be called with the correct event.
     */
    @Test
    fun genericLifecycleObserver() {
        var lastEvent: Lifecycle.Event? = null
        val viewModel = TestViewModel().apply {
            lifecycle.addObserver(LifecycleEventObserver { _, event ->
                lastEvent = event
            })
        }
        viewModel.destroy()

        assert(lastEvent === Lifecycle.Event.ON_DESTROY)
    }

    /**
     * Test if other [LifecycleObserver] implementation are called properly.
     */
    @Test
    fun otherLifecycleObserver() {
        var isDestroyed = false
        var onAnyCalled = false
        var onCreateCalled = false
        var otherMethodCalled = false

        val viewModel = TestViewModel().apply {
            lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroyed() {
                    isDestroyed = true
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
                fun onAny() {
                    onAnyCalled = true
                }

                @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                fun onCreate() {
                    onCreateCalled = true
                }

                fun otherMethod() {
                    otherMethodCalled = true
                }
            })
        }
        viewModel.destroy()

        assert(isDestroyed)
        assert(onAnyCalled)
        assert(!onCreateCalled)
        assert(!otherMethodCalled)
    }
}
