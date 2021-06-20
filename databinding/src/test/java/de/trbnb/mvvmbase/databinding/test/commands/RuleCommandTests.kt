package de.trbnb.mvvmbase.databinding.test.commands

import androidx.databinding.Observable
import de.trbnb.mvvmbase.databinding.commands.DisabledCommandInvocationException
import de.trbnb.mvvmbase.databinding.commands.EnabledListener
import de.trbnb.mvvmbase.databinding.commands.RuleCommand
import de.trbnb.mvvmbase.databinding.commands.invoke
import de.trbnb.mvvmbase.databinding.commands.invokeSafely
import de.trbnb.mvvmbase.databinding.test.BR
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
        command.addEnabledListener { enabled ->
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
        val command = RuleCommand<Unit, Unit>(enabledRule = { isEnabled }, action = { Unit })

        var listenerWasTriggered = false
        command.addEnabledListener { listenerWasTriggered = true }
        isEnabled = bool
        command.onEnabledChanged()

        assert(!listenerWasTriggered)
    }

    @Test
    fun `removing enabledListener works`() {
        var isEnabled = true
        val command = RuleCommand<Unit, Unit>(enabledRule = { isEnabled }, action = { Unit })

        var listenerWasTriggered = false
        val listener: EnabledListener = { listenerWasTriggered = true }
        command.addEnabledListener(listener)
        command.removeEnabledListener(listener)
        isEnabled = !isEnabled
        command.onEnabledChanged()

        assert(!listenerWasTriggered)
    }

    @Test
    fun `clearing enabledListeners works`() {
        var isEnabled = false
        val command = RuleCommand<Unit, Unit>(enabledRule = { isEnabled }, action = { Unit })

        fun newListener() = object : EnabledListener {
            var wasTriggered = false
                private set

            override fun invoke(enabled: Boolean) {
                wasTriggered = true
            }
        }

        val listeners = List(3) { newListener() }.onEach { command.addEnabledListener(it) }

        command.clearEnabledListeners()
        isEnabled = !isEnabled
        command.onEnabledChanged()

        assert(listeners.all { !it.wasTriggered })
    }

    @Test
    fun `changing enabled invokes notifyPropertyChanged`() {
        var isEnabled = false
        val command = RuleCommand<Unit, Unit>(enabledRule = { isEnabled }, action = { Unit })

        var callbackWasTriggered = false
        command.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                if (propertyId == BR.enabled) {
                    callbackWasTriggered = true
                }
            }
        })

        isEnabled = true
        command.onEnabledChanged()
        assert(callbackWasTriggered)
    }
}
