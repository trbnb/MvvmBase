package de.trbnb.mvvmbase.commands

/**
 * A [Command] implementation that can simply be set as en-/disabled with a boolean value.
 *
 * @param action The initial action that will be run when the Command is executed.
 * @param isEnabled Has to be `true` if this Command should be enabled, otherwise `false`.
 */
open class SimpleCommand<in P, out R>(isEnabled: Boolean = true, action: (P) -> R) : BaseCommandImpl<P, R>(action) {

    override var isEnabled: Boolean = isEnabled
        set(value) {
            field = value
            triggerEnabledChangedListener()
        }

}

/**
 * Helper function to create a [SimpleCommand] with [Unit] as parameter type, indicating that the command doesn't need a parameter.
 */
@Suppress("FunctionName")
fun <R> SimpleCommand(isEnabled: Boolean = true, action: (Unit) -> R): SimpleCommand<Unit, R> {
    return SimpleCommand<Unit, R>(isEnabled, action)
}
