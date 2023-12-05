package de.trbnb.mvvmbase.coroutines.test

import androidx.databinding.Observable
import androidx.lifecycle.Lifecycle
import de.trbnb.mvvmbase.databinding.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import de.trbnb.mvvmbase.events.EventChannel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty

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
            override fun notifyChange() = TODO("Not yet implemented")
            override fun notifyPropertyChanged(fieldId: Int) = TODO("Not yet implemented")
            override fun notifyPropertyChanged(property: KProperty<*>) {
                TODO("Not yet implemented")
            }
            override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) = TODO("Not yet implemented")
            override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) = TODO("Not yet implemented")
            override fun onBind() = TODO("Not yet implemented")
            override fun onUnbind() = TODO("Not yet implemented")
            override fun onDestroy() = TODO("Not yet implemented")
            override fun destroy() = TODO("Not yet implemented")
            override fun <T : Any> get(key: String) = TODO("Not yet implemented")
            override fun <T : Any> initTag(key: String, newValue: T) = TODO("Not yet implemented")
            override val lifecycle: Lifecycle
                get() = TODO("Not yet implemented")
            override val eventChannel: EventChannel get() = TODO("Not yet implemented")
        }

        Assertions.assertThrows(RuntimeException::class.java) { viewModel.viewModelScope }
    }
}
