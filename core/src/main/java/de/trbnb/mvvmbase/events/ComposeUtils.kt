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
fun EventChannelOwner.OnEvent(action: @Composable (event: Event) -> Unit) {
    val lastEvent = lastEventAsState()
    lastEvent.value?.let { action(it) }
}

/**
 * Observes the events from [EventChannelOwner.eventChannel] as Compose state.
 * If no event has been observed the State contains `null`.
 */
@Composable
fun EventChannelOwner.lastEventAsState(): State<Event?> {
    val state = remember { mutableStateOf<Event?>(null, neverEqualPolicy()) }
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = this, lifecycleOwner) {
        val action = { event: Event -> state.value = event }
        eventChannel.addListener(lifecycleOwner, action)
        onDispose { eventChannel.removeListener(action) }
    }
    return state
}
