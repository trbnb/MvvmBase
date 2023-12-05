package de.trbnb.mvvmbase.coroutines

import androidx.lifecycle.viewModelScope
import de.trbnb.mvvmbase.coroutines.flow.FlowBindable
import de.trbnb.mvvmbase.coroutines.flow.OnCompletion
import de.trbnb.mvvmbase.coroutines.flow.OnException
import de.trbnb.mvvmbase.databinding.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * Interface that defines coroutines extensions for [ViewModel].
 */
public interface CoroutineViewModel : ViewModel {
    /**
     * Gets a [CoroutineScope] that is cancelled when the ViewModel is destroyed.
     */
    public val viewModelScope: CoroutineScope
        get() = (this as? androidx.lifecycle.ViewModel)?.viewModelScope ?: throw RuntimeException(
            "ViewModel doesn't extend androidx.lifecycle.ViewModel and has to implement viewModelScope manually."
        )

    /**
     * Creates a new FlowBindable.Provider instance.
     *
     * @param defaultValue Value of the property from the start.
     */
    @ExperimentalCoroutinesApi
    public fun <T> Flow<T>.toBindable(
        defaultValue: T,
        onException: OnException<T>? = null,
        onCompletion: OnCompletion<T>? = null,
        scope: CoroutineScope = viewModelScope
    ): FlowBindable.Provider<T> = FlowBindable.Provider(this, onException, onCompletion, scope, defaultValue)

    /**
     * Creates a new FlowBindable.Provider instance with `null` as default value.
     */
    @ExperimentalCoroutinesApi
    public fun <T> Flow<T>.toBindable(
        onException: OnException<T>? = null,
        onCompletion: OnCompletion<T>? = null,
        scope: CoroutineScope = viewModelScope
    ): FlowBindable.Provider<T?> = toBindable(null, onException, onCompletion, scope)
}
