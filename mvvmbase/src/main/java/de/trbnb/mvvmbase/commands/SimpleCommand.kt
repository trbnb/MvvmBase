package de.trbnb.mvvmbase.commands

import de.trbnb.mvvmbase.ViewModel

/**
 * A [Command] implementation that can simply be set as en-/disabled with a boolean value.
 *
 * @param action The initial action that will be run when the Command is executed.
 * @param isEnabled Has to be `true` if this Command should be enabled, otherwise `false`.
 */
class SimpleCommand<in P, out R>
@Deprecated("Use the ViewModel extension functions to create lifecycle-aware commands.", replaceWith = ReplaceWith("simpleCommand()"), level = DeprecationLevel.WARNING)
constructor(isEnabled: Boolean = true, action: (P) -> R) : BaseCommandImpl<P, R>(action) {

    override var isEnabled: Boolean = isEnabled
        set(value) {
            field = value
            triggerEnabledChangedListener()
        }

}

/**
 * Helper function to create a [SimpleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
@Suppress("DEPRECATION")
@JvmName("parameterizedSimpleCommand")
fun <P, R> ViewModel.simpleCommand(isEnabled: Boolean = true, action: (P) -> R): SimpleCommand<P, R> {
    return SimpleCommand(isEnabled, action).apply {
        observeLifecycle(lifecycle)
    }
}

/**
 * Helper function to create a parameter-less [SimpleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
@Suppress("DEPRECATION")
fun <R> ViewModel.simpleCommand(isEnabled: Boolean = true, action: (Unit) -> R): SimpleCommand<Unit, R> {
    return SimpleCommand(isEnabled, action).apply {
        observeLifecycle(lifecycle)
    }
}
