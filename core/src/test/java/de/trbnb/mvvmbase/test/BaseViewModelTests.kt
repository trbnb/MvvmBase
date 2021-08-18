package de.trbnb.mvvmbase.test

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.DependsOn
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.observable.addOnPropertyChangedCallback
import de.trbnb.mvvmbase.observableproperty.observable
import de.trbnb.mvvmbase.utils.observe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BaseViewModelTests {
    @BeforeEach
    fun setup() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
    }

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

    @Test
    fun `@DependsOn`() {
        val viewModel = object : BaseViewModel() {
            var foo by observable("")

            @DependsOn("foo")
            val bar: String
                get() = "${foo}bar"
        }

        var barChanged = false
        var amountOfNotify = 0

        viewModel.addOnPropertyChangedCallback { _, _ ->
            amountOfNotify++
        }

        viewModel::bar.observe {
            barChanged = true
        }

        viewModel.foo = "foo"

        Assertions.assertEquals(true, barChanged)
        Assertions.assertEquals(2, amountOfNotify)
        Assertions.assertEquals("foobar", viewModel.bar)
    }
}
