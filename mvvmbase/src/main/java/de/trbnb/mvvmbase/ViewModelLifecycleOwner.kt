package de.trbnb.mvvmbase

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * The custom lifecycle owner for ViewModels.
 *
 * Its lifecycle state is:
 * - After initialization & being unbound: [Lifecycle.State.STARTED].
 * - After being bound: [Lifecycle.State.RESUMED].
 * - After being unbound: [Lifecycle.State.STARTED].
 * - After being destroyed: [Lifecycle.State.DESTROYED].
 */
internal class ViewModelLifecycleOwner : LifecycleOwner {
    private val registry = LifecycleRegistry(this)

    init {
        setState(State.INITIALIZED)
    }

    fun setState(state: State) {
        registry.currentState = when (state) {
            State.INITIALIZED -> Lifecycle.State.STARTED
            State.BOUND -> Lifecycle.State.RESUMED
            State.UNBOUND -> Lifecycle.State.STARTED
            State.DESTROYED -> Lifecycle.State.DESTROYED
        }
    }

    override fun getLifecycle() = registry

    /**
     * Enum for the specific Lifecycle of ViewModels.
     */
    internal enum class State {
        INITIALIZED,
        BOUND,
        UNBOUND,
        DESTROYED
    }
}
