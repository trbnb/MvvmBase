package de.trbnb.mvvmbase

import de.trbnb.mvvmbase.bindableproperty.StateSaveOption

/**
 * Object for containing library configurations.
 */
object MvvmBase {
    internal var defaultStateSaveOption: StateSaveOption = StateSaveOption.Automatic

    internal var enforceViewModelLifecycleMainThread = true

    /**
     * Sets the default [StateSaveOption] that will be used for bindable properties in [de.trbnb.mvvmbase.savedstate.StateSavingViewModel].
     */
    fun defaultStateSaveOption(stateSaveOption: StateSaveOption) = apply {
        defaultStateSaveOption = stateSaveOption
    }

    /**
     * Starting with Androidx Lifecycle version 2.3.0 all Lifecycles are thread-safe (only usable from main-thread).
     * This can be deactivated for [ViewModel.getLifecycle] to allow for initialization of ViewModels on other threads.
     */
    fun disableViewModelLifecycleThreadConstraints(): MvvmBase = apply {
        enforceViewModelLifecycleMainThread = false
    }
}
