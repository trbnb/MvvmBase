package de.trbnb.mvvmbase.bindableproperty

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.list.destroyAll
import de.trbnb.mvvmbase.savedstate.StateSavingViewModel
import de.trbnb.mvvmbase.utils.resolveFieldId
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import kotlin.reflect.KProperty

/**
 * Property delegate provider for [BindableProperty].
 * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
 *
 * @see BindableProperty
 */
class ChildViewModelBindablePropertyProvider<C : Collection<ViewModel>>(
    private val fieldId: Int? = null,
    private val defaultValue: C,
    private val stateSaveOption: StateSaveOption
) : BindablePropertyBase.Provider<C>() {
    override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>): BindableProperty<C> = BindableProperty<C>(
        viewModel = thisRef,
        fieldId = fieldId ?: property.resolveFieldId(),
        defaultValue = defaultValue,
        stateSavingKey = stateSaveOption.resolveKey(property),
        distinct = distinct,
        afterSet = afterSet,
        beforeSet = { old, new ->
            old.destroyAll()
            thisRef.apply {
                new.autoDestroy()
                    .bindEvents()
            }
            beforeSet?.invoke(old, new)
        },
        validate = validate
    )
}

/**
 * Creates a new BindableProperty instance.
 *
 * @param defaultValue Value of the property from the start.
 * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
inline fun <reified C : Collection<ViewModel>> ViewModel.childrenBindable(
    defaultValue: C,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption? = null
): ChildViewModelBindablePropertyProvider<C> = ChildViewModelBindablePropertyProvider(fieldId, defaultValue, when (this) {
    is StateSavingViewModel -> when (stateSaveOption) {
        null -> when (savingStateInBindableSupports<C>()) {
            true -> StateSaveOption.Automatic
            false -> StateSaveOption.None
        }
        else -> stateSaveOption
    }
    else -> StateSaveOption.None
})
