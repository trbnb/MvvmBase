package de.trbnb.mvvmbase.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import de.trbnb.mvvmbase.ViewModel

@Composable
fun EventChannel.OnEvent(action: (event: Event) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = this, lifecycleOwner) {
        addListener(lifecycleOwner, action)
        onDispose { removeListener(action) }
    }
}

@Composable
fun EventChannel.lastEventAsState(): State<Event?> {
    val state = remember { mutableStateOf<Event?>(null, neverEqualPolicy()) }
    OnEvent { state.value = it }
    return state
}

@Composable
fun ViewModel.OnEvent(action: (event: Event) -> Unit) = eventChannel.OnEvent(action)

@Composable
fun ViewModel.lastEventAsState(): State<Event?> = eventChannel.lastEventAsState()