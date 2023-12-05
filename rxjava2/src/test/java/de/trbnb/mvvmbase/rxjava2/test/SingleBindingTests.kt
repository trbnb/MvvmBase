@file:Suppress("UNUSED_VARIABLE", "unused")

package de.trbnb.mvvmbase.rxjava2.test

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.databinding.BaseViewModel
import de.trbnb.mvvmbase.databinding.initDataBinding
import de.trbnb.mvvmbase.rxjava2.RxViewModel
import io.reactivex.Single
import io.reactivex.subjects.SingleSubject
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class SingleBindingTests {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            MvvmBase.initDataBinding().disableViewModelLifecycleThreadConstraints()
        }
    }

    @Test
    fun `is the given default value used`() {
        val single: Single<Int> = SingleSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by single.toBindable(defaultValue = 3)
        }

        assert(viewModel.property == 3)
    }

    @Test
    fun `is null the default default value`() {
        val single: Single<Int> = SingleSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by single.toBindable()
        }

        assert(viewModel.property == null)
    }

    @Test
    fun `is onError called`() {
        val single: SingleSubject<Int> = SingleSubject.create()
        var isOnErrorCalled = false
        val onError = { _: Throwable -> isOnErrorCalled = true }

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by single.toBindable(onError = onError)
        }

        single.onError(RuntimeException())
        assert(isOnErrorCalled)
    }

    @Test
    fun `are new values received`() {
        val single: SingleSubject<Int> = SingleSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by single.toBindable<Int>(defaultValue = 3)
        }

        val newValue = 55
        single.onSuccess(newValue)
        assert(viewModel.property == newValue)
    }

    @Test
    fun `is notifyPropertyChanged() called (automatic field ID)`() {
        val single: SingleSubject<Int> = SingleSubject.create()

        val viewModel = ViewModelWithBindable(single)

        val propertyChangedCallback = TestPropertyChangedCallback()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        val newValue = 55
        single.onSuccess(newValue)
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithBindable(single: Single<Int>) : BaseViewModel(), RxViewModel {
        @get:Bindable
        val property by single.toBindable<Int>(defaultValue = 3)
    }
}
