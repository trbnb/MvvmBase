package de.trbnb.mvvmbase.conductor

import androidx.databinding.ViewDataBinding
import de.trbnb.mvvmbase.ViewModel

/**
 * Typealias for Controllers that don't need to specify the specific [ViewDataBinding] implementation.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Controller.
 */
typealias MvvmController<VM> = MvvmBindingController<VM, ViewDataBinding>
