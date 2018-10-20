package de.trbnb.mvvmbase.commands

import java.lang.ref.WeakReference

/**
 * Base class for standard [Command] implementations.

 * An implementation of the [Command.isEnabled] is not given.
 *
 * @param action The initial action that will be run when the Command is executed.
 */
abstract class BaseCommandImpl<out R>(private val action: () -> R) : Command<R> {

    private val listeners = mutableListOf<WeakReference<(Boolean) -> Unit>>()

    final override fun invoke(): R {
        if(!isEnabled){
            throw DisabledCommandInvocationException()
        }

        return action()
    }

    /**
     * This method should be called when the result of [isEnabled] might have changed.
     */
    protected fun triggerEnabledChangedListener() {
        listeners.forEach {
            it.get()?.invoke(isEnabled)
        }
    }

    override fun addEnabledListener(listener: (Boolean) -> Unit) {
        listeners.add(WeakReference(listener))
    }

    override fun removeEnabledListener(listener: (Boolean) -> Unit) {
        listeners.removeAll { it.get() == listener }
    }

    override fun clearEnabledListeners() {
        listeners.clear()
    }

}
