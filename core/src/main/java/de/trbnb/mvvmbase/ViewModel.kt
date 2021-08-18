package de.trbnb.mvvmbase

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import de.trbnb.mvvmbase.events.EventChannelOwner
import de.trbnb.mvvmbase.events.addListener
import de.trbnb.mvvmbase.observable.ObservableContainer
import de.trbnb.mvvmbase.observableproperty.BeforeSet
import de.trbnb.mvvmbase.observableproperty.ObservableProperty
import de.trbnb.mvvmbase.utils.destroyAll

/**
 * Base interface that defines basic functionality for all view models.
 *
 * It extends the [ObservableContainer] interface provided by the Android data binding library. This means
 * that implementations have to handle [OnPropertyChangedCallback]s..
 */
interface ViewModel : ObservableContainer, LifecycleOwner, EventChannelOwner {
    /**
     * Is called when this instance is about to be removed from memory.
     * This means that this object is no longer bound to a view and will never be. It is about to
     * be garbage collected.
     * Implementations should provide a method to deregister from callbacks, etc.
     *
     * @see [BaseViewModel.onDestroy]
     */
    fun destroy()

    /**
     * @see [androidx.lifecycle.ViewModel.getTag]
     */
    operator fun <T : Any> get(key: String): T?

    /**
     * @see [androidx.lifecycle.ViewModel.setTagIfAbsent]
     */
    fun <T : Any> initTag(key: String, newValue: T): T

    /**
     * Destroys all ViewModels in that list when the containing ViewModel is destroyed.
     */
    fun <VM : ViewModel, C : Collection<VM>> C.autoDestroy(): C = onEach { it.autoDestroy() }

    /**
     * Destroys the receiver ViewModel when the containing ViewModel is destroyed.
     */
    fun <VM : ViewModel> VM.autoDestroy(): VM = also { child ->
        val parentLifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                child.destroy()
            }
        }.also(this@ViewModel.lifecycle::addObserver)

        // If the child is destroyed for any reason it's listener to the parents lifecycle is removed to avoid leaks.
        child.lifecycle.addObserver(LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                this@ViewModel.lifecycle.removeObserver(parentLifecycleObserver)
            }
        })
    }

    /**
     * Sends all the events of a given list of (receiver type) ViewModels through the event channel of the ViewModel where this function is called in.
     */
    fun <VM : ViewModel, C : Collection<VM>> C.bindEvents(): C = onEach { it.bindEvents() }

    /**
     * Sends all the events of a given (receiver type) ViewModel through the event channel of the ViewModel where this function is called in.
     */
    fun <VM : ViewModel> VM.bindEvents(): VM = also { child ->
        child.eventChannel.addListener(this@ViewModel) { event -> this@ViewModel.eventChannel.invoke(event) }
    }

    /**
     * Sets [ObservableProperty.beforeSet] to a given function and returns that instance.
     */
    fun <T : Collection<ViewModel>> ObservableProperty.Provider<T>.asChildren(beforeSet: BeforeSet<T>? = null) = apply {
        beforeSet { old, new ->
            old.destroyAll()
            new.autoDestroy().bindEvents()
            beforeSet?.invoke(old, new)
        }
    }
}
