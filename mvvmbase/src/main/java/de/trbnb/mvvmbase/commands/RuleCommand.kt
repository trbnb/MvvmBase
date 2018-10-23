package de.trbnb.mvvmbase.commands

/**
 * A [Command] that determines if it is enabled via a predicate.
 * This predicate, or "rule", is set during initialization.

 * The predicates result will be cached. A refresh can be triggered by calling [onEnabledChanged].
 *
 * @param action The initial action that will be run when the Command is executed.
 * @param enabledRule The initial rule that determines if this Command is enabled.
 */
open class RuleCommand<in P, out R>(action: (P) -> R, private val enabledRule: () -> Boolean) : BaseCommandImpl<P, R>(action) {

    override var isEnabled: Boolean = enabledRule()
        protected set(value) {
            if (field == value) return

            field = value
            triggerEnabledChangedListener()
        }

    /**
     * This method has to be called when the result of the rule might have changed.
     */
    fun onEnabledChanged() {
        isEnabled = enabledRule()
    }
}

/**
 * Helper function to create a [RuleCommand] with [Unit] as parameter type, indicating that the command doesn't need a parameter.
 */
@Suppress("FunctionName")
fun <R> RuleCommand(action: (Unit) -> R, enabledRule: () -> Boolean): RuleCommand<Unit, R> {
    return RuleCommand<Unit, R>(action, enabledRule)
}
