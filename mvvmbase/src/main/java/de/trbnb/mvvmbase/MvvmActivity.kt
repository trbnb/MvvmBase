package de.trbnb.mvvmbase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelLazy
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.events.addListener
import de.trbnb.mvvmbase.utils.findGenericSuperclass

/**
 * Reference implementation of an [MvvmView] with [android.app.Activity].
 *
 * This creates the binding and the ViewModel during [onCreate].
 */
abstract class MvvmActivity<VM> : AppCompatActivity(), MvvmView<VM>
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel {
    /**
     * Is called when the ViewModel sends an [Event].
     * Will only call [onEvent].
     *
     * @see onEvent
     */
    private val eventListener: (Event) -> Unit = { event ->
        runOnUiThread { onEvent(event) }
    }

    @Suppress("LeakingThis")
    override val viewModelDelegate: Lazy<VM> = ViewModelLazy(
        viewModelClass = viewModelClass.kotlin,
        storeProducer = { viewModelStore },
        factoryProducer = { defaultViewModelProviderFactory }
    )

    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<MvvmActivity<VM>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")

    /**
     * Calls [onViewModelLoaded]. This happens here and not in [onCreate] so that initializations can finish before event callbacks like
     * [onEvent] and [onViewModelPropertyChanged] are can access those initilaized components.
     */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        viewModel.eventChannel.addListener(this, eventListener)
    }

    @Suppress("EmptyFunctionBlock")
    override fun onEvent(event: Event) { }

}
