package de.trbnb.mvvmbase.events

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner

/**
 * Helper function to use handle one-off events in Compose.
 */
@Composable
fun EventChannelOwner.OnEvent(action: (event: Event) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = this, lifecycleOwner) {
        eventChannel.addListener(lifecycleOwner, action)
        onDispose { eventChannel.removeListener(action) }
    }
}

/**
 * Observes the events from [EventChannelOwner.eventChannel] as Compose state.
 */
@Composable
fun EventChannelOwner.lastEventAsState(): State<Event?> {
    val state = remember { mutableStateOf<Event?>(null, neverEqualPolicy()) }
    OnEvent { state.value = it }
    return state
}
