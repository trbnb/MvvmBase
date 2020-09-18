@file:Suppress("EXPERIMENTAL_API_USAGE")

package de.trbnb.mvvmbase.coroutines.test

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FlowBindableTests {
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(TestCoroutineDispatcher())
        MvvmBase.init<BR>()
    }

    @Test
    fun `is the given default value used`() {
        val flow: Flow<Int> = flowOf()

        val viewModel = object : BaseViewModel(), CoroutineViewModel {
            val property by flow.toBindable(defaultValue = 3)
        }

        assert(viewModel.property == 3)
    }

    @Test
    fun `is null the default default value`() {
        val flow: Flow<Int> = flowOf()

        val viewModel = object : BaseViewModel(), CoroutineViewModel {
            val property by flow.toBindable()
        }

        assert(viewModel.property == null)
    }

    @Test
    fun `is onError called`() {
        val flow: Flow<Int> = flow {
            throw Exception()
        }
        var isOnErrorCalled = false

        val viewModel = object : BaseViewModel(), CoroutineViewModel {
            val property by flow.toBindable(onException = { isOnErrorCalled = true })
        }

        assert(isOnErrorCalled)
    }

    @Test
    fun foo() {
        flowOf(3, 5)
            .onCompletion { emit(1000) }
            .onEach { println(it) }
            .launchIn(TestCoroutineScope())
    }

    @Test
    fun `is onComplete called`() {
        val flow: Flow<Int> = flowOf(3, 4)
        var isOnCompleteCalled = false

        val viewModel = object : BaseViewModel(), CoroutineViewModel {
            val property by flow.toBindable(onCompletion = { isOnCompleteCalled = true })
        }

        assert(isOnCompleteCalled)
    }

    @Test
    fun `are new values received`() {
        val flow = MutableStateFlow(4)

        val viewModel = object : BaseViewModel(), CoroutineViewModel {
            val property by flow.toBindable(defaultValue = 3)
        }

        val newValue = 55
        flow.value = newValue
        assert(viewModel.property == newValue)
    }

    @Test
    fun `is notifyPropertyChanged() called (automatic field ID)`() {
        val flow = MutableStateFlow(4)

        val viewModel = ViewModelWithBindable(flow)

        val propertyChangedCallback = TestPropertyChangedCallback()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        val newValue = 55
        flow.value = newValue
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
    }

    @Test
    fun `is notifyPropertyChanged() called (manual field ID)`() {
        val flow = MutableStateFlow(4)

        val viewModel = ViewModelWithBindable(flow, fieldId = BR.property)

        val propertyChangedCallback = TestPropertyChangedCallback()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        val newValue = 55
        flow.value = newValue
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithBindable(flow: Flow<Int>, fieldId: Int? = null) : BaseViewModel(), CoroutineViewModel {
        @get:Bindable
        val property by flow.toBindable(defaultValue = 3, fieldId = fieldId)
    }
}
