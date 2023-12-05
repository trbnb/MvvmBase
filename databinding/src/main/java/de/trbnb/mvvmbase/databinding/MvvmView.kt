package de.trbnb.mvvmbase.databinding

import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import de.trbnb.mvvmbase.events.Event

/**
 * Contract for view components that want to support MVVM with a [ViewModel] bound to a [ViewDataBinding].
 *
 * Implementation should have an instance in [binding] what will bind the [viewModel] with the [viewModelBindingId].
 * Only a layout resource has to be specified via [layoutId] to create the binding.
 * Specifying a [DataBindingComponent] via [dataBindingComponent] is optional.
 *
 * The [ViewModel] will be instantiated via the ViewModel API by Android X.
 */
public interface MvvmView<VM, B : ViewDataBinding> : ViewModelStoreOwner, SavedStateRegistryOwner
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel {
    /**
     * The [ViewDataBinding] implementation for a specific layout.
     * Nullable due to possible lifecycle circumstances.
     */
    public val binding: B?

    /**
     * Delegate for [viewModel].
     *
     * Can be overridden to make use of `activityViewModels()` or `navGraphViewModels()`.
     */
    public val viewModelDelegate: Lazy<VM>

    /**
     * The [ViewModel] that is used for data binding.
     *
     * @see viewModelDelegate
     */
    public val viewModel: VM
        get() = viewModelDelegate.value

    /**
     * Gets the class of the view model that an implementation uses.
     */
    public val viewModelClass: Class<VM>

    /**
     * The [de.trbnb.mvvmbase.BR] value that is used as parameter for the view model in the binding.
     * Is always [de.trbnb.mvvmbase.BR.vm].
     */
    public val viewModelBindingId: Int
        get() = BR.vm

    /**
     * The layout resource ID for this Activity.
     * Is used to create the [ViewDataBinding].
     */
    @get:LayoutRes
    public val layoutId: Int

    /**
     * Defines which [DataBindingComponent] will be used with [DataBindingUtil.inflate].
     * Default is `null` and will lead to usage of [DataBindingUtil.getDefaultComponent].
     */
    public val dataBindingComponent: DataBindingComponent?
        get() = null

    /**
     * Called when the view model is loaded and is set as [viewModel].
     *
     * @param[viewModel] The [ViewModel] instance that was loaded.
     */
    public fun onViewModelLoaded(viewModel: VM)

    /**
     * Called when the view model notifies listeners that a property has changed.
     *
     * @param[viewModel] The [ViewModel] instance whose property has changed.
     * @param[fieldId] The ID of the field in the BR file that indicates which property in the view model has changed.
     */
    @CallSuper
    @Deprecated("Use KProperty.observe() instead", level = DeprecationLevel.ERROR)
    public fun onViewModelPropertyChanged(viewModel: VM, fieldId: Int) { }

    /**
     * Is called when the ViewModel sends an [Event].
     */
    public fun onEvent(event: Event) { }
}
