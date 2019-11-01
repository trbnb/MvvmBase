package de.trbnb.mvvmbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.utils.findGenericSuperclass
import javax.inject.Provider

/**
 * Base class for Fragments that serve as view within an MVVM structure.
 *
 * It automatically creates the binding and sets the view model as that bindings parameter.
 * Note that the parameter has to have to name "vm".
 *
 * The view model will be kept in memory with the Architecture Components. If a Fragment is created
 * for the first time the view model will be instantiated via the [viewModelProvider].
 * This [Provider] can either be implemented manually or injected with DI.
 *
 * @param[VM] The type of the specific [ViewModel] implementation for this Fragment.
 * @param[B] The type of the specific [ViewDataBinding] implementation for this Fragment.
 */
abstract class MvvmBindingFragment<VM, B> : Fragment()
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {
    /**
     * The [ViewDataBinding] implementation for a specific layout.
     * Will only be set in [onCreateView].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected var binding: B? = null

    /**
     * The [ViewModel] that is used for data binding.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected var viewModel: VM? = null
        private set

    /**
     * Gets the class of the view model that an implementation uses.
     */
    protected open val viewModelClass: Class<VM>
        @Suppress("UNCHECKED_CAST")
        get() {
            val superClass = findGenericSuperclass<MvvmBindingFragment<VM, B>>() ?: throw IllegalStateException()
            return superClass.actualTypeArguments[0] as Class<VM>
        }

    /**
     * Creates a new view model via [viewModelProvider].
     */
    private val viewModelFactory: ViewModelProvider.Factory
        get() = SavedStateViewModelFactory(viewModelProvider, this, defaultViewModelArgs)

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
    private val viewModelObserver = object : Observable.OnPropertyChangedCallback(){
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

    /**
     * Defines which [DataBindingComponent] will be used with [DataBindingUtil.inflate].
     * Default is `null` and will lead to usage of [DataBindingUtil.getDefaultComponent].
     */
    protected open val dataBindingComponent: DataBindingComponent?
        get() = null

    /**
     * Defines which Bundle will be used as defaultArgs with [SavedStateViewModelFactory].
     * Default is [getArguments].
     */
    protected open val defaultViewModelArgs: Bundle?
        get() = arguments

    /**
     * Gets the view model with the Architecture Components.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[viewModelClass]
    }

    /**
     * Called by the lifecycle.
     *
     * Creates the [ViewDataBinding].
     */
    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return initBinding(inflater, container).also {
            it.lifecycleOwner = viewLifecycleOwner
            binding = it
        }.root
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel?.let { viewModel ->
            binding?.setVariable(viewModelBindingId, viewModel)
            viewModel.onBind()
            onViewModelLoaded(viewModel)
        }

        binding?.let(this::onBindingCreated)
    }

    /**
     * Called after [onViewCreated]. Passes the view of the Fragment contained in the [binding].
     */
    protected open fun onBindingCreated(binding: B) { }

    /**
     * Creates a new [ViewDataBinding].
     *
     * @return The new [ViewDataBinding] instance that fits this Fragment.
     */
    private fun initBinding(inflater: LayoutInflater, container: ViewGroup?): B {
        return when (val dataBindingComponent = dataBindingComponent) {
            null -> DataBindingUtil.inflate(inflater, layoutId, container, false)
            else -> DataBindingUtil.inflate(inflater, layoutId, container, false, dataBindingComponent)
        }
    }

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
    protected open fun onEvent(event: Event) { }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.setVariable(viewModelBindingId, null)
        viewModel?.onUnbind()
        viewModel?.eventChannel?.removeListener(eventListener)
        viewModel?.removeOnPropertyChangedCallback(viewModelObserver)

        binding = null
    }

    override fun onDetach() {
        super.onDetach()

        viewModel = null
    }
}

