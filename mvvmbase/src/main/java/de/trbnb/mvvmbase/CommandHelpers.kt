package de.trbnb.mvvmbase

import android.content.Context
import de.trbnb.databindingcommands.command.RuleCommand
import de.trbnb.databindingcommands.command.SimpleCommand

/**
 * Helper method to create a [SimpleCommand] with named parameters.
 */
fun simpleCommand(enabled: Boolean = true, action: (Context) -> Unit) = SimpleCommand(action, enabled)

/**
 * Helper method to create a [RuleCommand] with named parameters.
 */
fun ruleCommand(action: (Context) -> Unit, enabledRule: () -> Boolean) = RuleCommand(action, enabledRule)
