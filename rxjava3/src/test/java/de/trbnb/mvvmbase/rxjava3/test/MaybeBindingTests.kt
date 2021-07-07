@file:Suppress("UNUSED_VARIABLE", "unused")

package de.trbnb.mvvmbase.rxjava3.test

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.databinding.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.databinding.initDataBinding
import de.trbnb.mvvmbase.rxjava3.RxViewModel
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.subjects.MaybeSubject
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class MaybeBindingTests {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            MvvmBase.initDataBinding().disableViewModelLifecycleThreadConstraints()
        }
    }

    @Test
    fun `is the given default value used`() {
        val maybe: Maybe<Int> = MaybeSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by maybe.toBindable(defaultValue = 3)
        }

        assert(viewModel.property == 3)
    }

    @Test
    fun `is null the default default value`() {
        val maybe: Maybe<Int> = MaybeSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by maybe.toBindable()
        }

        assert(viewModel.property == null)
    }

    @Test
    fun `is onError called`() {
        val maybe: MaybeSubject<Int> = MaybeSubject.create()
        var isOnErrorCalled = false
        val onError = { _: Throwable -> isOnErrorCalled = true }

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by maybe.toBindable(onError = onError)
        }

        maybe.onError(RuntimeException())
        assert(isOnErrorCalled)
    }

    @Test
    fun `is onComplete called`() {
        val maybe: MaybeSubject<Int> = MaybeSubject.create()
        var isOnCompleteCalled = false
        val onComplete = { isOnCompleteCalled = true }

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by maybe.toBindable(onComplete = onComplete)
        }

        maybe.onComplete()
        assert(isOnCompleteCalled)
    }

    @Test
    fun `are new values received`() {
        val maybe: MaybeSubject<Int> = MaybeSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by maybe.toBindable<Int>(defaultValue = 3)
        }

        val newValue = 55
        maybe.onSuccess(newValue)
        assert(viewModel.property == newValue)
    }

    @Test
    fun `is notifyPropertyChanged() called (automatic field ID)`() {
        val maybe: MaybeSubject<Int> = MaybeSubject.create()

        val viewModel = ViewModelWithBindable(maybe)

        val propertyChangedCallback = TestPropertyChangedCallback()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        val newValue = 55
        maybe.onSuccess(newValue)
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithBindable(maybe: Maybe<Int>) : BaseViewModel(), RxViewModel {
                val property by maybe.toBindable<Int>(defaultValue = 3)
    }
}
