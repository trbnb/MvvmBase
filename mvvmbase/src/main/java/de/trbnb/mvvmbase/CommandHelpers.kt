package de.trbnb.mvvmbase

import de.trbnb.databindingcommands.command.RuleCommand
import de.trbnb.databindingcommands.command.SimpleCommand

/**
 * Helper method to create a [SimpleCommand] with named parameters.
 */
fun simpleCommand(enabled: Boolean = true, action: () -> Unit) = SimpleCommand(action, enabled)

/**
 * Helper method to create a [RuleCommand] with named parameters.
 */
fun ruleCommand(action: () -> Unit, enabledRule: () -> Boolean) = RuleCommand(action, enabledRule)
