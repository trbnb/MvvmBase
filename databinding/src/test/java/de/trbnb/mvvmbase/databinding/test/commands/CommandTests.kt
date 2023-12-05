package de.trbnb.mvvmbase.databinding.test.commands

import androidx.databinding.Bindable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import de.trbnb.mvvmbase.MvvmBase
import de.trbnb.mvvmbase.databinding.BaseViewModel
import de.trbnb.mvvmbase.databinding.bindableproperty.bindable
import de.trbnb.mvvmbase.databinding.commands.Command
import de.trbnb.mvvmbase.databinding.commands.SimpleCommand
import de.trbnb.mvvmbase.databinding.commands.addEnabledListener
import de.trbnb.mvvmbase.databinding.commands.ruleCommand
import de.trbnb.mvvmbase.databinding.commands.simpleCommand
import de.trbnb.mvvmbase.databinding.initDataBinding
import de.trbnb.mvvmbase.databinding.resetDataBinding
import de.trbnb.mvvmbase.databinding.test.BR
import org.junit.jupiter.api.Test

class CommandTests {
    @Test
    fun `enabledListener with Lifecycle`() {
        val command: Command<*, *> = SimpleCommand<Unit, Unit> { }
        val lifecycleOwner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry.createUnsafe(this).apply {
                currentState = Lifecycle.State.STARTED
            }
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
            val command = simpleCommand { }
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
        MvvmBase.initDataBinding()
        val viewModel = DependsOnViewModel()
        assert(!viewModel.command.isEnabled)

        viewModel.foo = Any()
        assert(viewModel.command.isEnabled)
        MvvmBase.resetDataBinding()
    }

    class DependsOnViewModel : BaseViewModel() {
        @get:Bindable
        var foo by bindable<Any>()

        val command = ruleCommand<Unit, Unit>(
            enabledRule = { foo != null },
            action = { },
            dependentFieldIds = intArrayOf(BR.foo)
        )
    }
}
