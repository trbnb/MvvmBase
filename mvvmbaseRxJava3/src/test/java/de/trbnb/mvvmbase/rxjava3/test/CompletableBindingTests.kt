@file:Suppress("UNUSED_VARIABLE", "unused")

package de.trbnb.mvvmbase.rxjava3.test

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.rxjava3.RxViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.subjects.CompletableSubject
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class CompletableBindingTests {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            MvvmBase.disableViewModelLifecycleThreadConstraints()
        }
    }

    @Test
    fun `is false the default default value`() {
        val completable = CompletableSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by completable.toBindable()
        }

        assert(!viewModel.property)
    }

    @Test
    fun `is onError called`() {
        val completable = CompletableSubject.create()
        var isOnErrorCalled = false
        val onError = { _: Throwable -> isOnErrorCalled = true }

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by completable.toBindable(onError = onError)
        }

        completable.onError(RuntimeException())
        assert(isOnErrorCalled)
    }

    @Test
    fun `is onComplete called`() {
        val completable = CompletableSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by completable.toBindable()
        }

        completable.onComplete()
        assert(viewModel.property)
    }

    @Test
    fun `is notifyPropertyChanged() called (automatic field ID)`() {
        val completable = CompletableSubject.create()

        val viewModel = ViewModelWithBindable(completable)

        val propertyChangedCallback = TestPropertyChangedCallback()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        completable.onComplete()
        assert("property" in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithBindable(completable: Completable) : BaseViewModel(), RxViewModel {
                val property by completable.toBindable()
    }
}
