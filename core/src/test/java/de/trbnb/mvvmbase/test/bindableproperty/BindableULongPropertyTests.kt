package de.trbnb.mvvmbase.test.bindableproperty

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.bindableproperty.StateSaveOption
import de.trbnb.mvvmbase.bindableproperty.afterSet
import de.trbnb.mvvmbase.bindableproperty.beforeSet
import de.trbnb.mvvmbase.bindableproperty.bindableULong
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.bindableproperty.validate
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import de.trbnb.mvvmbase.test.TestPropertyChangedCallback
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalUnsignedTypes
class BindableULongPropertyTests {
    @BeforeEach
    fun setup() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
    }

    @Test
    fun `is zero the default value without explicit assignment`() {
        val viewModel = object : BaseViewModel() {
            val property by bindableULong()
        }

        assert(viewModel.property == 0.toULong())
    }

    @Test
    fun `does value assignment work`() {
        val viewModel = object : BaseViewModel() {
            var property by bindableULong()
        }

        val newValue = 3.toULong()
        assert(viewModel.property != newValue)

        viewModel.property = newValue
        assert(viewModel.property == newValue)
    }

    @Test
    fun `is afterSet() called`() {
        val oldValue = 4.toULong()
        val newValue = 7.toULong()
        val viewModel = object : BaseViewModel() {
            var property by bindableULong(oldValue)
                .afterSet { old, new ->
                    assert(newValue == new)
                    assert(oldValue == old)
                }
        }

        viewModel.property = newValue
    }

    @Test
    fun `is distinct() prohibting afterSet() invocation`() {
        val value = 4.toULong()
        var afterSetWasCalled = false
        val viewModel = object : BaseViewModel() {
            var propery by bindableULong(value)
                .distinct()
                .afterSet { _, _ -> afterSetWasCalled = true }
        }

        viewModel.propery = value
        assert(!afterSetWasCalled)
    }

    @Test
    fun `is beforeSet() called`() {
        val oldValue = 4.toULong()
        val newValue = 9.toULong()
        assert(oldValue != newValue)

        val viewModel = object : BaseViewModel() {
            var property by bindableULong(oldValue)
                .beforeSet { old, new ->
                    assert(old == oldValue)
                    assert(new == newValue)
                }
        }

        assert(viewModel.property == oldValue)
        viewModel.property = newValue
    }

    @Test
    fun `is validate() called`() {
        val oldValue = 0.toULong()
        val newValue = 50.toULong()
        val maxValue = 25.toULong()
        assert(oldValue != newValue)

        val viewModel = object : BaseViewModel() {
            var property by bindableULong(defaultValue = oldValue)
                .validate { old, new ->
                    assert(old == oldValue)
                    assert(new == newValue)
                    return@validate new.coerceAtMost(maxValue)
                }
        }

        assert(viewModel.property == oldValue)
        viewModel.property = newValue
        assert(viewModel.property == maxValue)
    }

    @Test
    fun `is correct field ID used for notifyPropertyChanged()`() {
        val propertyChangedCallback = TestPropertyChangedCallback()
        val viewModel = ViewModelWithBindable()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        viewModel.property = 5.toULong()
        assert("property" in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()
    }

    class ViewModelWithBindable : BaseViewModel() {
                var property by bindableULong()
    }

    @Test
    fun `automatic StateSaveOption is set correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = AutomaticSavedStateViewModel(savedStateHandle)

        val newValue = ULong.MAX_VALUE
        viewModel.supportedAutomatic = newValue
        assert(savedStateHandle.get<Long>("supportedAutomatic")?.toULong() == newValue)
    }

    class AutomaticSavedStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
                var supportedAutomatic by bindableULong()
    }

    @Test
    fun `manual StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val key = "Bar"
        val viewModel = ManualSavedStateViewModel(key, savedStateHandle)

        val newValue = 9.toULong()
        viewModel.property = newValue
        assert(savedStateHandle.get<Long>(key)?.toULong() == newValue)
    }

    class ManualSavedStateViewModel(
        key: String,
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ) : BaseStateSavingViewModel(savedStateHandle) {
                var property by bindableULong(stateSaveOption = StateSaveOption.Manual(key))
    }

    @Test
    fun `none StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = NoneSavedStateViewModel(savedStateHandle)

        val newValue = 6.toULong()
        viewModel.property = newValue
        assert(savedStateHandle.keys().isEmpty())
    }

    class NoneSavedStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
                var property by bindableULong(stateSaveOption = StateSaveOption.None)
    }

    @Test
    fun `distinct does prevent notifyPropertyChanged`() {
        val propertyChangedCallback = TestPropertyChangedCallback()
        val viewModel = ViewModelWithDistinct().apply {
            addOnPropertyChangedCallback(propertyChangedCallback)
        }

        viewModel.property = 3.toULong()
        assert("property" in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.property = 3.toULong()
        assert("property" !in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithDistinct : BaseViewModel() {
                var property by bindableULong()
            .distinct()
    }
}