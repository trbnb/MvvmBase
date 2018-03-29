package de.trbnb.mvvmbase

import android.databinding.Bindable
import org.junit.Test

class BindablePropertyTests {

    class TestViewModel : BaseViewModel() {
        @get:Bindable
        var foo by bindable(0)
    }

    @Test
    fun `auto detect field ID`() {
        val propertyChangedListener = PropertyChangedMapListener()

        val vm = TestViewModel()

        vm.addOnPropertyChangedCallback(propertyChangedListener)

        vm.foo = 3

        //TODO: check if foo changed
    }
}
