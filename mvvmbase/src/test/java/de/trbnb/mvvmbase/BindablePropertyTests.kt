package de.trbnb.mvvmbase

import android.util.Size
import android.util.SizeF
import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import de.trbnb.mvvmbase.utils.brFieldName
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import org.junit.Test

class BindablePropertyTests {
    class TestViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
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

    @Test
    fun `saved state integration in bindable properties`() {
        val handle = SavedStateHandle().apply {
            set("text", "Meh")
            set("isLoading", false)
            set("isDone", true)
        }

        val viewModel = TestViewModel(handle)
        assert(viewModel.text == "Meh")
        assert(viewModel.userSetting.not())
        assert(viewModel.isLoading == false)
        assert(viewModel.isDone)
        assert(viewModel.isDoneTwo.not())
    }
}
