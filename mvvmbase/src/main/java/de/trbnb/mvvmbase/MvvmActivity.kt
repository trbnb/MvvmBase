package de.trbnb.mvvmbase

import android.databinding.ViewDataBinding

/**
 * Base class for Activities that don't need to specify the specific [ViewDataBinding] implementation.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Activity.
 */
abstract class MvvmActvity<VM : ViewModel> : MvvmBindingActivity<VM, ViewDataBinding>()