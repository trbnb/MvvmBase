package de.trbnb.mvvmbase.commands

import de.trbnb.mvvmbase.ViewModel
import kotlin.reflect.KProperty

/**
 * A [Command] that determines if it is enabled via a predicate.
 * This predicate, or "rule", is set during initialization.

 * The predicates result will be cached. A refresh can be triggered by calling [onEnabledChanged].
 *
 * @param action The initial action that will be run when the Command is executed.
 * @param enabledRule The initial rule that determines if this Command is enabled.
 */
class RuleCommand<in P, out R> internal constructor(
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
@JvmName("parameterizedRuleCommand0")
fun <P, R> ViewModel.ruleCommand(
    action: (P) -> R,
    enabledRule: () -> Boolean,
    dependencyPropertyNames: List<String>? = null
): RuleCommand<P, R> = RuleCommand(action, enabledRule).apply {
    dependsOn(this@ruleCommand, dependencyPropertyNames)
}

/**
 * Helper function to create a [RuleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
@JvmName("parameterizedRuleCommand1")
fun <P, R> ViewModel.ruleCommand(
    action: (P) -> R,
    enabledRule: () -> Boolean,
    dependencyProperties: List<KProperty<*>>
): RuleCommand<P, R> = ruleCommand(action, enabledRule, dependencyProperties.map { it.name })

/**
 * Helper function to create a parameter-less [RuleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
@JvmName("parameterizedRuleCommand2")
fun <R> ViewModel.ruleCommand(
    action: (Unit) -> R,
    enabledRule: () -> Boolean,
    dependencyPropertyNames: List<String>? = null
): RuleCommand<Unit, R> = ruleCommand<Unit, R>(action, enabledRule, dependencyPropertyNames)

/**
 * Helper function to create a parameter-less [RuleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
fun <R> ViewModel.ruleCommand(
    action: (Unit) -> R,
    enabledRule: () -> Boolean,
    dependencyProperties: List<KProperty<*>>
): RuleCommand<Unit, R> = ruleCommand<Unit, R>(action, enabledRule, dependencyProperties)
