package de.trbnb.mvvmbase.databinding.test.bindableproperty

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.bindableproperty.StateSaveOption
import de.trbnb.mvvmbase.databinding.BaseViewModel
import de.trbnb.mvvmbase.databinding.bindableproperty.afterSet
import de.trbnb.mvvmbase.databinding.bindableproperty.beforeSet
import de.trbnb.mvvmbase.databinding.bindableproperty.bindableFloat
import de.trbnb.mvvmbase.databinding.bindableproperty.distinct
import de.trbnb.mvvmbase.databinding.bindableproperty.validate
import de.trbnb.mvvmbase.databinding.initDataBinding
import de.trbnb.mvvmbase.databinding.resetDataBinding
import de.trbnb.mvvmbase.databinding.savedstate.BaseStateSavingViewModel
import de.trbnb.mvvmbase.databinding.test.TestPropertyChangedCallback
import de.trbnb.mvvmbase.databinding.test.BR
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BindableFloatPropertyTests {
    @BeforeEach
    fun setup() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
    }

    @Test
    fun `is zero the default value without explicit assignment`() {
        val viewModel = object : BaseViewModel() {
            val property by bindableFloat()
        }

        assert(viewModel.property == 0f)
    }

    @Test
    fun `does value assignment work`() {
        val viewModel = object : BaseViewModel() {
            var property by bindableFloat()
        }

        val newValue = 3f
        assert(viewModel.property != newValue)

        viewModel.property = newValue
        assert(viewModel.property == newValue)
    }

    @Test
    fun `is afterSet() called`() {
        val oldValue = 4f
        val newValue = 7f
        val viewModel = object : BaseViewModel() {
            var property by bindableFloat(oldValue)
                .afterSet { old, new ->
                    assert(newValue == new)
                    assert(oldValue == old)
                }
        }

        viewModel.property = newValue
    }

    @Test
    fun `is distinct() prohibting afterSet() invocation`() {
        val value = 4f
        var afterSetWasCalled = false
        val viewModel = object : BaseViewModel() {
            var propery by bindableFloat(value)
                .distinct()
                .afterSet { _, _ -> afterSetWasCalled = true }
        }

        viewModel.propery = value
        assert(!afterSetWasCalled)
    }

    @Test
    fun `is beforeSet() called`() {
        val oldValue = 4f
        val newValue = 9f
        assert(oldValue != newValue)

        val viewModel = object : BaseViewModel() {
            var property by bindableFloat(oldValue)
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
        val oldValue = 0f
        val newValue = 50f
        val maxValue = 25f
        assert(oldValue != newValue)

        val viewModel = object : BaseViewModel() {
            var property by bindableFloat(defaultValue = oldValue)
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
        MvvmBase.initDataBinding()
        val propertyChangedCallback = TestPropertyChangedCallback()
        val manualFieldId = BR.vm
        val viewModel = ViewModelWithBindable(manualFieldId)
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        viewModel.property = 5f
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.manualProperty = 7f
        assert(manualFieldId in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()
    }

    @Test
    fun `field ID falls back to _all`() {
        // undo MvvmBase.initDataBinding() call
        MvvmBase.resetDataBinding()

        val propertyChangedCallback = TestPropertyChangedCallback()
        val manualFieldId = BR.vm
        val viewModel = ViewModelWithBindable(manualFieldId)
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        viewModel.property = 5f
        assert(BR._all in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.manualProperty = 2f
        assert(manualFieldId in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()
    }

    class ViewModelWithBindable(fieldId: Int) : BaseViewModel() {
        @get:Bindable
        var property by bindableFloat()

        @get:Bindable
        var manualProperty by bindableFloat(fieldId = fieldId)
    }

    @Test
    fun `automatic StateSaveOption is set correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = AutomaticSavedStateViewModel(savedStateHandle)

        val newValue = 6f
        viewModel.supportedAutomatic = newValue
        assert(savedStateHandle.get<Float>("supportedAutomatic") == newValue)
    }

    class AutomaticSavedStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var supportedAutomatic by bindableFloat()
    }

    @Test
    fun `manual StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val key = "Bar"
        val viewModel = ManualSavedStateViewModel(key, savedStateHandle)

        val newValue = 9f
        viewModel.property = newValue
        assert(savedStateHandle.get<Float>(key) == newValue)
    }

    class ManualSavedStateViewModel(
        key: String,
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var property by bindableFloat(stateSaveOption = StateSaveOption.Manual(key))
    }

    @Test
    fun `none StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = NoneSavedStateViewModel(savedStateHandle)

        val newValue = 6f
        viewModel.property = newValue
        assert(savedStateHandle.keys().isEmpty())
    }

    class NoneSavedStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var property by bindableFloat(stateSaveOption = StateSaveOption.None)
    }

    @Test
    fun `distinct does prevent notifyPropertyChanged`() {
        val propertyChangedCallback = TestPropertyChangedCallback()
        val viewModel = ViewModelWithDistinct().apply {
            addOnPropertyChangedCallback(propertyChangedCallback)
        }

        viewModel.property = 3f
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.property = 3f
        assert(BR.property !in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithDistinct : BaseViewModel() {
        @get:Bindable
        var property by bindableFloat()
            .distinct()
    }
}
