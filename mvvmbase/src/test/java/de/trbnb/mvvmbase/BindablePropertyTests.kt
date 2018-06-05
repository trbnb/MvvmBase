package de.trbnb.mvvmbase

import de.trbnb.mvvmbase.bindableproperty.BindableProperty
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.brFieldName
import org.junit.Test
import kotlin.reflect.KProperty0
import kotlin.reflect.jvm.isAccessible

class BindablePropertyTests {

    class TestViewModel : BaseViewModel() {
        var text: String by bindable("foo")
        var userSetting: Boolean by bindable(false)
        var isLoading: Boolean? by bindable()
    }

    @Suppress("UNCHECKED_CAST", "SimplifyBooleanWithConstants")
    @Test
    fun `isBoolean detection`() {
        val vm = TestViewModel()

        fun KProperty0<*>.delegateIsBooleanBindableProperty(): Boolean {
            isAccessible = true
            return (getDelegate() as BindableProperty<TestViewModel, *>).isBoolean
        }

        assert(vm::text.delegateIsBooleanBindableProperty() == false)
        assert(vm::userSetting.delegateIsBooleanBindableProperty() == true)
        assert(vm::isLoading.delegateIsBooleanBindableProperty() == true)
    }

    @Test
    fun `field ID from property name & type detection`() {
        val vm = TestViewModel()

        assert(vm::text.brFieldName(false) == "text")
        assert(vm::userSetting.brFieldName(false) == "userSetting")
        assert(vm::isLoading.brFieldName(true) == "loading")
        assert(vm::isLoading.brFieldName(false) == "isLoading")
    }

}
