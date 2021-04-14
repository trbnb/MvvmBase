package de.trbnb.mvvmbase.test

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.observable.addOnPropertyChangedCallback
import org.junit.jupiter.api.Test

class ObservableTests {
    @Test
    fun `property changed callback with Lifecycle`() {
        val observable = object : BaseViewModel() {}

        val lifecycleOwner = object : LifecycleOwner {
            private val lifecycle = LifecycleRegistry.createUnsafe(this).apply {
                currentState = Lifecycle.State.STARTED
            }
            override fun getLifecycle() = lifecycle
            fun destroy() { lifecycle.currentState = Lifecycle.State.DESTROYED }
        }

        var callbackWasTriggered = false
        observable.addOnPropertyChangedCallback(lifecycleOwner) { _, _ -> callbackWasTriggered = true }

        lifecycleOwner.destroy()
        observable.notifyPropertyChanged("")
        assert(!callbackWasTriggered)
    }
}
