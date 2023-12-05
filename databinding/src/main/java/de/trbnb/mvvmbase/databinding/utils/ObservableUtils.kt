package de.trbnb.mvvmbase.databinding.utils

import androidx.databinding.Observable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Adds an [Observable.OnPropertyChangedCallback] and removes it when the lifecycle of [lifecycleOwner] is destroyed.
 */
public fun Observable.addOnPropertyChangedCallback(lifecycleOwner: LifecycleOwner, callback: Observable.OnPropertyChangedCallback) {
    addOnPropertyChangedCallback(callback)
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeOnPropertyChangedCallback(callback)
            }
        }
    })
}
