@file:Suppress("UNUSED_VARIABLE", "unused")

package de.trbnb.mvvmbase.rxjava3.test

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.rxjava3.RxViewModel
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.SingleSubject
import org.junit.Before
import org.junit.Test

class SingleBindingTests {
    @Before
    fun setup() {
        MvvmBase.init<BR>()
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

        // imitate first getter call (usually done by binding)
        // first getter call initiates the field id
        viewModel.property

        val newValue = 55
        single.onSuccess(newValue)
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
    }

    @Test
    fun `is notifyPropertyChanged() called (manual field ID)`() {
        val single: SingleSubject<Int> = SingleSubject.create()

        val viewModel = ViewModelWithBindable(single, fieldId = BR.property)

        val propertyChangedCallback = TestPropertyChangedCallback()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        val newValue = 55
        single.onSuccess(newValue)
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithBindable(single: Single<Int>, fieldId: Int? = null) : BaseViewModel(), RxViewModel {
        @get:Bindable
        val property by single.toBindable<Int>(defaultValue = 3, fieldId = fieldId)
    }
}
