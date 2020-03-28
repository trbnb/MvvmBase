package de.trbnb.mvvmbase.rxjava3.test

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.rxjava3.autoDispose
import de.trbnb.mvvmbase.rxjava3.compositeDisposable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.junit.Test

class DisposableTests {
    @Test
    fun `compositeDisposable extension is the same for a ViewModel`() {
        val viewModel = object : BaseViewModel() {}
        val firstCompositeDisposable = viewModel.compositeDisposable
        val secondCompositeDisposable = viewModel.compositeDisposable

        assert(firstCompositeDisposable === secondCompositeDisposable)
    }

    @Test
    fun `compositeDisposable is disposed when ViewModelStore is cleared`() {
        val viewModelStore = ViewModelStore()
        val viewModel = ViewModelProvider(viewModelStore, object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel?> create(modelClass: Class<T>) = object : BaseViewModel() {} as T
        })[BaseViewModel::class.java]
        val compositeDisposable = viewModel.compositeDisposable
        viewModel.onDestroy()
        viewModelStore.clear()
        assert(compositeDisposable.isDisposed)
    }

    @Test
    fun `autoDispose disposes in onDestroy`() {
        val compositeDisposable = CompositeDisposable()
        val viewModel = object : BaseViewModel() {
            init {
                compositeDisposable.autoDispose(this)
            }
        }
        viewModel.onDestroy()
        assert(compositeDisposable.isDisposed)
    }
}