package de.trbnb.mvvmbase.commands

import de.trbnb.mvvmbase.ViewModel

/**
 * A [Command] that determines if it is enabled via a predicate.
 * This predicate, or "rule", is set during initialization.

 * The predicates result will be cached. A refresh can be triggered by calling [onEnabledChanged].
 *
 * @param action The initial action that will be run when the Command is executed.
 * @param enabledRule The initial rule that determines if this Command is enabled.
 */
class RuleCommand<in P, out R>
@Deprecated("Use the ViewModel extension functions to create lifecycle-aware commands.", replaceWith = ReplaceWith("ruleCommand(action, enabledRule)"), level = DeprecationLevel.WARNING)
constructor(
    action: (P) -> R,
    private val enabledRule: () -> Boolean
) : BaseCommandImpl<P, R>(action) {

    override var isEnabled: Boolean = enabledRule()
        private set(value) {
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
 * Helper function to create a [RuleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
@Suppress("DEPRECATION")
@JvmName("parameterizedRuleCommand")
fun <P, R> ViewModel.ruleCommand(action: (P) -> R, enabledRule: () -> Boolean): RuleCommand<P, R> {
    return RuleCommand(action, enabledRule).apply {
        observeLifecycle(lifecycle)
    }
}

/**
 * Helper function to create a parameter-less [RuleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
@Suppress("DEPRECATION")
fun <R> ViewModel.ruleCommand(action: (Unit) -> R, enabledRule: () -> Boolean): RuleCommand<Unit, R> {
    return RuleCommand(action, enabledRule).apply {
        observeLifecycle(lifecycle)
    }
}
