package de.trbnb.mvvmbase.commands

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.utils.resolveFieldId
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
    @get:Bindable
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
@JvmName("parameterizedRuleCommand")
fun <P, R> ViewModel.ruleCommand(
    action: (P) -> R,
    enabledRule: () -> Boolean,
    dependentFieldIds: IntArray? = null
): RuleCommand<P, R> = RuleCommand(action, enabledRule).apply {
    observeLifecycle(this@ruleCommand)
    dependsOn(this@ruleCommand, dependentFieldIds)
}

/**
 * Helper function to create a [RuleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
@JvmName("parameterizedRuleCommand")
fun <P, R> ViewModel.ruleCommand(
    action: (P) -> R,
    enabledRule: () -> Boolean,
    dependentFields: List<KProperty<*>>
): RuleCommand<P, R> = ruleCommand(action, enabledRule, dependentFields.map { it.resolveFieldId() }.toIntArray())

/**
 * Helper function to create a parameter-less [RuleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
fun <R> ViewModel.ruleCommand(
    action: (Unit) -> R,
    enabledRule: () -> Boolean,
    dependentFieldIds: IntArray? = null
): RuleCommand<Unit, R> = ruleCommand<Unit, R>(action, enabledRule, dependentFieldIds)

/**
 * Helper function to create a parameter-less [RuleCommand] that clears all it's listeners automatically when
 * [ViewModel.onUnbind] is called.
 */
fun <R> ViewModel.ruleCommand(
    action: (Unit) -> R,
    enabledRule: () -> Boolean,
    dependentFields: List<KProperty<*>>
): RuleCommand<Unit, R> = ruleCommand(action, enabledRule, dependentFields)
