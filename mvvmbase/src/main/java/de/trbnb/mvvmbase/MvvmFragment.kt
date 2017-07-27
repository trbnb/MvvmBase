package de.trbnb.mvvmbase

import android.databinding.ViewDataBinding

/**
 * Typealias for Fragments that don't need to specify the specific [ViewDataBinding] implementation.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Fragment.
 */
typealias MvvmFragment<VM> = MvvmBindingFragment<VM, ViewDataBinding>