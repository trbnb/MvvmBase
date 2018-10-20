package de.trbnb.mvvmbase

import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.Observable
import android.databinding.PropertyChangeRegistry
import android.arch.lifecycle.ViewModel as ArchitectureViewModel

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
     * Gets the custom lifecycle for ViewModels.
     *
     * Is either [Lifecycle.State.INITIALIZED] or [Lifecycle.State.DESTROYED].
     */
    private val lifecycle = object : Lifecycle() {
        private val observers = mutableListOf<LifecycleObserver>()

        /**
         * Gets if the ViewModel is destroyed.
         */
        private var isDestroyed = false

        override fun addObserver(observer: LifecycleObserver) {
            observers += observer
        }

        override fun removeObserver(observer: LifecycleObserver) {
            observers -= observer
        }

        override fun getCurrentState(): State = if (isDestroyed) State.INITIALIZED else State.DESTROYED

        /**
         * Registers that the ViewModel will be destroyed.
         * This will trigger the [LifecycleObserver]s.
         */
        fun setDestroyed() {
            isDestroyed = true
            val event = Event.ON_DESTROY

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
    }

    /**
     * Adds a property changed callback to [callbacks].
     */
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    /**
     * Removes a property changed callback to [callbacks].
     */
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    /**
     * Is called when the view model is bound to an activity/layout.
     */
    override fun onBind() { }

    /**
     * Is called when the view model is no longer bound to an activity/layout.
     */
    override fun onUnbind() { }

    /**
     * Is called when this instance is about to be destroyed.
     * Any references that could cause memory leaks should be cleared here.
     */
    override fun onDestroy() { }

    final override fun onCleared() {
        super.onCleared()

        onDestroy()
        lifecycle.setDestroyed()
    }

    override fun getLifecycle() = lifecycle

}
