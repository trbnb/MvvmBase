package de.trbnb.mvvmbase.test.commands

import androidx.databinding.Bindable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.commands.Command
import de.trbnb.mvvmbase.commands.SimpleCommand
import de.trbnb.mvvmbase.commands.addEnabledListener
import de.trbnb.mvvmbase.commands.ruleCommand
import de.trbnb.mvvmbase.commands.simpleCommand
import de.trbnb.mvvmbase.test.BR
import org.junit.jupiter.api.Test

class CommandTests {
    @Test
    fun `enabledListener with Lifecycle`() {
        val command: Command<*, *> = SimpleCommand<Unit, Unit> { Unit }
        val lifecycleOwner = object : LifecycleOwner {
            private val lifecycle = LifecycleRegistry(this).apply {
                currentState = Lifecycle.State.STARTED
            }
            override fun getLifecycle() = lifecycle
            fun destroy() { lifecycle.currentState = Lifecycle.State.DESTROYED }
        }

        var listenerWasTriggered = false
        command.addEnabledListener(lifecycleOwner) { listenerWasTriggered = true }
        lifecycleOwner.destroy()
        assert(!listenerWasTriggered)
    }

    @Test
    fun `observeLifecycle works`() {
        val viewModel = object : BaseViewModel() {
            val command = simpleCommand { Unit }
        }

        var amountListenerWasCalled = 0
        viewModel.command.addEnabledListenerForView { amountListenerWasCalled++ }
        viewModel.onBind()
        viewModel.command.isEnabled = !viewModel.command.isEnabled
        assert(amountListenerWasCalled == 1)

        viewModel.onUnbind()
        viewModel.command.isEnabled = !viewModel.command.isEnabled
        assert(amountListenerWasCalled == 1)
    }

    @Test
    fun `dependsOn works`() {
        MvvmBase.init<BR>()
        val viewModel = DependsOnViewModel()
        assert(!viewModel.command.isEnabled)

        viewModel.foo = Any()
        assert(viewModel.command.isEnabled)
        MvvmBase.init<Unit>()
    }

    class DependsOnViewModel : BaseViewModel() {
        @get:Bindable
        var foo by bindable<Any>()

        val command = ruleCommand<Unit, Unit>(
            enabledRule = { foo != null },
            action = { Unit },
            dependentFieldIds = intArrayOf(BR.foo)
        )
    }
}
