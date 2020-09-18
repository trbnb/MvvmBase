package de.trbnb.mvvmbase.test.commands

import androidx.databinding.Observable
import de.trbnb.mvvmbase.commands.DisabledCommandInvocationException
import de.trbnb.mvvmbase.commands.EnabledListener
import de.trbnb.mvvmbase.commands.SimpleCommand
import de.trbnb.mvvmbase.commands.invoke
import de.trbnb.mvvmbase.commands.invokeSafely
import de.trbnb.mvvmbase.test.BR
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
        val command = SimpleCommand<Unit, Unit>(isEnabled = true) { Unit }
        var wasDisabled = false
        var wasReEnabled = false
        command.addEnabledListener { enabled ->
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
        val command = SimpleCommand<Unit, Unit>(isEnabled = bool) { Unit }

        var listenerWasTriggered = false
        command.addEnabledListener { listenerWasTriggered = true }
        command.isEnabled = bool

        assert(!listenerWasTriggered)
    }

    @Test
    fun `removing enabledListener works`() {
        val command = SimpleCommand<Unit, Unit> { Unit }

        var listenerWasTriggered = false
        val listener: EnabledListener = { listenerWasTriggered = true }
        command.addEnabledListener(listener)
        command.removeEnabledListener(listener)
        command.isEnabled = !command.isEnabled

        assert(!listenerWasTriggered)
    }

    @Test
    fun `clearing enabledListeners works`() {
        val command = SimpleCommand<Unit, Unit> { Unit }

        fun newListener() = object : EnabledListener {
            var wasTriggered = false
                private set

            override fun invoke(enabled: Boolean) {
                wasTriggered = true
            }
        }

        val listeners = List(3) { newListener() }.onEach { command.addEnabledListener(it) }

        command.clearEnabledListeners()
        command.isEnabled = !command.isEnabled

        assert(listeners.all { !it.wasTriggered })
    }

    @Test
    fun `changing enabled invokes notifyPropertyChanged`() {
        val command = SimpleCommand<Unit, Unit>(false) { Unit }

        var callbackWasTriggered = false
        command.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (propertyId == BR.enabled) {
                    callbackWasTriggered = true
                }
            }
        })

        command.isEnabled = true
        assert(callbackWasTriggered)
    }
}
