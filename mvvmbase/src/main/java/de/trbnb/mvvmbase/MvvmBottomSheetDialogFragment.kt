package de.trbnb.mvvmbase

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.ViewModelLazy
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.trbnb.mvvmbase.events.Event
import de.trbnb.mvvmbase.events.addListener
import de.trbnb.mvvmbase.utils.findGenericSuperclass

/**
 * Reference implementation of an [MvvmView] with [BottomSheetDialogFragment].
 *
 * This creates the binding during [onCreateView] and the ViewModel during [onCreate].
 */
abstract class MvvmBottomSheetDialogFragment<VM> : BottomSheetDialogFragment(), MvvmView<VM>
    where VM : ViewModel, VM : androidx.lifecycle.ViewModel {
    @Suppress("UNCHECKED_CAST")
    override val viewModelClass: Class<VM>
        get() = findGenericSuperclass<MvvmBottomSheetDialogFragment<VM>>()
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
