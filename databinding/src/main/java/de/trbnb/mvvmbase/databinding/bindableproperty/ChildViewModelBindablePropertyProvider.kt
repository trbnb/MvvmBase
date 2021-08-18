package de.trbnb.mvvmbase.databinding.bindableproperty

import de.trbnb.mvvmbase.databinding.ViewModel
import de.trbnb.mvvmbase.observableproperty.StateSaveOption

/**
 * Helper function to migrate from `childrenBindable`.
 *
 * @see ViewModel.asChildren
 */
@Deprecated("Use asChildren() instead.", ReplaceWith("bindable(defaultValue, fieldId, stateSaveOption).asChildren()"))
inline fun <reified C : Collection<ViewModel>> ViewModel.childrenBindable(
    defaultValue: C,
    fieldId: Int? = null,
    stateSaveOption: StateSaveOption? = null
): BindableProperty.Provider<C> = bindable(defaultValue, fieldId, stateSaveOption).asChildren()
