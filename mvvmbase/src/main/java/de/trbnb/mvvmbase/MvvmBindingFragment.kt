package de.trbnb.mvvmbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.savedstate.SavedStateViewModelFactory
import de.trbnb.mvvmbase.utils.findGenericSuperclass

/**
 * Reference implementation of an [MvvmView] with [Fragment].
 *
 * This creates the binding during [onCreateView] and the ViewModel during [onCreate].
 */
abstract class MvvmBindingFragment<VM, B> : Fragment(), MvvmView<VM, B>
        where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {
    override var binding: B? = null

    /**
     * Callback implementation that delegates the parametes to [onViewModelPropertyChanged].
     */
    @Suppress("LeakingThis")
    private val viewModelObserver = ViewModelPropertyChangedCallback(this)

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<MvvmBindingFragment<VM, B>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")

    /**
     * Is called when the ViewModel sends an [Event].
     * Will only call [onEvent].
     *
     * @see onEvent
     */
    private val eventListener = { event: Event -> onEvent(event) }

    /**
     * Defines which Bundle will be used as defaultArgs with [SavedStateViewModelFactory].
     * Default is [getArguments].
     */
    override val defaultViewModelArgs: Bundle?
        get() = arguments

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

        binding?.setVariable(viewModelBindingId, viewModel)
        viewModel.onBind()
        onViewModelLoaded(viewModel)

        binding?.let(this::onBindingCreated)
    }

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

    @CallSuper
    override fun onViewModelLoaded(viewModel: VM) {
        viewModel.addOnPropertyChangedCallback(viewModelObserver)
        viewModel.eventChannel.addListener(eventListener)
    }

    override fun onViewModelPropertyChanged(viewModel: VM, fieldId: Int) { }

    override fun onEvent(event: Event) { }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.setVariable(viewModelBindingId, null)
        viewModel.onUnbind()
        viewModel.eventChannel.removeListener(eventListener)
        viewModel.removeOnPropertyChangedCallback(viewModelObserver)

        binding = null
    }
}

