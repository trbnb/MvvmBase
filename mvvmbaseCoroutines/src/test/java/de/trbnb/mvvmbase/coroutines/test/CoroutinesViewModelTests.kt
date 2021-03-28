package de.trbnb.mvvmbase.coroutines.test

import androidx.lifecycle.Lifecycle
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.OnPropertyChangedCallback
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import de.trbnb.mvvmbase.events.EventChannel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class CoroutinesViewModelTests {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            MvvmBase.disableViewModelLifecycleThreadConstraints()
        }
    }

    @Test
    fun `viewModelScope is resolved correctly`() {
        val viewModel = object : BaseViewModel(), CoroutineViewModel {}

        viewModel.viewModelScope
    }

    @Test
    fun `exception thrown if viewModelScope cannot be resolved`() {
        val viewModel = object : CoroutineViewModel {
            override val eventChannel: EventChannel get() = TODO("Not yet implemented")
            override fun destroy(): Unit = TODO("Not yet implemented")
            override fun <T : Any> get(key: String): T = TODO("Not yet implemented")
            override fun <T : Any> initTag(key: String, newValue: T): T = TODO("Not yet implemented")
            override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback): Unit = TODO("Not yet implemented")
            override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback): Unit = TODO("Not yet implemented")
            override fun notifyPropertyChanged(propertyName: String): Unit = TODO("Not yet implemented")
            override fun getLifecycle(): Lifecycle = TODO("Not yet implemented")
        }

        Assertions.assertThrows(RuntimeException::class.java) { viewModel.viewModelScope }
    }
}
