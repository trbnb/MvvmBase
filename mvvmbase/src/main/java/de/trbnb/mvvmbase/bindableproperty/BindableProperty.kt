package de.trbnb.mvvmbase.bindableproperty

import android.os.Build
import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.savedstate.StateSavingViewModel
import de.trbnb.mvvmbase.utils.resolveFieldId
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Delegate property that invokes [BaseObservable.notifyPropertyChanged] and saves state
 * via [StateSavingViewModel.savedStateHandle].
 *
 * @param T Type of the stored value.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param defaultValue Value that will be used at start.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
class BindableProperty<T>(
    viewModel: ViewModel,
    private var fieldId: Int?,
    defaultValue: T,
    private val stateSaveOption: StateSaveOption
) : BindablePropertyBase(), ReadWriteProperty<ViewModel, T> {

    companion object {
        /**
         * Initializes the automatic field ID detection by providing the class inside BR.java.
         */
        @Deprecated(
            message = "Use MvvmBase.init() instead",
            replaceWith = ReplaceWith("MvvmBase.init(brClass)", "de.trbnb.mvvmbase.MvvmBase"),
            level = DeprecationLevel.WARNING
        )
        fun init(brClass: Class<*>) {
            MvvmBase.init(brClass)
        }

        /**
         * Initializes the automatic field ID detection by providing the class inside BR.java.
         */
        @Deprecated(
            message = "Use MvvmBase.init() instead",
            replaceWith = ReplaceWith("MvvmBase.init<BR>()", "de.trbnb.mvvmbase.MvvmBase"),
            level = DeprecationLevel.WARNING
        )
        inline fun <reified BR> init() = MvvmBase.init<BR>()
    }

    /**
     * Gets or sets the stored value.
     */
    @Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST")
    private var value: T = when {
        stateSaveOption is StateSaveOption.Manual && viewModel is StateSavingViewModel && stateSaveOption.key in viewModel.savedStateHandle -> {
            viewModel.savedStateHandle.get<T>(stateSaveOption.key) as T
        }
        else -> defaultValue
    }

    /**
     * The key that will be used to save the state of the property.
     */
    private var stateSavingKey: String? = (stateSaveOption as? StateSaveOption.Manual)?.key

    /**
     * Gets or sets a function that will be invoked if a new value is about to be set.
     * The first parameter is the old value and the second parameter is the new value.
     *
     * This function will not be invoked if [BindableProperty.distinct] is true and the new value
     * is equal to the old value.
     */
    internal var beforeSet: ((old: T, new: T) -> Unit)? = null

    /**
     * Gets or sets a function that will validate a newly set value.
     * The first parameter is the old value and the second parameter is the new value.
     * The returned value will be the new stored value.
     *
     * If this function is null validation will not happen and the new value will simply be set.
     */
    internal var validate: ((old: T, new: T) -> T)? = null

    /**
     * Gets or sets a function that will be invoked if a new value was set and
     * [BaseObservable.notifyPropertyChanged] was invoked.
     * The first parameter is the old value and the second parameter is the new value.
     */
    internal var afterSet: ((new: T) -> Unit)? = null

    override operator fun getValue(thisRef: ViewModel, property: KProperty<*>): T {
        detectStateSavingKey(thisRef, property)
        return value
    }

    override fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
        detectStateSavingKey(thisRef, property)

        if (fieldId == null) {
            fieldId = property.resolveFieldId()
        }

        if (distinct && this.value == value) {
            return
        }

        beforeSet?.invoke(this.value, value)
        this.value = validate?.invoke(this.value, value) ?: value
        thisRef.notifyPropertyChanged(fieldId ?: BR._all)
        if (thisRef is StateSavingViewModel) {
            stateSavingKey?.let { thisRef.savedStateHandle[it] = this.value }
        }
        afterSet?.invoke(this.value)
    }

    @Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST")
    private fun detectStateSavingKey(thisRef: ViewModel, property: KProperty<*>) {
        if (stateSaveOption is StateSaveOption.Automatic && stateSavingKey == null && thisRef is StateSavingViewModel) {
            val newStateSavingKey = property.name.also { this.stateSavingKey = it }
            if (newStateSavingKey in thisRef.savedStateHandle) {
                this.value = thisRef.savedStateHandle.get<T>(newStateSavingKey) as T
            }
        }
    }
}

/**
 * Creates a new BindableProperty instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
inline fun <reified T> ViewModel.bindable(
    defaultValue: T,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption? = null
) = BindableProperty(this, fieldId, defaultValue, when (this) {
    is StateSavingViewModel -> when (stateSaveOption) {
        null -> when (savingStateInBindableSupports<T>(Build.VERSION.SDK_INT)) {
            true -> StateSaveOption.Automatic
            false -> StateSaveOption.None
        }
        else -> stateSaveOption
    }
    else -> StateSaveOption.None
})

/**
 * Creates a new BindableProperty instance with `null` as default value.
 *
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
inline fun <reified T> ViewModel.bindable(
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption? = null
): BindableProperty<T?> = bindable(null, fieldId, stateSaveOption)

/**
 * Sets [BindableProperty.beforeSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty<T>.beforeSet(action: (old: T, new: T) -> Unit): BindableProperty<T> {
    return apply { beforeSet = action }
}

/**
 * Sets [BindableProperty.validate] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty<T>.validate(action: (old: T, new: T) -> T) = apply { validate = action }

/**
 * Sets [BindableProperty.afterSet] of a [BindableProperty] instance to a given function and
 * returns that instance.
 */
fun <T> BindableProperty<T>.afterSet(action: (new: T) -> Unit) = apply { afterSet = action }
