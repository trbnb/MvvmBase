package de.trbnb.mvvmbase.conductor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import de.trbnb.mvvmbase.BR
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.utils.findGenericSuperclass
import javax.inject.Provider

/**
 * Base class for Controllers that serve as view within an MVVM structure.
 *
 * It automatically creates the binding and sets the view model as that bindings parameter.
 * Note that the parameter has to have to name "vm".
 *
 * The view model will be kept in memory with the Architecture Components.
 * The view model will be instantiated via the [viewModelProvider].
 * This [Provider] can either be implemented manually or injected with DI.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Controller.
 * @param[B] The type of the specific [ViewDataBinding] implementation for this Controller.
 */
abstract class MvvmBindingController<VM, B> : LifecycleController, ViewModelStoreOwner
        where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {

    constructor() : super()
    constructor(bundle: Bundle? = null) : super(bundle)

    /**
     * The [ViewDataBinding] implementation for a specific layout.
     * Will only be set in [onCreateView].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected var binding: B? = null
        private set

    /**
     * The [ViewModel] that is used for data binding.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected val viewModel: VM by lazy { ViewModelProvider(this, viewModelFactory)[viewModelClass] }

    /**
     * Gets the class of the view model that an implementation uses.
     */
    protected open val viewModelClass: Class<VM>
        @Suppress("UNCHECKED_CAST")
        get() {
            val superClass = findGenericSuperclass<MvvmBindingController<VM, B>>() ?: throw IllegalStateException()
            return superClass.actualTypeArguments[0] as Class<VM>
        }

    /**
     * Creates a new view model via [viewModelProvider].
     */
    private val viewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return viewModelProvider.get() as T
        }
    }

    /**
     * The [de.trbnb.mvvmbase.BR] value that is used as parameter for the view model in the binding.
     * Is always [de.trbnb.mvvmbase.BR.vm].
     */
    private val viewModelBindingId: Int
        get() = BR.vm

    /**
     * The layout resource ID for this Fragment.
     * Is used in [onCreateView] to create the [ViewDataBinding].
     */
    @get:LayoutRes
    protected abstract val layoutId: Int

    /**
     * The [Provider] implementation that is used if a new view model has to be instantiated.
     */
    abstract val viewModelProvider: Provider<VM>

    /**
     * Callback implementation that delegates the parametes to [onViewModelPropertyChanged].
     */
    private val viewModelObserver = object : Observable.OnPropertyChangedCallback() {
        @Suppress("UNCHECKED_CAST")
        override fun onPropertyChanged(sender: Observable, fieldId: Int) {
            onViewModelPropertyChanged(sender as VM, fieldId)
        }
    }

    /**
     * Is called when the ViewModel sends an [Event].
     * Will only call [onEvent].
     *
     * @see onEvent
     */
    private val eventListener = { event: Event ->
        onEvent(event)
    }

    private val viewModelStore = ViewModelStore()

    override fun getViewModelStore() = viewModelStore

    /**
     * Called by the lifecycle.
     *
     * Creates the [ViewDataBinding].
     */
    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return initBinding(inflater, container).also { binding ->
            this.binding = binding

            binding.lifecycleOwner = this
            binding.setVariable(viewModelBindingId, viewModel)
            viewModel.onBind()
            onViewModelLoaded(viewModel)

            onBindingCreated(binding)
        }.root
    }

    /**
     * Creates a new [ViewDataBinding].
     *
     * @return The new [ViewDataBinding] instance that fits this Fragment.
     */
    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?): B {
        return DataBindingUtil.inflate(inflater, layoutId, container, false)
    }

    protected open fun onBindingCreated(binding: B) { }

    /**
     * Called when the view model is loaded and is set as [viewModel].
     *
     * @param[viewModel] The [ViewModel] instance that was loaded.
     */
    @CallSuper
    protected open fun onViewModelLoaded(viewModel: VM) {
        viewModel.addOnPropertyChangedCallback(viewModelObserver)
        viewModel.eventChannel.addListener(eventListener)
    }

    /**
     * Called when the view model notifies listeners that a property has changed.
     *
     * @param[viewModel] The [ViewModel] instance whose property has changed.
     * @param[fieldId] The ID of the field in the BR file that indicates which property in the view model has changed.
     */
    protected open fun onViewModelPropertyChanged(viewModel: VM, fieldId: Int) { }

    /**
     * Is called when the ViewModel sends an [Event].
     */
    @CallSuper
    protected open fun onEvent(event: Event) { }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)

        binding?.setVariable(viewModelBindingId, null)
        viewModel.onUnbind()
        viewModel.eventChannel.removeListener(eventListener)
        viewModel.removeOnPropertyChangedCallback(viewModelObserver)

        binding = null
    }
}
