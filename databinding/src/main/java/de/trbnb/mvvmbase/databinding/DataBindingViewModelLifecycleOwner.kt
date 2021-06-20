package de.trbnb.mvvmbase.databinding

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

/**
 * The custom lifecycle owner for ViewModels.
 *
 * Its lifecycle state is:
 * - After initialization/being unbound: [Lifecycle.State.STARTED].
 * - After being bound: [Lifecycle.State.RESUMED].
 * - After being destroyed: [Lifecycle.State.DESTROYED].
 */
internal class DataBindingViewModelLifecycleOwner(enforceMainThread: Boolean) : LifecycleOwner {
    @SuppressLint("VisibleForTests")
    private val registry = when (enforceMainThread) {
        true -> LifecycleRegistry(this)
        false -> LifecycleRegistry.createUnsafe(this)
    }

    init {
        onEvent(Event.INITIALIZED)
    }

    fun onEvent(event: Event) {
        registry.currentState = when (event) {
            Event.INITIALIZED -> Lifecycle.State.STARTED
            Event.BOUND -> Lifecycle.State.RESUMED
            Event.UNBOUND -> Lifecycle.State.STARTED
            Event.DESTROYED -> Lifecycle.State.DESTROYED
        }
    }

    override fun getLifecycle() = registry

    internal fun getInternalState() = when (registry.currentState) {
        Lifecycle.State.DESTROYED -> State.DESTROYED
        Lifecycle.State.RESUMED -> State.BOUND
        else -> State.INITIALIZED
    }

    /**
     * Enum for the specific Lifecycle of ViewModels.
     */
    internal enum class State {
        INITIALIZED,
        BOUND,
        DESTROYED
    }

    /**
     * Enum for the specific Lifecycle of ViewModels.
     */
    internal enum class Event {
        INITIALIZED,
        BOUND,
        UNBOUND,
        DESTROYED
    }
}
