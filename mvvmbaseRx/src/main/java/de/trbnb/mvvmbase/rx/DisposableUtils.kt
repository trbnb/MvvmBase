package de.trbnb.mvvmbase.rx

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.Lifecycle.Event.ON_DESTROY
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
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
