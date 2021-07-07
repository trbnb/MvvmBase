package de.trbnb.mvvmbase.test.commands

import de.trbnb.mvvmbase.commands.DisabledCommandInvocationException
import de.trbnb.mvvmbase.commands.SimpleCommand
import de.trbnb.mvvmbase.commands.invoke
import de.trbnb.mvvmbase.commands.invokeSafely
import de.trbnb.mvvmbase.utils.observe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SimpleCommandTests {
    @Test
    fun `invocation works when enabled`() {
        val command = SimpleCommand { _: Unit -> 4 }
        assert(command() == 4)
        assert(command.invokeSafely() == 4)
    }

    @Test
    fun `disabled command doesn't work`() {
        val command = SimpleCommand(isEnabled = false) { _: Unit -> 4 }
        assertThrows<DisabledCommandInvocationException> { command() }
        assert(command.invokeSafely() == null)
    }

    @Test
    fun `enabledListener is triggered`() {
        val command = SimpleCommand<Unit, Unit>(isEnabled = true) { }
        var wasDisabled = false
        var wasReEnabled = false
        command::isEnabled.observe { enabled ->
            when {
                enabled -> wasReEnabled = true
                else -> wasDisabled = true
            }
        }

        command.isEnabled = false
        assert(wasDisabled)
        command.isEnabled = true
        assert(wasReEnabled)
    }

    @Test
    fun `enabledListener is not triggered when isEnabled is set to previous value`() = booleanArrayOf(true, false).forEach { bool ->
        val command = SimpleCommand<Unit, Unit>(isEnabled = bool) { }

        var listenerWasTriggered = false
        command::isEnabled.observe { listenerWasTriggered = true }
        command.isEnabled = bool

        assert(!listenerWasTriggered)
    }

    @Test
    fun `removing enabledListener works`() {
        val command = SimpleCommand<Unit, Unit> { }

        var listenerWasTriggered = false
        command::isEnabled.observe { listenerWasTriggered = true }()
        command.isEnabled = !command.isEnabled

        assert(!listenerWasTriggered)
    }

    @Test
    fun `clearing enabledListeners works`() {
        val command = SimpleCommand<Unit, Unit> { }

        fun newListener() = object : (Boolean) -> Unit {
            var wasTriggered = false
                private set

            override fun invoke(enabled: Boolean) {
                wasTriggered = true
            }
        }

        val listeners = List(3) { newListener() }.map { it to command::isEnabled.observe(action = it) }

        listeners.forEach { it.second() }
        command.isEnabled = !command.isEnabled

        assert(listeners.all { !it.first.wasTriggered })
    }

    @Test
    fun `changing enabled invokes notifyPropertyChanged`() {
        val command = SimpleCommand<Unit, Unit>(false) { }

        var callbackWasTriggered = false
        command::isEnabled.observe { callbackWasTriggered = true }

        command.isEnabled = true
        assert(callbackWasTriggered)
    }
}
