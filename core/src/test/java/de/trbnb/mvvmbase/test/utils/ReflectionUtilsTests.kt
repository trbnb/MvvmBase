package de.trbnb.mvvmbase.test.utils

import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.observableproperty.bindable
import de.trbnb.mvvmbase.utils.observe
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ReflectionUtilsTests {
    @BeforeEach
    fun setup() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
    }

    @Test
    fun `observe property`() {
        val observable = object : BaseViewModel() {
            var foo by bindable("")
        }

        var wasTriggered = false

        observable::foo.observe {
            wasTriggered = true
        }

        observable.foo = "kd"

        Assertions.assertEquals(true, wasTriggered)
    }
}