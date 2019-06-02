package de.trbnb.mvvmbase

import androidx.databinding.ViewDataBinding

/**
 * Typealias for Activities that don't need to specify the specific [ViewDataBinding] implementation.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Activity.
 */
typealias MvvmActivity<VM> = MvvmBindingActivity<VM, ViewDataBinding>
