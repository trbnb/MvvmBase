package de.trbnb.mvvmbase.databinding.commands

import androidx.databinding.Bindable
import androidx.databinding.Observable

public typealias EnabledListener = (enabled: Boolean) -> Unit

/**
 * The basic contract for command implementations.
 *
 * @param P The parameter type for invocation. An instance of this has to be used to call [invoke]. [Unit] may be used if no parameter is neccessary.
 * @param R The return type for invocation. An instance of this has to be returned from [invoke].
 */
public interface Command<in P, out R> : Observable {
    /**
     * Determines whether this Command is enabled or not.
     *
     * @return Returns `true` if this Command is enabled, otherwise `false`.
     */
    @get:Bindable
    public val isEnabled: Boolean

    /**
     * Invokes the Command.
     *
     * @throws de.trbnb.mvvmbase.commands.DisabledCommandInvocationException If [isEnabled] returns `false`.
     * @return A return type instance.
     */
    public operator fun invoke(param: P): R

    /**
     * Invokes the Command only if [isEnabled] equals `true`.
     *
     * @return A return type instance if [isEnabled] equals `true` before invocation, otherwise `null`.
     */
    public fun invokeSafely(param: P): R? {
        return if (isEnabled) invoke(param) else null
    }

    /**
     * Adds a listener that is notified when the value of [isEnabled] might have changed.
     */
    public fun addEnabledListener(listener: EnabledListener)

    /**
     * Adds a listener for a view component.
     * These will be removed via [clearEnabledListenersForViews] and [observeLifecycle] .
     */
    public fun addEnabledListenerForView(listener: EnabledListener)

    /**
     * Removes a listener that is used for listening to changes to [isEnabled].
     * A listener that is passed to this method will not be notified anymore.
     */
    public fun removeEnabledListener(listener: EnabledListener)

    /**
     * Removes all listeners that are used for listening to changes to [isEnabled].
     * No previously added listeners will be notified anymore.
     */
    public fun clearEnabledListeners()

    /**
     * Removes all listeners that were added via [addEnabledListenerForView].
     */
    public fun clearEnabledListenersForViews()
}
