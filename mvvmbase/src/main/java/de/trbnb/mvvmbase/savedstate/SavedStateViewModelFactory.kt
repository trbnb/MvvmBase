package de.trbnb.mvvmbase.savedstate

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

/**
 * [androidx.lifecycle.ViewModelProvider.Factory] implementation that calls the [viewModelProvider] function
 * to initialize a [ViewModel] with [SavedStateHandle].
 */
class SavedStateViewModelFactory<VM : de.trbnb.mvvmbase.ViewModel>(
    private val viewModelProvider: (SavedStateHandle) -> VM,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return viewModelProvider(handle) as T
    }
}
