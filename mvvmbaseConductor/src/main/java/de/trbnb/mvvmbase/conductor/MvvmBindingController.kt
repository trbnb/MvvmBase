package de.trbnb.mvvmbase.conductor

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.savedstate.SavedStateRegistryController
import com.bluelinelabs.conductor.archlifecycle.LifecycleController
import de.trbnb.mvvmbase.MvvmView
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.ViewModelPropertyChangedCallback
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.utils.findGenericSuperclass

/**
 * Reference implementation of an [MvvmView] with [com.bluelinelabs.conductor.Controller].
 *
 * This creates the binding in [onCreateView] and the ViewModel lazily.
*/
abstract class MvvmBindingController<VM, B>(
    bundle: Bundle? = null,
    @LayoutRes override val layoutId: Int = 0
) : LifecycleController(bundle), MvvmView<VM, B> where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {
    override var binding: B? = null

    /**
     * Serves the same purpose as [ComponentActivity.getDefaultViewModelProviderFactory].
     */
    open val defaultViewModelProviderFactory: ViewModelProvider.Factory by lazy {
        SavedStateViewModelFactory(
            activity?.application ?: throw RuntimeException("Unable to retrieve application context"),
            this,
            defaultViewModelArgs
        )
    }

    /**
     * Callback implementation that delegates the parametes to [onViewModelPropertyChanged].
     */
    @Suppress("LeakingThis")
    private val viewModelObserver = ViewModelPropertyChangedCallback(this)

    @Suppress("LeakingThis")
    override val viewModelDelegate: Lazy<VM> = ViewModelLazy(
        viewModelClass = viewModelClass.kotlin,
        storeProducer = { viewModelStore },
        factoryProducer = { defaultViewModelProviderFactory }
    )

    /**
     * Is called when the ViewModel sends an [Event].
     * Will only call [onEvent].
     *
     * @see onEvent
     */
    private val eventListener = { event: Event -> onEvent(event) }

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<MvvmBindingController<VM, B>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")

    /**
     * Defines which Bundle will be used as defaultArgs with [androidx.lifecycle.AbstractSavedStateViewModelFactory].
     * Default is [getArgs].
     */
    protected open val defaultViewModelArgs: Bundle?
        get() = args

    override val dataBindingComponent: DataBindingComponent?
        get() = null

    private val viewModelStore = ViewModelStore()

    @Suppress("LeakingThis")
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    /**
     * Necessary memory if [onRestoreInstanceState] has been invoked.
     * If not it means [SavedStateRegistryController.performRestore] has to be invoked with `null` manually.
     *
     * @see onContextAvailable
     * @see onRestoreInstanceState
     */
    private var onRestoreInstanceStateCalled = false

    override fun getViewModelStore() = viewModelStore

    override fun getSavedStateRegistry() = savedStateRegistryController.savedStateRegistry

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        if (lifecycle.currentState == Lifecycle.State.INITIALIZED && !onRestoreInstanceStateCalled) {
            savedStateRegistryController.performRestore(null)
        }
        onRestoreInstanceStateCalled = false
    }

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
            onBindingCreated(binding)
            onViewModelLoaded(viewModel)
        }.root
    }

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

    protected open fun onBindingCreated(binding: B) { }

    @CallSuper
    override fun onViewModelLoaded(viewModel: VM) {
        viewModel.addOnPropertyChangedCallback(viewModelObserver)
        viewModel.eventChannel.addListener(eventListener)
    }

    @Suppress("EmptyFunctionBlock")
    override fun onViewModelPropertyChanged(viewModel: VM, fieldId: Int) { }

    @Suppress("EmptyFunctionBlock")
    override fun onEvent(event: Event) { }

    override fun onDestroyView(view: View) {
        super.onDestroyView(view)

        binding?.setVariable(viewModelBindingId, null)
        viewModel.onUnbind()
        viewModel.eventChannel.removeListener(eventListener)
        viewModel.removeOnPropertyChangedCallback(viewModelObserver)

        binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        savedStateRegistryController.performSave(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedStateRegistryController.performRestore(savedInstanceState)
        onRestoreInstanceStateCalled = true
    }
}
