package de.trbnb.mvvmbase.test.commands

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.commands.Command
import de.trbnb.mvvmbase.commands.SimpleCommand
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.observableproperty.observable
import de.trbnb.mvvmbase.utils.observe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class CommandTests {
    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            MvvmBase.disableViewModelLifecycleThreadConstraints()
        }
    }

    @Test
    fun `enabledListener with Lifecycle`() {
        val command: Command<*, *> = SimpleCommand<Unit, Unit> { }
        val lifecycleOwner = object : LifecycleOwner {
            private val lifecycle = LifecycleRegistry.createUnsafe(this).apply {
                currentState = Lifecycle.State.STARTED
            }
            override fun getLifecycle() = lifecycle
            fun destroy() { lifecycle.currentState = Lifecycle.State.DESTROYED }
        }

        var listenerWasTriggered = false
        command::isEnabled.observe(lifecycleOwner) { listenerWasTriggered = true }
        lifecycleOwner.destroy()
        assert(!listenerWasTriggered)
    }

    @Test
    fun `observeLifecycle works`() {
        val viewModel = object : BaseViewModel() {
            val command = simpleCommand { }
        }

        var amountListenerWasCalled = 0
        viewModel.command::isEnabled.observe { amountListenerWasCalled++ }
        viewModel.command.isEnabled = !viewModel.command.isEnabled
        assertEquals(amountListenerWasCalled, 1)

        viewModel.command.isEnabled = !viewModel.command.isEnabled
        assertEquals(amountListenerWasCalled, 2)
    }

    @Test
    fun `dependsOn works`() {
        val viewModel = DependsOnViewModel()
        assert(!viewModel.command.isEnabled)

        viewModel.foo = Any()
        assert(viewModel.command.isEnabled)
    }

    class DependsOnViewModel : BaseViewModel() {
        var foo by observable<Any>()

        val command = ruleCommand<Unit, Unit>(
            enabledRule = { foo != null },
            action = {},
            dependencyProperties = listOf(::foo)
        )
    }
}
