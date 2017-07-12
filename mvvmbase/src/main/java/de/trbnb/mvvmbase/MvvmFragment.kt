package de.trbnb.mvvmbase

import android.databinding.ViewDataBinding

/**
 * Base class for Fragments that don't need to specify the specific [ViewDataBinding] implementation.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Fragment.
 */
abstract class MvvmFragment<VM : ViewModel> : MvvmBindingFragment<VM, ViewDataBinding>()