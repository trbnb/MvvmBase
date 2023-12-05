package de.trbnb.mvvmbase.rxjava3.test

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import de.trbnb.mvvmbase.databinding.BaseViewModel
import de.trbnb.mvvmbase.rxjava3.autoDispose
import de.trbnb.mvvmbase.rxjava3.compositeDisposable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext

@ExtendWith(InstantTaskExecutorRuleForJUnit5::class)
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
            override fun <T : ViewModel> create(modelClass: Class<T>): T = object : BaseViewModel() {} as T
        })[BaseViewModel::class.java]
        val compositeDisposable = viewModel.compositeDisposable
        viewModel.destroy()
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
        viewModel.destroy()
        assert(compositeDisposable.isDisposed)
    }
}

class InstantTaskExecutorRuleForJUnit5 : AfterEachCallback, BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}