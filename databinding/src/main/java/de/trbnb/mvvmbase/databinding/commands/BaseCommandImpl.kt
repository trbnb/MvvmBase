package de.trbnb.mvvmbase.databinding.commands

import androidx.databinding.BaseObservable
import de.trbnb.mvvmbase.databinding.BR

/**
 * Base class for standard [Command] implementations.

 * An implementation of the [Command.isEnabled] is not given.
 *
 * @param action The initial action that will be run when the Command is executed.
 */
abstract class BaseCommandImpl<in P, out R>(private val action: (P) -> R) : BaseObservable(), Command<P, R> {
    private val listeners = mutableListOf<(Boolean) -> Unit>()

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
        notifyPropertyChanged(BR.enabled)
        listeners.forEach { it(isEnabled) }
    }

    override fun addEnabledListener(listener: EnabledListener) {
        listeners.add(listener)
    }

    override fun removeEnabledListener(listener: EnabledListener) {
        listeners.remove(listener)
    }

    override fun clearEnabledListeners() {
        listeners.clear()
    }

    override fun addEnabledListenerForView(listener: EnabledListener) {
        listeners.add(ViewListener(listener))
    }

    override fun clearEnabledListenersForViews() {
        listeners.removeAll { it is ViewListener }
    }

    private class ViewListener(private val listener: EnabledListener) : EnabledListener {
        override fun invoke(enabled: Boolean) = listener(enabled)
    }
}
