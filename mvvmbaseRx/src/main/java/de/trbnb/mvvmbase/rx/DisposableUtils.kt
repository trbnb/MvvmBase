package de.trbnb.mvvmbase.rx

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

/**
 * Disposes a [Disposable] when the given lifecycle has emitted the [ON_DESTROY] event.
 */
fun Disposable.autoDispose(lifecycle: Lifecycle) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(ON_DESTROY)
        fun onDestroy() {
            dispose()
        }
    })
}
