package de.trbnb.mvvmbase.databinding

import androidx.databinding.ViewDataBinding

/**
 * Typealias for Fragments that don't need to specify the specific [ViewDataBinding] implementation.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Fragment.
 */
public typealias MvvmFragment<VM> = MvvmBindingFragment<VM, ViewDataBinding>

/**
 * Typealias for DialogFragments that don't need to specify the specific [ViewDataBinding] implementation.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Activity.
 */
public typealias MvvmDialogFragment<VM> = MvvmBindingDialogFragment<VM, ViewDataBinding>

/**
 * Typealias for BottomSheetDialogFragments that don't need to specify the specific [ViewDataBinding] implementation.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Activity.
 */
public typealias MvvmBottomSheetDialogFragment<VM> = MvvmBindingBottomSheetDialogFragment<VM, ViewDataBinding>

/**
 * Typealias for Activities that don't need to specify the specific [ViewDataBinding] implementation.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Activity.
 */
public typealias MvvmActivity<VM> = MvvmBindingActivity<VM, ViewDataBinding>
