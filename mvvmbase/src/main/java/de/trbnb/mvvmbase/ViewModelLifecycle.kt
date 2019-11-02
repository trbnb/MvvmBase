package de.trbnb.mvvmbase

import android.annotation.SuppressLint
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
 * The custom lifecycle for ViewModels.
 *
 * Its state is:
 * - After initialization & being unbound: [Lifecycle.State.STARTED].
 * - After being bound: [Lifecycle.State.RESUMED].
 * - After being unbound: [Lifecycle.State.STARTED].
 * - After being destroyed: [Lifecycle.State.DESTROYED].
 */
internal class ViewModelLifecycle(private val viewModel: ViewModel) : Lifecycle() {
    private val observers = mutableListOf<LifecycleObserver>()

    @set:SuppressLint("RestrictedApi")
    var state = State.INITIALIZED
        set(value) {
            field = value
            val event = when (value) {
                State.INITIALIZED -> Event.ON_START
                State.BOUND -> Event.ON_RESUME
                State.UNBOUND -> Event.ON_PAUSE
                State.DESTROYED -> Event.ON_DESTROY
            }

            // Copy the observers as some might unregister themselves and could cause a java.util.ConcurrentModificationException
            val observers = List(observers.size) { observers[it] }
            observers.forEach { observer ->
                when (observer) {
                    is GenericLifecycleObserver -> observer.onStateChanged(viewModel, event)
                    else -> {
                        // LifecycleObservers that are not a GenericLifecycleObserver will be triggered
                        // via reflection. See OnLifecycleEvent annotation.
                        observer.javaClass.declaredMethods.filter {
                            it.annotations.any { annotation ->
                                val annotationValue = (annotation as? OnLifecycleEvent)?.value ?: return@any false
                                annotationValue == event || annotationValue == Event.ON_ANY
                            }
                        }.forEach { it.invoke(observer) }
                    }
                }
            }
        }

    override fun addObserver(observer: LifecycleObserver) {
        observers += observer
    }

    override fun removeObserver(observer: LifecycleObserver) {
        observers -= observer
    }

    override fun getCurrentState(): Lifecycle.State = when (state) {
        State.INITIALIZED -> Lifecycle.State.STARTED
        State.BOUND -> Lifecycle.State.RESUMED
        State.UNBOUND -> Lifecycle.State.STARTED
        State.DESTROYED -> Lifecycle.State.DESTROYED
    }

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
