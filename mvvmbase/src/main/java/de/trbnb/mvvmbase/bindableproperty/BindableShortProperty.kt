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
 */
class BindableShortProperty (
    viewModel: ViewModel,
    private var fieldId: Int?,
    defaultValue: Short,
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
    internal var beforeSet: ((old: Short, new: Short) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: ((old: Short, new: Short) -> Short)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: ((new: Short) -> Unit)? = null

    init {
        if (savedStateKey != null) {
            viewModel.onRestore { savedStateHandle ->
                if (savedStateHandle.contains(savedStateKey)) {
                    this.value = savedStateHandle.get(savedStateKey) ?: return@onRestore
                }
            }
        }
    }

    operator fun getValue(thisRef: ViewModel, property: KProperty<*>) = value

    operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: Short) {
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
 * Creates a new [BindableShortProperty] instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 */
fun ViewModel.bindableShort(
    defaultValue: Short = 0,
    savedStateKey: String? = null,
    fieldId: Int? = null
) = BindableShortProperty(this, fieldId, defaultValue, savedStateKey)

/**
 * Sets [BindableShortProperty.beforeSet] of a [BindableShortProperty] instance to a given function and
 * returns that instance.
 */
fun BindableShortProperty.beforeSet(action: (old: Short, new: Short) -> Unit) = apply { beforeSet = action }

/**
 * Sets [BindableShortProperty.validate] of a [BindableShortProperty] instance to a given function and
 * returns that instance.
 */
fun BindableShortProperty.validate(action: (old: Short, new: Short) -> Short) = apply { validate = action }

/**
 * Sets [BindableShortProperty.afterSet] of a [BindableShortProperty] instance to a given function and
 * returns that instance.
 */
fun BindableShortProperty.afterSet(action: (new: Short) -> Unit) = apply { afterSet = action }

