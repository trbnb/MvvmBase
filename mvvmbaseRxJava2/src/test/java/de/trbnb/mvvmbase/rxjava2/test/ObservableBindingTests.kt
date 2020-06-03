@file:Suppress("UNUSED_VARIABLE", "unused")

package de.trbnb.mvvmbase.rxjava2.test

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.rxjava2.RxViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import org.junit.Before
import org.junit.Test

class ObservableBindingTests {
    @Before
    fun setup() {
        MvvmBase.init<BR>()
    }

    @Test
    fun `is the given default value used`() {
        val observable: Observable<Int> = PublishSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by observable.toBindable(defaultValue = 3)
        }

        assert(viewModel.property == 3)
    }

    @Test
    fun `is null the default default value`() {
        val observable: Observable<Int> = PublishSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by observable.toBindable()
        }

        assert(viewModel.property == null)
    }

    @Test
    fun `is onError called`() {
        val observable: PublishSubject<Int> = PublishSubject.create()
        var isOnErrorCalled = false
        val onError = { _: Throwable -> isOnErrorCalled = true }

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by observable.toBindable(onError = onError)
        }

        observable.onError(RuntimeException())
        assert(isOnErrorCalled)
    }

    @Test
    fun `is onComplete called`() {
        val observable: PublishSubject<Int> = PublishSubject.create()
        var isOnCompleteCalled = false
        val onComplete = { isOnCompleteCalled = true }

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by observable.toBindable(onComplete = onComplete)
        }

        observable.onComplete()
        assert(isOnCompleteCalled)
    }

    @Test
    fun `are new values received`() {
        val observable: PublishSubject<Int> = PublishSubject.create()

        val viewModel = object : BaseViewModel(), RxViewModel {
            val property by observable.toBindable<Int>(defaultValue = 3)
        }

        val newValue = 55
        observable.onNext(newValue)
        assert(viewModel.property == newValue)
    }

    @Test
    fun `is notifyPropertyChanged() called (automatic field ID)`() {
        val observable: PublishSubject<Int> = PublishSubject.create()

        val viewModel = ViewModelWithBindable(observable)

        val propertyChangedCallback = TestPropertyChangedCallback()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        // imitate first getter call (usually done by binding)
        // first getter call initiates the field id
        viewModel.property

        val newValue = 55
        observable.onNext(newValue)
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
    }

    @Test
    fun `is notifyPropertyChanged() called (manual field ID)`() {
        val observable: PublishSubject<Int> = PublishSubject.create()

        val viewModel = ViewModelWithBindable(observable, fieldId = BR.property)

        val propertyChangedCallback = TestPropertyChangedCallback()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        val newValue = 55
        observable.onNext(newValue)
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithBindable(observable: Observable<Int>, fieldId: Int? = null) : BaseViewModel(), RxViewModel {
        @get:Bindable
        val property by observable.toBindable<Int>(defaultValue = 3, fieldId = fieldId)
    }
}
