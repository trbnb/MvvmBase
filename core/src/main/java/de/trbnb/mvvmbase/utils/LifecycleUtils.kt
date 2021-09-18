package de.trbnb.mvvmbase.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

val LifecycleOwner.isDestroyed: Boolean
    get() = lifecycle.currentState == Lifecycle.State.DESTROYED