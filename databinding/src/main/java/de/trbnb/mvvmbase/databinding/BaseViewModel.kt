package de.trbnb.mvvmbase.databinding

import androidx.annotation.CallSuper
import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.destroyInternal
import androidx.lifecycle.getTagFromViewModel
import androidx.lifecycle.setTagIfAbsentForViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.databinding.utils.resolveFieldId
import de.trbnb.mvvmbase.events.EventChannel
import de.trbnb.mvvmbase.events.EventChannelImpl
import kotlin.reflect.KProperty
import androidx.lifecycle.ViewModel as ArchitectureViewModel

/**
 * Simple base implementation of the [ViewModel] interface based on [BaseObservable].
 */
abstract class BaseViewModel : ArchitectureViewModel(), ViewModel, LifecycleOwner {
    /**
     * Callback registry for [Observable].
     */
    @Transient
    private var callbacks = PropertyChangeRegistry()

    private val lifecycleOwner = DataBindingViewModelLifecycleOwner(MvvmBase.enforceViewModelLifecycleMainThread)

    /**
     * [EventChannel] implementation that can be used to send non-state information to a view component.
     */
    override val eventChannel: EventChannel by lazy { EventChannelImpl(memorizeNotReceivedEvents) }

    /**
     * Gets if events that are raised when no listeners are registered are raised later when a listener is registered.
     */
    protected open val memorizeNotReceivedEvents: Boolean
        get() = true

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
    }

    final override fun notifyPropertyChanged(property: KProperty<*>) {
        notifyPropertyChanged(property.resolveFieldId())
    }

    /**
     * Is called when the view model is bound to an activity/layout.
     */
    @CallSuper
    override fun onBind() {
        lifecycleOwner.onEvent(DataBindingViewModelLifecycleOwner.Event.BOUND)
    }

    /**
     * Is called when the view model is no longer bound to an activity/layout.
     */
    @CallSuper
    override fun onUnbind() {
        lifecycleOwner.onEvent(DataBindingViewModelLifecycleOwner.Event.UNBOUND)
    }

    final override fun destroy() {
        destroyInternal()
    }

    final override fun onCleared() {
        onDestroy()
    }

    final override operator fun <T : Any> get(key: String): T? = getTagFromViewModel(key)

    final override fun <T : Any> initTag(key: String, newValue: T): T = setTagIfAbsentForViewModel(key, newValue)

    /**
     * Is called when this instance is about to be destroyed.
     * Any references that could cause memory leaks should be cleared here.
     */
    @CallSuper
    override fun onDestroy() {
        if (lifecycleOwner.getInternalState() == DataBindingViewModelLifecycleOwner.State.BOUND) {
            onUnbind()
        }

        super.onCleared()
        lifecycleOwner.onEvent(DataBindingViewModelLifecycleOwner.Event.DESTROYED)
    }

    override fun getLifecycle(): Lifecycle = lifecycleOwner.lifecycle
}
