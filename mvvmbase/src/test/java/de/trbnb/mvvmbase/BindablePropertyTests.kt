package de.trbnb.mvvmbase

import android.util.Size
import android.util.SizeF
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.utils.brFieldName
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import org.junit.Test

class BindablePropertyTests {

    class TestViewModel : BaseViewModel() {
        var text: String by bindable("foo")
        var userSetting: Boolean by bindable(false)
        var isLoading: Boolean? by bindable()
        var isDone: Boolean by bindableBoolean()
        var isDoneTwo: Boolean by bindable(false)
    }

    @Test
    fun `field ID from property name & type detection`() {
        val vm = TestViewModel()

        assert(vm::text.brFieldName() == "text")
        assert(vm::userSetting.brFieldName() == "userSetting")
        assert(vm::isLoading.brFieldName() == "isLoading")
        assert(vm::isDone.brFieldName() == "done")
        assert(vm::isDoneTwo.brFieldName() == "doneTwo")
    }

    @Test
    fun `supported state saving types`() {
        assert(savingStateInBindableSupports<Int>(1))
        assert(savingStateInBindableSupports<Short?>(1))
        assert(savingStateInBindableSupports<Array<String>>(1))
        assert(!savingStateInBindableSupports<List<Any>>(1))

        assert(!savingStateInBindableSupports<Size>(19))
        assert(!savingStateInBindableSupports<SizeF>(19))
        assert(savingStateInBindableSupports<Size>(21))
        assert(savingStateInBindableSupports<SizeF>(21))
    }

}
