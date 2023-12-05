package de.trbnb.mvvmbase.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

/**
 * Returns `true` if [LifecycleOwner.getLifecycle] is in a destroyed state.
 */
public val LifecycleOwner.isDestroyed: Boolean
    get() = lifecycle.currentState == Lifecycle.State.DESTROYED
