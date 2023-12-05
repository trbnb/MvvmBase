package de.trbnb.mvvmbase.databinding.commands

import androidx.databinding.Observable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

internal fun Command<*, *>.observeLifecycle(lifecycleOwner: LifecycleOwner) {
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_PAUSE) {
                clearEnabledListenersForViews()
            }
        }
    })
}

internal fun RuleCommand<*, *>.dependsOn(observable: Observable, dependentFieldIds: IntArray?) {
    if (dependentFieldIds?.isEmpty() != false) return
    observable.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (propertyId in dependentFieldIds) {
                onEnabledChanged()
            }
        }
    })
}

/**
 * Calls [Command.addEnabledListener] and removes the listener if the lifecycle of [lifecycleOwner] is destroyed.
 */
public fun Command<*, *>.addEnabledListener(lifecycleOwner: LifecycleOwner, listener: (enabled: Boolean) -> Unit) {
    addEnabledListener(listener)
    lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
        override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
            if (event == Lifecycle.Event.ON_DESTROY) {
                removeEnabledListener(listener)
            }
        }
    })
}

/**
 * Invokes the command with the parameter [Unit].
 */
public operator fun <R> Command<Unit, R>.invoke(): R = invoke(Unit)

/**
 * Invokes the command safely with the parameter [Unit].
 */
public fun <R> Command<Unit, R>.invokeSafely(): R? = invokeSafely(Unit)
