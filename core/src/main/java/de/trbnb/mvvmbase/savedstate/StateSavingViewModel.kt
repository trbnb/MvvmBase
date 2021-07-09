package de.trbnb.mvvmbase.savedstate

import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.observableproperty.StateSaveOption

/**
 * Specification for [ViewModel]s that support saving state via [SavedStateHandle].
 */
interface StateSavingViewModel {
    /**
     * Used to store/read values if a new instance has to be created.
     */
    val savedStateHandle: SavedStateHandle

    /**
     * Defines what default [StateSaveOption] will be used for bindable properties.
     */
    val defaultStateSaveOption: StateSaveOption
        get() = MvvmBase.defaultStateSaveOption
}
