package de.trbnb.mvvmbase.rxjava2

import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import io.reactivex.disposables.Disposable

/**
 * Disposes a [Disposable] when the given lifecycle has emitted the [ON_DESTROY] event.
 */
fun Disposable.autoDispose(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycle.addObserver(LifecycleEventObserver { _, event ->
        if (event == ON_DESTROY) {
            dispose()
        }
    })
}
