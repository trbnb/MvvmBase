package de.trbnb.mvvmbase.databinding.test.bindableproperty

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.observableproperty.StateSaveOption
import de.trbnb.mvvmbase.databinding.BaseViewModel
import de.trbnb.mvvmbase.databinding.bindableproperty.afterSet
import de.trbnb.mvvmbase.databinding.bindableproperty.beforeSet
import de.trbnb.mvvmbase.databinding.bindableproperty.bindableLong
import de.trbnb.mvvmbase.databinding.bindableproperty.distinct
import de.trbnb.mvvmbase.databinding.bindableproperty.validate
import de.trbnb.mvvmbase.databinding.initDataBinding
import de.trbnb.mvvmbase.databinding.resetDataBinding
import de.trbnb.mvvmbase.databinding.savedstate.BaseStateSavingViewModel
import de.trbnb.mvvmbase.databinding.test.BR
import de.trbnb.mvvmbase.databinding.test.TestPropertyChangedCallback
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BindableLongPropertyTests {
    @BeforeEach
    fun setup() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
    }

    @Test
    fun `is zero the default value without explicit assignment`() {
        val viewModel = object : BaseViewModel() {
            val property by bindableLong()
        }

        assert(viewModel.property == 0.toLong())
    }

    @Test
    fun `does value assignment work`() {
        val viewModel = object : BaseViewModel() {
            var property by bindableLong()
        }

        val newValue = 3.toLong()
        assert(viewModel.property != newValue)

        viewModel.property = newValue
        assert(viewModel.property == newValue)
    }

    @Test
    fun `is afterSet() called`() {
        val oldValue = 4.toLong()
        val newValue = 7.toLong()
        val viewModel = object : BaseViewModel() {
            var property by bindableLong(oldValue)
                .afterSet { old, new ->
                    assert(newValue == new)
                    assert(oldValue == old)
                }
        }

        viewModel.property = newValue
    }

    @Test
    fun `is distinct() prohibting afterSet() invocation`() {
        val value = 4.toLong()
        var afterSetWasCalled = false
        val viewModel = object : BaseViewModel() {
            var propery by bindableLong(value)
                .distinct()
                .afterSet { _, _ -> afterSetWasCalled = true }
        }

        viewModel.propery = value
        assert(!afterSetWasCalled)
    }

    @Test
    fun `is beforeSet() called`() {
        val oldValue = 4.toLong()
        val newValue = 9.toLong()
        assert(oldValue != newValue)

        val viewModel = object : BaseViewModel() {
            var property by bindableLong(oldValue)
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
        val oldValue = 0.toLong()
        val newValue = 50.toLong()
        val maxValue = 25.toLong()
        assert(oldValue != newValue)

        val viewModel = object : BaseViewModel() {
            var property by bindableLong(defaultValue = oldValue)
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

        viewModel.property = 5.toLong()
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.manualProperty = 7.toLong()
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

        viewModel.property = 5.toLong()
        assert(BR._all in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.manualProperty = 2.toLong()
        assert(manualFieldId in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()
    }

    class ViewModelWithBindable(fieldId: Int) : BaseViewModel() {
        @get:Bindable
        var property by bindableLong()

        @get:Bindable
        var manualProperty by bindableLong(fieldId = fieldId)
    }

    @Test
    fun `automatic StateSaveOption is set correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = AutomaticStateViewModel(savedStateHandle)

        val newValue = 6.toLong()
        viewModel.supportedAutomatic = newValue
        assert(savedStateHandle.get<Long>("supportedAutomatic") == newValue)
    }

    class AutomaticStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var supportedAutomatic by bindableLong()
    }

    @Test
    fun `manual StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val key = "Bar"
        val viewModel = ManualStateViewModel(key, savedStateHandle)

        val newValue = 9.toLong()
        viewModel.property = newValue
        assert(savedStateHandle.get<Long>(key) == newValue)
    }

    class ManualStateViewModel(
        key: String,
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var property by bindableLong(stateSaveOption = StateSaveOption.Manual(key))
    }

    @Test
    fun `none StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = NoneStateViewModel(savedStateHandle)

        val newValue = 6.toLong()
        viewModel.property = newValue
        assert(savedStateHandle.keys().isEmpty())
    }

    class NoneStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var property by bindableLong(stateSaveOption = StateSaveOption.None)
    }

    @Test
    fun `distinct does prevent notifyPropertyChanged`() {
        val propertyChangedCallback = TestPropertyChangedCallback()
        val viewModel = ViewModelWithDistinct().apply {
            addOnPropertyChangedCallback(propertyChangedCallback)
        }

        viewModel.property = 3.toLong()
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.property = 3.toLong()
        assert(BR.property !in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithDistinct : BaseViewModel() {
        @get:Bindable
        var property by bindableLong()
            .distinct()
    }
}
