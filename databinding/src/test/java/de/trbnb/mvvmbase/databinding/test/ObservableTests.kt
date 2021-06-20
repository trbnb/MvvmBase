package de.trbnb.mvvmbase.databinding.test

import androidx.databinding.BaseObservable
import androidx.databinding.Observable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import de.trbnb.mvvmbase.databinding.utils.addOnPropertyChangedCallback
import org.junit.jupiter.api.Test

class ObservableTests {
    @Test
    fun `property changed callback with Lifecycle`() {
        val observable = BaseObservable()

        val lifecycleOwner = object : LifecycleOwner {
            private val lifecycle = LifecycleRegistry.createUnsafe(this).apply {
                currentState = Lifecycle.State.STARTED
            }
            override fun getLifecycle() = lifecycle
            fun destroy() { lifecycle.currentState = Lifecycle.State.DESTROYED }
        }

        var callbackWasTriggered = false
        observable.addOnPropertyChangedCallback(lifecycleOwner, object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                callbackWasTriggered = true
            }
        })

        lifecycleOwner.destroy()
        observable.notifyPropertyChanged(BR.enabled)
        assert(!callbackWasTriggered)
    }
}
