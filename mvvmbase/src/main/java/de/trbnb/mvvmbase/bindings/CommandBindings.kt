package de.trbnb.mvvmbase.bindings

import android.databinding.BindingAdapter
import android.view.View
import de.trbnb.mvvmbase.commands.Command

/**
 * Binds the given [Command] as command that will be invoked when the View has been clicked.
 * This will also bind the [View.isEnabled] property to the [Command.isEnabled] property.
 */
@BindingAdapter("android:clickCommand")
fun View.bindClickCommand(command: Command<Unit, *>) {
    bindEnabled(command)

    setOnClickListener {
        command.invokeSafely(Unit)
    }
}

/**
 * Binds the [View.isEnabled] property to the [Command.isEnabled] property of the given instances.
 */
private fun View.bindEnabled(command: Command<*, *>) {
    isEnabled = command.isEnabled

    command.addEnabledListener {
        post {
            isEnabled = it
        }
    }
}

/**
 * Binds the given [Command] as command that will be invoked when the View has been long-clicked.
 */
@BindingAdapter("android:longClickCommand")
fun View.bindLongClickCommand(command: Command<Unit, *>) {
    setOnLongClickListener {
        if (command.isEnabled) {
            command.invoke(Unit) as? Boolean ?: true
        } else {
            false
        }
    }
}
