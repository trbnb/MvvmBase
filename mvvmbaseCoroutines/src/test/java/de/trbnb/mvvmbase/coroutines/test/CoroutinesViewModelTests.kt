package de.trbnb.mvvmbase.coroutines.test

import androidx.databinding.Observable
import androidx.lifecycle.Lifecycle
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.coroutines.CoroutineViewModel
import de.trbnb.mvvmbase.events.EventChannel
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.reflect.KProperty

class CoroutinesViewModelTests {
    @Test
    fun `viewModelScope is resolved correctly`() {
        val viewModel = object : BaseViewModel(), CoroutineViewModel {}

        viewModel.viewModelScope
    }

    @Test
    fun `exception thrown if viewModelScope cannot be resolved`() {
        val viewModel = object : CoroutineViewModel {
            override val eventChannel: EventChannel get() = TODO("Not yet implemented")
            override fun notifyChange() { TODO("Not yet implemented") }
            override fun notifyPropertyChanged(fieldId: Int) { TODO("Not yet implemented") }
            override fun notifyPropertyChanged(property: KProperty<*>) { TODO("Not yet implemented") }
            override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) { TODO("Not yet implemented") }
            override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) { TODO("Not yet implemented") }
            override fun onBind() { TODO("Not yet implemented") }
            override fun onUnbind() { TODO("Not yet implemented") }
            override fun onDestroy() { TODO("Not yet implemented") }
            override fun <T : Any> getTag(key: String): T? { TODO("Not yet implemented") }
            override fun <T : Any> setTagIfAbsent(key: String, newValue: T): T { TODO("Not yet implemented") }
            override fun getLifecycle(): Lifecycle { TODO("Not yet implemented") }
        }

        Assertions.assertThrows(RuntimeException::class.java) { viewModel.viewModelScope }
    }
}
