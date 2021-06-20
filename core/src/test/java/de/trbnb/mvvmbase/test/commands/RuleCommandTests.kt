package de.trbnb.mvvmbase.test.commands

import de.trbnb.mvvmbase.commands.DisabledCommandInvocationException
import de.trbnb.mvvmbase.commands.RuleCommand
import de.trbnb.mvvmbase.commands.invoke
import de.trbnb.mvvmbase.commands.invokeSafely
import de.trbnb.mvvmbase.databinding.utils.observe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RuleCommandTests {
    @Test
    fun `invocation works when enabled`() {
        val command = RuleCommand(enabledRule = { true }, action = { _: Unit -> 4 })
        assert(command() == 4)
        assert(command.invokeSafely() == 4)
    }

    @Test
    fun `disabled command doesn't work`() {
        val command = RuleCommand(enabledRule = { false }, action = { _: Unit -> 4 })
        assertThrows<DisabledCommandInvocationException> { command() }
        assert(command.invokeSafely() == null)
    }

    @Test
    fun `enabledListener is triggered`() {
        var isEnabled = true
        val command = RuleCommand<Unit, Unit>(enabledRule = { isEnabled }, action = { Unit })
        var wasDisabled = false
        var wasReEnabled = false
        command::isEnabled.observe { enabled ->
            when {
                enabled -> wasReEnabled = true
                else -> wasDisabled = true
            }
        }

        isEnabled = false
        command.onEnabledChanged()
        assert(wasDisabled)
        isEnabled = true
        command.onEnabledChanged()
        assert(wasReEnabled)
    }

    @Test
    fun `enabledListener is not triggered when isEnabled is set to previous value`() = booleanArrayOf(true, false).forEach { bool ->
        var isEnabled = bool
        val command = RuleCommand<Unit, Unit>(enabledRule = { isEnabled }, action = { })

        var listenerWasTriggered = false
        command::isEnabled.observe { listenerWasTriggered = true }
        isEnabled = bool
        command.onEnabledChanged()

        assert(!listenerWasTriggered)
    }

    @Test
    fun `removing enabledListener works`() {
        var isEnabled = true
        val command = RuleCommand<Unit, Unit>(enabledRule = { isEnabled }, action = { })

        var listenerWasTriggered = false
        command::isEnabled.observe { listenerWasTriggered = true }()
        isEnabled = !isEnabled
        command.onEnabledChanged()

        assert(!listenerWasTriggered)
    }

    @Test
    fun `clearing enabledListeners works`() {
        var isEnabled = false
        val command = RuleCommand<Unit, Unit>(enabledRule = { isEnabled }, action = { })

        fun newListener() = object : (Boolean) -> Unit {
            var wasTriggered = false
                private set

            override fun invoke(enabled: Boolean) {
                wasTriggered = true
            }
        }

        val listeners = List(3) { newListener() }.map { it to command::isEnabled.observe(action = it) }

        listeners.forEach { it.second() }
        isEnabled = !isEnabled
        command.onEnabledChanged()

        assert(listeners.all { !it.first.wasTriggered })
    }

    @Test
    fun `changing enabled invokes notifyPropertyChanged`() {
        var isEnabled = false
        val command = RuleCommand<Unit, Unit>(enabledRule = { isEnabled }, action = { })

        var callbackWasTriggered = false
        command::isEnabled.observe { callbackWasTriggered = true }

        isEnabled = true
        command.onEnabledChanged()
        assert(callbackWasTriggered)
    }
}
