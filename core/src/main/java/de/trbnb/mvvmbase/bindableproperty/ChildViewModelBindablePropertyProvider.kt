package de.trbnb.mvvmbase.bindableproperty

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.list.destroyAll
import de.trbnb.mvvmbase.savedstate.StateSavingViewModel
import de.trbnb.mvvmbase.utils.savingStateInBindableSupports
import kotlin.reflect.KProperty

/**
 * Property delegate provider for [BindableProperty].
 * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
 *
 * @see BindableProperty
 */
class ChildViewModelBindablePropertyProvider<C : Collection<ViewModel>>(
    private val defaultValue: C,
    private val stateSaveOption: StateSaveOption
) : BindablePropertyBase.Provider<C>() {
    override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>): BindableProperty<C> = BindableProperty(
        viewModel = thisRef,
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
 * @param stateSaveOption Specifies if the state of the property should be saved and with which key.
 */
inline fun <reified C : Collection<ViewModel>> ViewModel.childrenBindable(
    defaultValue: C,
    stateSaveOption: StateSaveOption? = null
): ChildViewModelBindablePropertyProvider<C> = ChildViewModelBindablePropertyProvider(defaultValue, when (this) {
    is StateSavingViewModel -> when (stateSaveOption) {
        null -> when (savingStateInBindableSupports<C>()) {
            true -> defaultStateSaveOption
            false -> StateSaveOption.None
        }
        else -> stateSaveOption
    }
    else -> StateSaveOption.None
})
