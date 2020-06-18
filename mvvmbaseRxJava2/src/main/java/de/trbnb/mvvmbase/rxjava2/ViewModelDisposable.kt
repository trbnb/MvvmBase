package de.trbnb.mvvmbase.rxjava2

import de.trbnb.mvvmbase.ViewModel
import io.reactivex.disposables.CompositeDisposable
import java.io.Closeable

private const val COMPOSITE_DISPOSABLE_KEY = "de.trbnb.mvvmbase.rxjava2.CompositeDisposable"

/**
 * Gets [CompositeDisposable] that will immediately be disposed if the ViewModel is destroyed.
 */
val ViewModel.compositeDisposable: CompositeDisposable
    get() = (get(COMPOSITE_DISPOSABLE_KEY) ?: initTag(COMPOSITE_DISPOSABLE_KEY, ViewModelDisposableContainer())).disposable

internal class ViewModelDisposableContainer : Closeable {
    internal val disposable = CompositeDisposable()
    override fun close() {
        disposable.dispose()
    }
}
