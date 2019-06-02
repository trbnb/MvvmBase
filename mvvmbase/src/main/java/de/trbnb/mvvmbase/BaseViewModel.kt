package de.trbnb.mvvmbase

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.GenericLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import de.trbnb.mvvmbase.annotations.DependsOn
import de.trbnb.mvvmbase.events.EventChannel
import de.trbnb.mvvmbase.events.EventChannelImpl
import de.trbnb.mvvmbase.utils.resolveFieldId
import kotlin.reflect.KProperty
import kotlin.reflect.full.memberProperties
import androidx.lifecycle.ViewModel as ArchitectureViewModel

/**
 * Simple base implementation of the [ViewModel] interface based on [BaseObservable].
 */
abstract class BaseViewModel : ArchitectureViewModel(), ViewModel {

    /**
     * Callback registry for [Observable].
     */
    @Transient
    private var callbacks = PropertyChangeRegistry()

    /**
     * [EventChannel] implementation that can be used to send non-state information to a view component.
     */
    override val eventChannel: EventChannel by lazy { EventChannelImpl(memorizeNotReceivedEvents) }

    /**
     * Gets if events that are raised when no listeners are registered are raised later when a listener is registered.
     */
    protected open val memorizeNotReceivedEvents: Boolean
        get() = true

    private val dependentFieldIds: Map<Int, IntArray>

    init {
        dependentFieldIds = this::class.memberProperties.asSequence()
            .filter { it.annotations.any { annotation -> annotation is DependsOn } }
            .map {
                it.resolveFieldId().takeUnless { id -> id == BR._all } to
                it.annotations.filterIsInstance<DependsOn>().firstOrNull()?.value
            }
            .filter { it.first != null && it.second != null }
            .filterIsInstance<Pair<Int, IntArray>>()
            .toMap()
    }

    /**
     * Gets the custom lifecycle for ViewModels.
     *
     * Its state is:
     * - After initialization & being unbound: [Lifecycle.State.STARTED].
     * - After being bound: [Lifecycle.State.RESUMED].
     * - After being unbound: [Lifecycle.State.STARTED].
     * - After being destroyed: [Lifecycle.State.DESTROYED].
     */
    private val lifecycle = object : Lifecycle() {
        private val observers = mutableListOf<LifecycleObserver>()

        @set:SuppressLint("RestrictedApi")
        var state = ViewModelLifecycleState.INITIALIZED
            set(value) {
                field = value
                val event = when (value) {
                    ViewModelLifecycleState.INITIALIZED -> Event.ON_START
                    ViewModelLifecycleState.BOUND -> Event.ON_RESUME
                    ViewModelLifecycleState.UNBOUND -> Event.ON_PAUSE
                    ViewModelLifecycleState.DESTROYED -> Event.ON_DESTROY
                }

                // Copy the observers as some might unregister themselves and could cause a java.util.ConcurrentModificationException
                val observers = List(observers.size) { observers[it] }
                observers.forEach { observer ->
                    when (observer) {
                        is GenericLifecycleObserver -> observer.onStateChanged(this@BaseViewModel, event)
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

        override fun getCurrentState(): State = when (state) {
            ViewModelLifecycleState.INITIALIZED -> State.STARTED
            ViewModelLifecycleState.BOUND -> State.RESUMED
            ViewModelLifecycleState.UNBOUND -> State.STARTED
            ViewModelLifecycleState.DESTROYED -> State.DESTROYED
        }
    }

    final override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    final override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    final override fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    final override fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
        dependentFieldIds[fieldId]?.forEach { notifyPropertyChanged(it) }
    }

    final override fun notifyPropertyChanged(property: KProperty<*>) {
        notifyPropertyChanged(property.resolveFieldId())
    }

    /**
     * Is called when the view model is bound to an activity/layout.
     */
    @CallSuper
    override fun onBind() {
        lifecycle.state = ViewModelLifecycleState.BOUND
    }

    /**
     * Is called when the view model is no longer bound to an activity/layout.
     */
    @CallSuper
    override fun onUnbind() {
        lifecycle.state = ViewModelLifecycleState.UNBOUND
    }

    /**
     * Is called when this instance is about to be destroyed.
     * Any references that could cause memory leaks should be cleared here.
     */
    @CallSuper
    override fun onDestroy() {
        super.onCleared()
        lifecycle.state = ViewModelLifecycleState.DESTROYED
    }

    final override fun onCleared() {
        onDestroy()
    }

    override fun getLifecycle() = lifecycle

    /**
     * Enum for the specific Lifecycle of ViewModels.
     */
    private enum class ViewModelLifecycleState {
        INITIALIZED,
        BOUND,
        UNBOUND,
        DESTROYED
    }

}
