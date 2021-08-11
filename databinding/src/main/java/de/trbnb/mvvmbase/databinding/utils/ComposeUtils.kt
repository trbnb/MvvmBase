package de.trbnb.mvvmbase.databinding.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.databinding.ViewDataBinding
import de.trbnb.mvvmbase.compose.PropertyMutableState
import de.trbnb.mvvmbase.databinding.BR
import de.trbnb.mvvmbase.databinding.ViewModel
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

/**
 * Observes an observable property as Compose state.
 */
@Composable
fun <T> KProperty0<T>.observeBindableAsState(): State<T> {
    val state = remember { mutableStateOf(get()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = this, key2 = lifecycleOwner) {
        val dispose = observeBindable(lifecycleOwner, false) { state.value = it }
        onDispose(dispose::invoke)
    }
    return state
}

/**
 * Observes an observable property as mutable Compose state.
 */
@Composable
fun <T> KMutableProperty0<T>.observeBindableAsMutableState(): MutableState<T> {
    return PropertyMutableState(observeBindableAsState(), this)
}

@Composable
fun <VM, B> AndroidViewDataBinding(
    viewModel: VM,
    factory: (inflater: LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> B,
    modifier: Modifier = Modifier,
    fieldId: Int = BR.vm,
    update: B.(viewModel: VM) -> Unit = {}
) where VM : ViewModel, VM : androidx.lifecycle.ViewModel, B : ViewDataBinding {
    AndroidViewBinding(
        factory = { inflater, parent, attachToParent ->
            factory(inflater, parent, attachToParent).apply {
                setVariable(fieldId, viewModel)
                viewModel.onBind()
            }
        },
        modifier = modifier,
        update = { update(viewModel) }
    )
}