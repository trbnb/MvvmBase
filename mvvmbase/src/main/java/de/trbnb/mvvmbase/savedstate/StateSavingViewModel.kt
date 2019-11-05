package de.trbnb.mvvmbase.savedstate

import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.ViewModel

/**
 * Specification for [ViewModel]s that support saving state via [SavedStateHandle].
 */
interface StateSavingViewModel : ViewModel {
    /**
     * Used to store/read values if a new instance has to be created.
     */
    val savedStateHandle: SavedStateHandle
}
