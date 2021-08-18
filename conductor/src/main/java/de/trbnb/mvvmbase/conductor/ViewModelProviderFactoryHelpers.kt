package de.trbnb.mvvmbase.conductor

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import de.trbnb.mvvmbase.databinding.ViewModel

/**
 * Convenience function to create a [ViewModelProvider.Factory] for an [de.trbnb.mvvmbase.databinding.MvvmBindingActivity].
 * Can be useful for overriding [MvvmBindingController.getDefaultViewModelProviderFactory].
 */
fun <VM> MvvmBindingController<VM, *>.viewModelProviderFactory(factory: (handle: SavedStateHandle) -> VM): ViewModelProvider.Factory
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel = object : AbstractSavedStateViewModelFactory(this, args) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        return factory(handle) as T
    }
}
