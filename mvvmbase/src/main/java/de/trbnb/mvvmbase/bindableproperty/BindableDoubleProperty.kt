package de.trbnb.mvvmbase.bindableproperty

import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.utils.resolveFieldId
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] after a value is set.
 * The getter is not affected.
 *
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start.
 * @param savedStateKey Key to be used to write to/read from [ViewModel.savedStateHandle].
 */
class BindableDoubleProperty(
    viewModel: ViewModel,
    private var fieldId: Int?,
    defaultValue: Double,
    private val savedStateKey: String? = null
) : BindablePropertyBase() {
    /**
     * Gets or sets the stored value.
     */
    private var value = defaultValue

    /**
     * Gets or sets a function that will be invoked if a new value is about to be set.
     * The first parameter is the old value and the second parameter is the new value.
     *
     * This function will not be invoked if [BindablePropertyBase.distinct] is true and the new value
     * is equal to the old value.
     */
    internal var beforeSet: ((old: Double, new: Double) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: ((old: Double, new: Double) -> Double)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: ((new: Double) -> Unit)? = null

    init {
        if (savedStateKey != null) {
            viewModel.withSavedStateHandle { savedStateHandle ->
                if (savedStateKey in savedStateHandle) {
                    this.value = savedStateHandle.get(savedStateKey) ?: return@withSavedStateHandle
                }
            }
        }
    }

    operator fun getValue(thisRef: ViewModel, property: KProperty<*>) = value

    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Double) {
        if (fieldId == null) {
            fieldId = property.resolveFieldId()
        }

        if (distinct && this.value == value) {
            return
        }

        beforeSet?.invoke(this.value, value)
        this.value = validate?.invoke(this.value, value) ?: value
        thisRef.notifyPropertyChanged(fieldId ?: BR._all)
        savedStateKey?.let { thisRef.savedStateHandle?.set(savedStateKey, this.value) }
        afterSet?.invoke(this.value)
    }
}

/**
 * Creates a new [BindableDoubleProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param savedStateKey Key to be used to write to/read from [ViewModel.savedStateHandle].
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 */
fun ViewModel.bindableDouble(
    defaultValue: Double = 0.0,
    savedStateKey: String? = null,
    fieldId: Int? = null
) = BindableDoubleProperty(this, fieldId, defaultValue, savedStateKey)

/**
 * Sets [BindableDoubleProperty.beforeSet] of a [BindableDoubleProperty] instance to a given function and
 * returns that instance.
 */
fun BindableDoubleProperty.beforeSet(action: (old: Double, new: Double) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableDoubleProperty.validate] of a [BindableDoubleProperty] instance to a given function and
 * returns that instance.
 */
fun BindableDoubleProperty.validate(action: (old: Double, new: Double) -> Double) = apply { validate = action }

/**
 * Sets [BindableDoubleProperty.afterSet] of a [BindableDoubleProperty] instance to a given function and
 * returns that instance.
 */
fun BindableDoubleProperty.afterSet(action: (new: Double) -> Unit) = apply { afterSet = action }

