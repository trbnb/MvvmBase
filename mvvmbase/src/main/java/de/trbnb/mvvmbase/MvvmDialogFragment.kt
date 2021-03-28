package de.trbnb.mvvmbase

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelLazy
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.events.addListener
import de.trbnb.mvvmbase.utils.findGenericSuperclass

/**
 * Reference implementation of an [MvvmView] with [DialogFragment].
 *
 * This creates the binding during [onCreateView] and the ViewModel during [onCreate].
 */
abstract class MvvmDialogFragment<VM> : DialogFragment(), MvvmView<VM>
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel {
    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<MvvmDialogFragment<VM>>()
            ?.actualTypeArguments
            ?.firstOrNull() as? Class<VM>
            ?: throw IllegalStateException("viewModelClass does not equal Class<VM>")

    @Suppress("LeakingThis")
    override val viewModelDelegate: Lazy<VM> = ViewModelLazy(
        viewModelClass = viewModelClass.kotlin,
        storeProducer = { viewModelStore },
        factoryProducer = { defaultViewModelProviderFactory }
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.eventChannel.addListener(viewLifecycleOwner, eventListener)
    }

    @Suppress("EmptyFunctionBlock")
    override fun onEvent(event: Event) { }
}
