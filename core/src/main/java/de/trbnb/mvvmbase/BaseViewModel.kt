package de.trbnb.mvvmbase

import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.destroyInternal
import androidx.lifecycle.getTagFromViewModel
import androidx.lifecycle.setTagIfAbsentForViewModel
import de.trbnb.mvvmbase.events.EventChannel
import de.trbnb.mvvmbase.events.EventChannelImpl
import de.trbnb.mvvmbase.observable.PropertyChangeRegistry
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import androidx.lifecycle.ViewModel as ArchitectureViewModel

/**
 * Simple base implementation of the [ViewModel].
 */
abstract class BaseViewModel : ArchitectureViewModel(), ViewModel, LifecycleOwner {
    /**
     * Callback registry for [de.trbnb.mvvmbase.observable.ObservableContainer].
     */
    private val callbacks: PropertyChangeRegistry

    /**
     * [EventChannel] implementation that can be used to send non-state information to a view component.
     */
    override val eventChannel: EventChannel by lazy { EventChannelImpl(memorizeNotReceivedEvents) }

    /**
     * Gets if events that are raised when no listeners are registered are raised later when a listener is registered.
     */
    protected open val memorizeNotReceivedEvents: Boolean
        get() = true

    /**
     * @see ViewModelLifecycleOwner
     */
    private val lifecycleOwner = ViewModelLifecycleOwner(MvvmBase.enforceViewModelLifecycleMainThread)

    init {
        val pairs = javaClass.kotlin.memberProperties.mapNotNull { property ->
            when (val annotation = property.findAnnotation<DependsOn>()) {
                null -> null
                else -> property.name to annotation.value
            }
        }

        callbacks = PropertyChangeRegistry(pairs)
    }

    final override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    final override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    final override fun notifyPropertyChanged(propertyName: String) {
        callbacks.notifyChange(this, propertyName)
    }

    final override fun notifyPropertyChanged(property: KProperty<*>) {
        notifyPropertyChanged(property.name)
    }

    final override fun destroy() {
        destroyInternal()
    }

    /**
     * Is called when this instance is about to be destroyed.
     * Any references that could cause memory leaks should be cleared here.
     */
    @CallSuper
    protected open fun onDestroy() {
        super.onCleared()
        lifecycleOwner.onEvent(ViewModelLifecycleOwner.Event.DESTROYED)
    }

    final override fun onCleared() {
        onDestroy()
    }

    final override operator fun <T : Any> get(key: String): T? = getTagFromViewModel(key)

    final override fun <T : Any> initTag(key: String, newValue: T): T = setTagIfAbsentForViewModel(key, newValue)

    override fun getLifecycle(): Lifecycle = lifecycleOwner.lifecycle
}
