package de.trbnb.mvvmbase

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import javax.inject.Provider

/**
 * [androidx.lifecycle.ViewModelProvider.Factory] implementation that calls [de.trbnb.mvvmbase.ViewModel.setSavedStateHandle]
 * immediately after initialization.
 */
class SavedStateViewModelFactory<VM : de.trbnb.mvvmbase.ViewModel>(
    private val provider: Provider<VM>,
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return provider.get().apply { setSavedStateHandle(handle) } as T
    }
}
