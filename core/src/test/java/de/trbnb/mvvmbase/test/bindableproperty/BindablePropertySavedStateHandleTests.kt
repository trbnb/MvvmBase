package de.trbnb.mvvmbase.test.bindableproperty

import android.util.Size
import android.util.SizeF
import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.observableproperty.observable
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BindablePropertySavedStateHandleTests {
    @BeforeEach
    fun setup() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
    }

    class TestViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
        var text: String by observable("foo")
        var userSetting: Boolean by observable(false)
        var nullableBoolean: Boolean? by observable()
        var isDone: Boolean by observable(false)
        var property: String? by observable()
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
    }

    @Test
    fun `supported state saving types`() {
        assert(savingStateInBindableSupports<Int>())
        assert(savingStateInBindableSupports<Short?>())
        assert(savingStateInBindableSupports<Array<String>>())
        assert(!savingStateInBindableSupports<List<Any>>())
        assert(savingStateInBindableSupports<Enum<*>>())

        assert(savingStateInBindableSupports<Size>())
        assert(savingStateInBindableSupports<SizeF>())
    }
}
