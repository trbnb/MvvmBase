package de.trbnb.mvvmbase.commands

/**
 * A [Command] that determines if it is enabled via a predicate.
 * This predicate, or "rule", is set during initialization.

 * The predicates result will be cached. A refresh can be triggered by calling [onEnabledChanged].
 *
 * @param action The initial action that will be run when the Command is executed.
 * @param enabledRule The initial rule that determines if this Command is enabled.
 */
open class RuleCommand<out R>(action: () -> R, private val enabledRule: () -> Boolean) : BaseCommandImpl<R>(action) {

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
