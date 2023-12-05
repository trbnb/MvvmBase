package de.trbnb.mvvmbase.commands

import de.trbnb.mvvmbase.observable.ObservableContainer

internal fun RuleCommand<*, *>.dependsOn(observable: ObservableContainer, dependencyPropertyNames: List<String>?) {
    if (dependencyPropertyNames.isNullOrEmpty()) return
    observable.addOnPropertyChangedCallback { _, propertyName ->
        if (propertyName in dependencyPropertyNames) {
            onEnabledChanged()
        }
    }
}

/**
 * Invokes the command with the parameter [Unit].
 */
public operator fun <R> Command<Unit, R>.invoke(): R = invoke(Unit)

/**
 * Invokes the command safely with the parameter [Unit].
 */
public fun <R> Command<Unit, R>.invokeSafely(): R? = invokeSafely(Unit)
