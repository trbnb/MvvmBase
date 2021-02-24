package de.trbnb.mvvmbase.test.bindableproperty

import android.util.Size
import android.util.SizeF
import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableBoolean
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import de.trbnb.mvvmbase.test.setSdkVersion
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BindablePropertySavedStateHandleTests {
    @BeforeEach
    fun setup() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
    }

    class TestViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var text: String by bindable("foo")
        @get:Bindable
        var userSetting: Boolean by bindable(false)
        @get:Bindable
        var nullableBoolean: Boolean? by bindable()
        @get:Bindable
        var isDone: Boolean by bindableBoolean()
        @get:Bindable
        var isDoneTwo: Boolean by bindable(false)
        var property: String? by bindable()
    }

    @Test
    fun `saved state integration in bindable properties`() {
        val handle = SavedStateHandle().apply {
            set("text", "Meh")
            set("isDone", true)
        }

        val viewModel = TestViewModel(handle)
        assert(viewModel.text == "Meh")
        assert(viewModel.userSetting.not())
        assert(viewModel.nullableBoolean == null)
        assert(viewModel.isDone)
        assert(!viewModel.isDoneTwo)
    }

    @Test
    fun `supported state saving types`() {
        assert(savingStateInBindableSupports<Int>())
        assert(savingStateInBindableSupports<Short?>())
        assert(savingStateInBindableSupports<Array<String>>())
        assert(!savingStateInBindableSupports<List<Any>>())
        assert(savingStateInBindableSupports<Enum<*>>())

        assert(!savingStateInBindableSupports<Size>())
        assert(!savingStateInBindableSupports<SizeF>())

        setSdkVersion(21)
        assert(savingStateInBindableSupports<Size>())
        assert(savingStateInBindableSupports<SizeF>())
    }
}
