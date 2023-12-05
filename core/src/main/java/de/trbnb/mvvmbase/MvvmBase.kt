package de.trbnb.mvvmbase

import de.trbnb.mvvmbase.observableproperty.StateSaveOption

/**
 * Object for containing library configurations.
 */
public object MvvmBase {
    public var defaultStateSaveOption: StateSaveOption = StateSaveOption.Automatic
        private set

    public var enforceViewModelLifecycleMainThread: Boolean = true
        private set

    /**
     * Sets the default [StateSaveOption] that will be used for bindable properties in [de.trbnb.mvvmbase.savedstate.StateSavingViewModel].
     */
    public fun defaultStateSaveOption(stateSaveOption: StateSaveOption): MvvmBase = apply {
        defaultStateSaveOption = stateSaveOption
    }

    /**
     * Starting with Androidx Lifecycle version 2.3.0 all Lifecycles are thread-safe (only usable from main-thread).
     * This can be deactivated for [ViewModel.getLifecycle] to allow for initialization of ViewModels on other threads.
     */
    public fun disableViewModelLifecycleThreadConstraints(): MvvmBase = apply {
        enforceViewModelLifecycleMainThread = false
    }
}
