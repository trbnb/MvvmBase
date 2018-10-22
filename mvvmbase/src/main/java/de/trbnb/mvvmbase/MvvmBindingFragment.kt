package de.trbnb.mvvmbase

import android.arch.lifecycle.ViewModelProvider
import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.Observable
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.utils.findGenericSuperclass
import javax.inject.Provider

/**
 * Base class for Frgaments that serve as view within an MVVM structure.
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
abstract class MvvmBindingFragment<VM : BaseViewModel, B : ViewDataBinding> : Fragment() {

    /**
     * The [ViewDataBinding] implementation for a specific layout.
     * Will only be set in [onCreateView].
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected var binding: B? = null
        private set(value) {
            if (value === field) return

            field?.setVariable(viewModelBindingId, null)

            field = value

            value?.setVariable(viewModelBindingId, viewModel)
        }

    /**
     * The [ViewModel] that is used for data binding.
     */
    @Suppress("MemberVisibilityCanBePrivate")
    protected var viewModel: VM? = null
        private set(value) {
            if(field === value) return

            field?.eventChannel?.removeListener(eventListener)
            field?.onUnbind()
            field?.removeOnPropertyChangedCallback(viewModelObserver)

            field = value

            value?.let {
                binding?.setVariable(viewModelBindingId, it)

                onViewModelLoaded(it)
            }
        }

    /**
     * Gets the class of the view model that an implementation uses.
     */
    private val viewModelClass: Class<VM>
        @Suppress("UNCHECKED_CAST")
        get() {
            val superClass = findGenericSuperclass<MvvmBindingFragment<VM, B>>()?: throw IllegalStateException()
            return superClass.actualTypeArguments[0] as Class<VM>
        }

    /**
     * Creates a new view model via [viewModelProvider].
     */
    private val viewModelFactory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : android.arch.lifecycle.ViewModel> create(modelClass: Class<T>): T {
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
    private val eventListener = ::onEvent

    /**
     * Gets the view model with the Architecture Components.
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)

        viewModel = ViewModelProvider(viewModelStore, viewModelFactory)[viewModelClass]
    }

    /**
     * Called by the lifecycle.
     *
     * Creates the [ViewDataBinding].
     */
    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return initBinding(inflater, container).also {
            binding = it
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

    /**
     * Called when the view model is loaded and is set as [viewModel].
     *
     * @param[viewModel] The [ViewModel] instance that was loaded.
     */
    @CallSuper
    protected open fun onViewModelLoaded(viewModel: VM) {
        viewModel.addOnPropertyChangedCallback(viewModelObserver)
        viewModel.onBind()
        viewModel.eventChannel.addListener(::onEvent)
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

        binding = null
    }

    override fun onDetach() {
        super.onDetach()

        viewModel = null
    }
}

