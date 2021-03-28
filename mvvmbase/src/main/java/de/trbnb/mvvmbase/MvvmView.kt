package de.trbnb.mvvmbase

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryOwner
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.utils.observe
import kotlin.reflect.KProperty0

/**
 * Contract for view components that want to support MVVM with a [ViewModel] bound to a [ViewDataBinding].
 *
 * Implementation should have an instance in [binding] what will bind the [viewModel] with the [viewModelBindingId].
 * Only a layout resource has to be specified via [layoutId] to create the binding.
 * Specifying a [DataBindingComponent] via [dataBindingComponent] is optional.
 *
 * The [ViewModel] will be instantiated via the ViewModel API by Android X.
 */
interface MvvmView<VM> : ViewModelStoreOwner, SavedStateRegistryOwner
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel {
    /**
     * Delegate for [viewModel].
     *
     * Can be overridden to make use of `activityViewModels()` or `navGraphViewModels()`.
     */
    val viewModelDelegate: Lazy<VM>

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
     * Is called when the ViewModel sends an [Event].
     */
    fun onEvent(event: Event) { }

    /**
     * Invokes [action] everytime notifyPropertyChanged is called for the receiver property.
     *
     * @param invokeImmediately If true [action] will be invoked immediately and not wait for the first notifyPropertyChanged call.
     * @param lifecycleOwner Lifecycle that determines when listening for notifyPropertyChanged stops.
     */
    fun <T> KProperty0<T>.observe(invokeImmediately: Boolean = true, lifecycleOwner: LifecycleOwner = this@MvvmView, action: (T) -> Unit) {
        observe(lifecycleOwner, invokeImmediately, action)
    }
}
