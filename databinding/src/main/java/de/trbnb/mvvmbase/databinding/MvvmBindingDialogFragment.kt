package de.trbnb.mvvmbase.databinding

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelLazy
import de.trbnb.mvvmbase.databinding.utils.findGenericSuperclass
import de.trbnb.mvvmbase.events.Event

/**
 * Reference implementation of an [MvvmView] with [DialogFragment].
 *
 * This creates the binding during [onCreateView] and the ViewModel during [onCreate].
 */
public abstract class MvvmBindingDialogFragment<VM, B>(@LayoutRes override val layoutId: Int) : DialogFragment(), MvvmView<VM, B>
        where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {
    override var binding: B? = null

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<MvvmBindingDialogFragment<VM, B>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")

    @Suppress("LeakingThis")
    override val viewModelDelegate: Lazy<VM> = ViewModelLazy(
        viewModelClass = viewModelClass.kotlin,
        storeProducer = { viewModelStore },
        factoryProducer = { defaultViewModelProviderFactory },
        extrasProducer = { defaultViewModelCreationExtras }
    )

    private val mainHandler by lazy { Handler(Looper.getMainLooper()) }

    /**
     * Is called when the ViewModel sends an [Event].
     * Will only call [onEvent].
     *
     * @see onEvent
     */
    private val eventListener: (Event) -> Unit = { event ->
        mainHandler.post { onEvent(event) }
    }

    public constructor() : this(0)

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
        binding?.let(this::onBindingCreated)
        onViewModelLoaded(viewModel)
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
        viewModel.eventChannel.addListener(eventListener)
    }

    @Suppress("EmptyFunctionBlock")
    override fun onEvent(event: Event) { }

    override fun onDestroyView() {
        super.onDestroyView()

        binding?.setVariable(viewModelBindingId, null)
        viewModel.eventChannel.removeListener(eventListener)

        binding = null
    }
}
