package de.trbnb.mvvmbase.commands

/**
 * A [Command] implementation that can simply be set as en-/disabled with a boolean value.
 *
 * @param action The initial action that will be run when the Command is executed.
 * @param isEnabled Has to be `true` if this Command should be enabled, otherwise `false`.
 */
class SimpleCommand<in P, out R> internal constructor(isEnabled: Boolean = true, action: (P) -> R) : BaseCommandImpl<P, R>(action) {
    override var isEnabled: Boolean = isEnabled
        set(value) {
            if (field == value) return
            field = value
            triggerEnabledChangedListener()
        }
}

/**
 * Helper function to create a [SimpleCommand].
 */
@JvmName("parameterizedSimpleCommand")
fun <P, R> simpleCommand(
    isEnabled: Boolean = true,
    action: (P) -> R
): SimpleCommand<P, R> = SimpleCommand(isEnabled, action)

/**
 * Helper function to create a parameter-less [SimpleCommand].
 */
fun <R> simpleCommand(
    isEnabled: Boolean = true,
    action: (Unit) -> R
): SimpleCommand<Unit, R> = simpleCommand<Unit, R>(isEnabled, action)
