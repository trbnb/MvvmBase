package de.trbnb.mvvmbase.observable

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import de.trbnb.mvvmbase.OnPropertyChangedCallback

/**
 * Adds an [OnPropertyChangedCallback] and removes it when the lifecycle of [lifecycleOwner] is destroyed.
 */
public fun ObservableContainer.addOnPropertyChangedCallback(lifecycleOwner: LifecycleOwner, callback: OnPropertyChangedCallback) {
    addOnPropertyChangedCallback(callback)
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeOnPropertyChangedCallback(callback)
            }
        }
    })
}
