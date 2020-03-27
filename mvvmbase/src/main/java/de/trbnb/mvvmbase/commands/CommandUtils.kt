package de.trbnb.mvvmbase.commands

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

internal fun Command<*, *>.observeLifecycle(lifecycle: Lifecycle) {
    lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            @Suppress("NON_EXHAUSTIVE_WHEN")
            when (event) {
                Lifecycle.Event.ON_PAUSE -> clearEnabledListeners()
                Lifecycle.Event.ON_DESTROY -> lifecycle.removeObserver(this)
            }
        }
    })
}
