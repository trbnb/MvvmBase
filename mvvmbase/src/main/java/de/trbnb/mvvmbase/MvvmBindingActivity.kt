package de.trbnb.mvvmbase

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelLazy
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.events.addListener
import de.trbnb.mvvmbase.savedstate.SavedStateViewModelFactory
import de.trbnb.mvvmbase.utils.findGenericSuperclass

/**
 * Reference implementation of an [MvvmView] with [android.app.Activity].
 *
 * This creates the binding and the ViewModel during [onCreate].
 */
abstract class MvvmBindingActivity<VM, B> : AppCompatActivity(), MvvmView<VM, B>
        where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {
    override lateinit var binding: B

    /**
     * Is called when the ViewModel sends an [Event].
     * Will only call [onEvent].
     *
     * @see onEvent
     */
    private val eventListener = { event: Event -> onEvent(event) }

    @Suppress("LeakingThis")
    override val viewModelDelegate: Lazy<VM> = ViewModelLazy(
        viewModelClass = viewModelClass.kotlin,
        storeProducer = { viewModelStore },
        factoryProducer = { viewModelFactory }
    )

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<MvvmBindingActivity<VM, B>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")

    /**
     * Callback implementation that delegates the parametes to [onViewModelPropertyChanged].
     */
    @Suppress("LeakingThis")
    private val viewModelObserver = ViewModelPropertyChangedCallback(this)

    /**
     * Defines which Bundle will be used as defaultArgs with [SavedStateViewModelFactory].
     * Default is [android.content.Intent.getExtras] from [getIntent].
     */
    override val defaultViewModelArgs: Bundle?
        get() = intent.extras

    /**
     * Called by the lifecycle.
     * Creates the [ViewDataBinding] and loads the view model.
     */
    @Suppress("UNCHECKED_CAST")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initBinding()
    }

    /**
     * Calls [onViewModelLoaded]. This happens here and not in [onCreate] so that initializations can finish before event callbacks like
     * [onEvent] and [onViewModelPropertyChanged] are can access those initilaized components.
     */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        onViewModelLoaded(viewModel)
    }

    /**
     * Creates the [ViewDataBinding].
     *
     * @return The new [ViewDataBinding] instance that fits this Activity.
     */
    private fun initBinding(): B {
        val binding: B = when (val dataBindingComponent = dataBindingComponent) {
            null -> DataBindingUtil.setContentView(this, layoutId)
            else -> DataBindingUtil.setContentView(this, layoutId, dataBindingComponent)
        }
        return binding.apply {
            lifecycleOwner = this@MvvmBindingActivity
            setVariable(viewModelBindingId, viewModel)
            viewModel.onBind()
        }
    }

    @CallSuper
    override fun onViewModelLoaded(viewModel: VM) {
        viewModel.addOnPropertyChangedCallback(viewModelObserver)
        viewModel.eventChannel.addListener(this, eventListener)
    }

    override fun onViewModelPropertyChanged(viewModel: VM, fieldId: Int) { }

    override fun onEvent(event: Event) { }

    /**
     * Called by the lifecycle.
     * Removes the view model callback.
     * If the Activity is finishing the view model is notified.
     */
    @CallSuper
    override fun onDestroy() {
        super.onDestroy()

        viewModel.onUnbind()
        viewModel.removeOnPropertyChangedCallback(viewModelObserver)
    }
}

