package de.trbnb.mvvmbase.savedstate

import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.BaseViewModel

/**
 * Base implementation for [StateSavingViewModel].
 * Receives the [SavedStateHandle] via construction parameter.
 */
abstract class BaseStateSavingViewModel(
    override val savedStateHandle: SavedStateHandle
) : BaseViewModel(), StateSavingViewModel
