package de.trbnb.mvvmbase.databinding.commands

/**
 * Exception that will only be thrown if [Command.invoke] has been called even though
 * [Command.isEnabled] was `false` at the same time.
 */
class DisabledCommandInvocationException : RuntimeException()
