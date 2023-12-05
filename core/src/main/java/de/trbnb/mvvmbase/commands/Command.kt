package de.trbnb.mvvmbase.commands

import de.trbnb.mvvmbase.observable.ObservableContainer

/**
 * The basic contract for command implementations.
 *
 * @param P The parameter type for invocation. An instance of this has to be used to call [invoke]. [Unit] may be used if no parameter is neccessary.
 * @param R The return type for invocation. An instance of this has to be returned from [invoke].
 */
public interface Command<in P, out R> : ObservableContainer {
    /**
     * Determines whether this Command is enabled or not.
     *
     * @return Returns `true` if this Command is enabled, otherwise `false`.
     */
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
}
