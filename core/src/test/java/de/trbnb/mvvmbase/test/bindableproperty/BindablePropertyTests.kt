package de.trbnb.mvvmbase.test.bindableproperty

import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.observableproperty.StateSaveOption
import de.trbnb.mvvmbase.observableproperty.bindable
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import de.trbnb.mvvmbase.test.TestPropertyChangedCallback
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
        val propertyChangedCallback = TestPropertyChangedCallback()
        val viewModel = ViewModelWithBindable()
        viewModel.addOnPropertyChangedCallback(propertyChangedCallback)

        viewModel.stringProperty = ""
        assert("stringProperty" in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.booleanProperty = true
        assert("booleanProperty" in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.isSomething = true
        assert("isSomething" in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()
    }

    class ViewModelWithBindable : BaseViewModel() {
                var stringProperty: String? by bindable()

                var booleanProperty: Boolean? by bindable()

                var isSomething: Boolean by bindable(false)
    }

    @Test
    fun `automatic StateSaveOption is set correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = AutomaticStateViewModel(savedStateHandle)

        val newValue = "Foo"
        viewModel.supportedAutomatic = newValue
        assert(savedStateHandle.get<String>("supportedAutomatic") == newValue)

        val newNotSupportedValue = listOf("Foo")
        viewModel.notSupportedAutomatic = newNotSupportedValue
        assert(!savedStateHandle.contains("notSupportedAutomatic"))
    }

    class AutomaticStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
                var supportedAutomatic by bindable("")

                var notSupportedAutomatic: Any by bindable(Any())
    }

    @Test
    fun `manual StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val key = "Bar"
        val viewModel = ManualStateViewModel(key, savedStateHandle)

        val newValue = "Foo"
        viewModel.property = newValue
        assert(savedStateHandle.get<String>(key) == newValue)
    }

    class ManualStateViewModel(
        key: String,
        savedStateHandle: SavedStateHandle = SavedStateHandle()
    ) : BaseStateSavingViewModel(savedStateHandle) {
                var property by bindable("", stateSaveOption = StateSaveOption.Manual(key))
    }

    @Test
    fun `none StateSaveOption is working correctly`() {
        val savedStateHandle = SavedStateHandle()
        val viewModel = NoneStateViewModel(savedStateHandle)

        val newValue = "Foo"
        viewModel.property = newValue
        assert(savedStateHandle.keys().isEmpty())
    }

    class NoneStateViewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) : BaseStateSavingViewModel(savedStateHandle) {
                var property by bindable("", stateSaveOption = StateSaveOption.None)
    }

    @Test
    fun `distinct does prevent notifyPropertyChanged`() {
        val propertyChangedCallback = TestPropertyChangedCallback()
        val viewModel = ViewModelWithDistinct().apply {
            addOnPropertyChangedCallback(propertyChangedCallback)
        }

        viewModel.property = "Foo"
        assert("property" in propertyChangedCallback.changedPropertyIds)
        propertyChangedCallback.clear()

        viewModel.property = "Foo"
        assert("property" !in propertyChangedCallback.changedPropertyIds)
    }

    class ViewModelWithDistinct : BaseViewModel() {
                var property by bindable("")
            .distinct()
    }
}
