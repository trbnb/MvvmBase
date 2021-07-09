package de.trbnb.mvvmbase.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import de.trbnb.mvvmbase.utils.observe
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty0

@Composable
fun <T> KProperty0<T>.observeAsState(): State<T> {
    val state = remember { mutableStateOf(get()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = this, lifecycleOwner) {
        val dispose = observe(lifecycleOwner, false) { state.value = it }
        onDispose(dispose::invoke)
    }
    return state
}

@Composable
fun <T> KMutableProperty0<T>.observeAsMutableState(): MutableState<T> {
    return ViewModelMutableState(observeAsState(), this)
}

inline val <T> MutableState<T>.setter: (T) -> Unit get() = component2()