package de.trbnb.mvvmbase.databinding.test

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.Lifecycle
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.events.Event
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.ExtensionContext
import java.io.Closeable

@ExtendWith(InstantTaskExecutorRuleForJUnit5::class)
class NestedViewModelTests {
    @Test
    fun `autoDestroy() destroys ViewModels in a parent ViewModel`() {
        val childViewModel = SimpleViewModel()
        val parentViewModel = object : BaseViewModel() {
            val items = listOf(childViewModel).autoDestroy()
        }

        parentViewModel.destroy()
        assert(childViewModel.lifecycle.currentState == Lifecycle.State.DESTROYED)
    }

    @Test
    fun `bindEvents() redirects events from child ViewModels to eventChannel of parent ViewModel`() {
        val event = object : Event {}
        val childViewModel = SimpleViewModel()
        val parentViewModel = object : BaseViewModel() {
            val items = listOf(childViewModel).bindEvents()
        }

        var eventWasReceived = false

        parentViewModel.eventChannel.addListener { newEvent ->
            if (event == newEvent) {
                eventWasReceived = true
            }
        }

        childViewModel.eventChannel(event)
        assert(eventWasReceived)
    }

    @Test
    fun `destroy() closes all tags`() {
        var tagIsClosed = false
        val viewModel = object : BaseViewModel() {
            init {
                initTag("foo", object : Closeable {
                    override fun close() {
                        tagIsClosed = true
                    }
                })
            }
        }

        viewModel.destroy()
        assert(tagIsClosed)
    }
}

class SimpleViewModel : BaseViewModel()

class InstantTaskExecutorRuleForJUnit5 : AfterEachCallback, BeforeEachCallback {
    override fun beforeEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }
        })
    }

    override fun afterEach(context: ExtensionContext?) {
        ArchTaskExecutor.getInstance().setDelegate(null)
    }
}