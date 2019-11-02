package de.trbnb.mvvmbase

import androidx.annotation.CallSuper
import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.Lifecycle
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
     * @see ViewModelLifecycle
     */
    @Suppress("LeakingThis")
    private val lifecycle = ViewModelLifecycle(this)

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
        dependentFieldIds.forEach { (property, dependentIds) ->
            if (fieldId in dependentIds) {
                notifyPropertyChanged(property)
            }
        }
    }

    final override fun notifyPropertyChanged(property: KProperty<*>) {
        notifyPropertyChanged(property.resolveFieldId())
    }

    /**
     * Is called when the view model is bound to an activity/layout.
     */
    @CallSuper
    override fun onBind() {
        lifecycle.state = ViewModelLifecycle.State.BOUND
    }

    /**
     * Is called when the view model is no longer bound to an activity/layout.
     */
    @CallSuper
    override fun onUnbind() {
        lifecycle.state = ViewModelLifecycle.State.UNBOUND
    }

    /**
     * Is called when this instance is about to be destroyed.
     * Any references that could cause memory leaks should be cleared here.
     */
    @CallSuper
    override fun onDestroy() {
        super.onCleared()
        lifecycle.state = ViewModelLifecycle.State.DESTROYED
    }

    final override fun onCleared() {
        onDestroy()
    }

    override fun getLifecycle(): Lifecycle = lifecycle
}
