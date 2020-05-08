package de.trbnb.mvvmbase

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.savedstate.SavedStateViewModelFactory

/**
 * Contract for view components that want to support MVVM with a [ViewModel] bound to a [ViewDataBinding].
 *
 * Implementation should have an instance in [binding] what will bind the [viewModel] with the [viewModelBindingId].
 * Only a layout resource has to be specified via [layoutId] to create the binding.
 * Specifying a [DataBindingComponent] via [dataBindingComponent] is optional.
 *
 * The [ViewModel] will be instantiated via the ViewModel API by Android X.
 * For this the function [createViewModel] will be called and has to be implemented.
 * A [SavedStateHandle] will be passed to it to support saving state.
 */
interface MvvmView<VM, B : ViewDataBinding> : ViewModelStoreOwner, SavedStateRegistryOwner
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel {
    /**
     * The [ViewDataBinding] implementation for a specific layout.
     * Nullable due to possible lifecycle circumstances.
     */
    val binding: B?

    /**
     * Delegate for [viewModel].
     *
     * Can be overridden to make use of `activityViewModels()` or `navGraphViewModels()`.
     * These should then make use of [viewModelFactory].
     */
    val viewModelDelegate: Lazy<VM>
        get() = ViewModelLazy(
            viewModelClass = viewModelClass.kotlin,
            storeProducer = { viewModelStore },
            factoryProducer = { viewModelFactory }
        )

    /**
     * The [ViewModel] that is used for data binding.
     *
     * @see viewModelDelegate
     */
    val viewModel: VM
        get() = viewModelDelegate.value

    /**
     * Gets the class of the view model that an implementation uses.
     */
    val viewModelClass: Class<VM>

    /**
     * The [de.trbnb.mvvmbase.BR] value that is used as parameter for the view model in the binding.
     * Is always [de.trbnb.mvvmbase.BR.vm].
     */
    val viewModelBindingId: Int
        get() = BR.vm

    /**
     * The layout resource ID for this Activity.
     * Is used to create the [ViewDataBinding].
     */
    @get:LayoutRes
    val layoutId: Int

    /**
     * Defines which [DataBindingComponent] will be used with [DataBindingUtil.inflate].
     * Default is `null` and will lead to usage of [DataBindingUtil.getDefaultComponent].
     */
    val dataBindingComponent: DataBindingComponent?
        get() = null

    /**
     * Defines which Bundle will be used as defaultArgs with [SavedStateViewModelFactory].
     */
    val defaultViewModelArgs: Bundle?

    /**
     * Creates a new view model via [createViewModel].
     *
     * @see SavedStateViewModelFactory
     */
    val viewModelFactory: ViewModelProvider.Factory
        get() = SavedStateViewModelFactory(this::createViewModel, this, defaultViewModelArgs)

    /**
     * Is called to create a need new ViewModel instance.
     *
     * @param savedStateHandle Needed for instances of a [de.trbnb.mvvmbase.savedstate.StateSavingViewModel].
     */
    fun createViewModel(savedStateHandle: SavedStateHandle): VM

    /**
     * Called when the view model is loaded and is set as [viewModel].
     *
     * @param[viewModel] The [ViewModel] instance that was loaded.
     */
    fun onViewModelLoaded(viewModel: VM)

    /**
     * Called when the view model notifies listeners that a property has changed.
     *
     * @param[viewModel] The [ViewModel] instance whose property has changed.
     * @param[fieldId] The ID of the field in the BR file that indicates which property in the view model has changed.
     */
    fun onViewModelPropertyChanged(viewModel: VM, fieldId: Int) { }

    /**
     * Is called when the ViewModel sends an [Event].
     */
    fun onEvent(event: Event) { }
}
