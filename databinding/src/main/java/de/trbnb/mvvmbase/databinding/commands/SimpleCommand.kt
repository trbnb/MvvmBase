package de.trbnb.mvvmbase.databinding.commands

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.databinding.ViewModel

/**
 * A [Command] implementation that can simply be set as en-/disabled with a boolean value.
 *
 * @param action The initial action that will be run when the Command is executed.
 * @param isEnabled Has to be `true` if this Command should be enabled, otherwise `false`.
 */
public class SimpleCommand<in P, out R> internal constructor(isEnabled: Boolean = true, action: (P) -> R) : BaseCommandImpl<P, R>(action) {
    @get:Bindable
    override var isEnabled: Boolean = isEnabled
        set(value) {
            if (field == value) return
            field = value
            triggerEnabledChangedListener()
        }
}

/**
 * Helper function to create a [SimpleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
@JvmName("parameterizedSimpleCommand")
public fun <P, R> ViewModel.simpleCommand(
    isEnabled: Boolean = true,
    action: (P) -> R
): SimpleCommand<P, R> = SimpleCommand(isEnabled, action).apply {
    observeLifecycle(this@simpleCommand)
}

/**
 * Helper function to create a parameter-less [SimpleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
public fun <R> ViewModel.simpleCommand(
    isEnabled: Boolean = true,
    action: (Unit) -> R
): SimpleCommand<Unit, R> = simpleCommand<Unit, R>(isEnabled, action)
