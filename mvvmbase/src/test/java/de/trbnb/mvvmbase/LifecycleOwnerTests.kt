package de.trbnb.mvvmbase

import androidx.lifecycle.Lifecycle
import org.junit.Test

class ViewModelLifecycleTests {
    class ViewModel : BaseViewModel()

    @Test
    fun `initial state`() {
        val viewModel = ViewModel()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.STARTED)
    }

    @Test
    fun `after onBind()`() {
        val viewModel = ViewModel()
        viewModel.onBind()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.RESUMED)
    }

    @Test
    fun `after onUnbind()`() {
        val viewModel = ViewModel()
        viewModel.onBind()
        viewModel.onUnbind()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.STARTED)
    }

    @Test
    fun `after onDestroy() without binding`() {
        val viewModel = ViewModel()
        viewModel.onDestroy()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.DESTROYED)
    }

    @Test
    fun `after onDestroy() with binding`() {
        val viewModel = ViewModel()
        viewModel.onBind()
        viewModel.onUnbind()
        viewModel.onDestroy()
        assert(viewModel.lifecycle.currentState == Lifecycle.State.DESTROYED)
    }
}
