package de.trbnb.mvvmbase.databinding.test.bindableproperty

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.bindableproperty.StateSaveOption
import de.trbnb.mvvmbase.databinding.BaseViewModel
import de.trbnb.mvvmbase.databinding.bindableproperty.afterSet
import de.trbnb.mvvmbase.databinding.bindableproperty.beforeSet
import de.trbnb.mvvmbase.databinding.bindableproperty.bindable
import de.trbnb.mvvmbase.databinding.bindableproperty.distinct
import de.trbnb.mvvmbase.databinding.bindableproperty.validate
import de.trbnb.mvvmbase.databinding.initDataBinding
import de.trbnb.mvvmbase.databinding.resetDataBinding
import de.trbnb.mvvmbase.databinding.savedstate.BaseStateSavingViewModel
import de.trbnb.mvvmbase.databinding.test.TestPropertyChangedCallback
import de.trbnb.mvvmbase.databinding.test.BR
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BindablePropertyTests {
    @BeforeEach
    fun setup() {
        MvvmBase.disableViewModelLifecycleThreadConstraints()
    }

    @Test
    fun `is null the default value without explicit assignment`() {
        val viewModel = object : BaseViewModel() {
            val property: String? by bindable()
        }

        assert(viewModel.property == null)
    }

    @Test
    fun `does value assignment work`() {
        val viewModel = object : BaseViewModel() {
            var property: String by bindable("")
        }

        val newValue = "Foo"
        assert(viewModel.property != newValue)

        viewModel.property = newValue
        assert(viewModel.property == newValue)
    }

    @Test
    fun `is afterSet() called`() {
        val oldValue = ""
        val newValue = "Foo"
        val viewModel = object : BaseViewModel() {
            var property: String by bindable(oldValue)
                .afterSet { old, new ->
                    assert(newValue == new)
                    assert(oldValue == old)
                }
        }

        viewModel.property = newValue
    }

    @Test
    fun `is distinct() prohibting afterSet() invocation`() {
        val value = "Foo"
        var afterSetWasCalled = false
        val viewModel = object : BaseViewModel() {
            var propery: String by bindable(value)
                .distinct()
                .afterSet { _, _ -> afterSetWasCalled = true }
        }

        viewModel.propery = value
        assert(!afterSetWasCalled)
    }

    @Test
    fun `is beforeSet() called`() {
        val oldValue = "Foo"
        val newValue = "Bar"
        assert(oldValue != newValue)

        val viewModel = object : BaseViewModel() {
            var property: String by bindable(oldValue)
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
        val oldValue = 0
        val newValue = 50
        val maxValue = 25
        assert(oldValue != newValue)

        val viewModel = object : BaseViewModel() {
            var property: Int by bindable(defaultValue = oldValue)
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
    fun `validate() returning null is handled properly`() {
        val oldValue = 0
        val newValue = 50
        assert(oldValue != newValue)

        val viewModel = object : BaseViewModel() {
            var property: Int? by bindable<Int?>(defaultValue = oldValue)
                .validate { old, new ->
                    assert(old == oldValue)
                    assert(new == newValue)
                    return@validate null
                }
        }

        assert(viewModel.property == oldValue)
        viewModel.property = newValue
        assert(viewModel.property == null)
    }

    @Test
    fun `is correct field ID used for notifyPropertyChanged()`() {
        MvvmBase.initDataBinding()
        val propertyChangedCallback = TestPropertyChangedCallback()
        val manualFieldId = BR.vm
        val viewModel = ViewModelWithBindable(manualFieldId)
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        viewModel.stringProperty = ""
        assert(BR.stringProperty in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.booleanProperty = true
        assert(BR.booleanProperty in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.isSomething = true
        assert(BR.something in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.manualProperty = ""
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

        viewModel.stringProperty = ""
        assert(BR._all in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.booleanProperty = true
        assert(BR._all in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.isSomething = true
        assert(BR._all in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.manualProperty = ""
        assert(manualFieldId in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()
    }

    class ViewModelWithBindable(fieldId: Int) : BaseViewModel() {
        @get:Bindable
        var stringProperty: String? by bindable()

        @get:Bindable
        var booleanProperty: Boolean? by bindable()

        @get:Bindable
        var isSomething: Boolean by bindable(false)

        @get:Bindable
        var manualProperty: Any? by bindable(fieldId = fieldId)
    }

    @Test
    fun `automatic StateSaveOption is set correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = AutomaticSavedStateViewModel(savedStateHandle)

        val newValue = "Foo"
        viewModel.supportedAutomatic = newValue
        assert(savedStateHandle.get<String>("supportedAutomatic") == newValue)

        val newNotSupportedValue = listOf("Foo")
        viewModel.notSupportedAutomatic = newNotSupportedValue
        assert(!savedStateHandle.contains("notSupportedAutomatic"))
    }

    class AutomaticSavedStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var supportedAutomatic by bindable("")

        @get:Bindable
        var notSupportedAutomatic: Any by bindable(Any())
    }

    @Test
    fun `manual StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val key = "Bar"
        val viewModel = ManualSavedStateViewModel(key, savedStateHandle)

        val newValue = "Foo"
        viewModel.property = newValue
        assert(savedStateHandle.get<String>(key) == newValue)
    }

    class ManualSavedStateViewModel(
        key: String,
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var property by bindable("", stateSaveOption = StateSaveOption.Manual(key))
    }

    @Test
    fun `none StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = NoneSavedStateViewModel(savedStateHandle)

        val newValue = "Foo"
        viewModel.property = newValue
        assert(savedStateHandle.keys().isEmpty())
    }

    class NoneSavedStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
        @get:Bindable
        var property by bindable("", stateSaveOption = StateSaveOption.None)
    }

    @Test
    fun `distinct does prevent notifyPropertyChanged`() {
        val propertyChangedCallback = TestPropertyChangedCallback()
        val viewModel = ViewModelWithDistinct().apply {
            addOnPropertyChangedCallback(propertyChangedCallback)
        }

        viewModel.property = "Foo"
        assert(BR.property in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.property = "Foo"
        assert(BR.property !in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithDistinct : BaseViewModel() {
        @get:Bindable
        var property by bindable("")
            .distinct()
    }
}
