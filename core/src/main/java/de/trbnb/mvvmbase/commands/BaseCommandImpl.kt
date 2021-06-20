package de.trbnb.mvvmbase.commands

import de.trbnb.mvvmbase.OnPropertyChangedCallback
import de.trbnb.mvvmbase.observable.PropertyChangeRegistry

/**
 * Base class for standard [Command] implementations.

 * An implementation of the [Command.isEnabled] is not given.
 *
 * @param action The initial action that will be run when the Command is executed.
 */
abstract class BaseCommandImpl<in P, out R>(private val action: (P) -> R) : Command<P, R> {
    private val registry = PropertyChangeRegistry(emptyList())

    override fun addOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        registry.add(callback)
    }

    override fun removeOnPropertyChangedCallback(callback: OnPropertyChangedCallback) {
        registry.remove(callback)
    }

    override fun notifyPropertyChanged(propertyName: String) {
        registry.notifyChange(this, propertyName)
    }

    final override fun invoke(param: P): R {
        if (!isEnabled) {
            throw DisabledCommandInvocationException()
        }

        return action(param)
    }

    /**
     * This method should be called when the result of [isEnabled] might have changed.
     */
    protected fun triggerEnabledChangedListener() {
        notifyPropertyChanged(::isEnabled)
    }
}
