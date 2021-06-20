package de.trbnb.mvvmbase.databinding.viewmodel

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import de.trbnb.mvvmbase.databinding.MvvmBindingActivity
import de.trbnb.mvvmbase.databinding.MvvmBindingFragment
import de.trbnb.mvvmbase.databinding.ViewModel
import javax.inject.Provider

/**
 * Convenience function to create a [ViewModelProvider.Factory] for an [MvvmBindingFragment].
 * Can be useful for overriding [MvvmBindingActivity.getDefaultViewModelProviderFactory].
 */
fun <VM> MvvmBindingFragment<VM, *>.viewModelProviderFactory(factory: (handle: SavedStateHandle) -> VM): ViewModelProvider.Factory
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel = object : AbstractSavedStateViewModelFactory(this, arguments) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return factory(handle) as T
        }
    }

/**
 * Convenience function to create a [ViewModelProvider.Factory] for an [MvvmBindingActivity].
 * Can be useful for overriding [MvvmBindingActivity.getDefaultViewModelProviderFactory].
 */
fun <VM> MvvmBindingActivity<VM, *>.viewModelProviderFactory(factory: (handle: SavedStateHandle) -> VM): ViewModelProvider.Factory
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel = object : AbstractSavedStateViewModelFactory(this, intent?.extras) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
            return factory(handle) as T
        }
    }

fun <VM> viewModelProviderFactory(factory: () -> VM): ViewModelProvider.Factory
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : androidx.lifecycle.ViewModel?> create(modelClass: Class<T>): T = factory() as T
}

/**
 * Converts a [Provider] to a [ViewModelProvider.Factory].
 * Useful if a [Provider] for a [ViewModel] is injected and is intended to be used for overriding getDefaultViewModelProviderFactory.
 */
fun <VM> Provider<VM>.asViewModelProviderFactory(): ViewModelProvider.Factory
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel = viewModelProviderFactory(::get)
