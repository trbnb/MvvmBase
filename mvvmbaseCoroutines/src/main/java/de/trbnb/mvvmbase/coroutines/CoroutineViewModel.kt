package de.trbnb.mvvmbase.coroutines

import androidx.lifecycle.viewModelScope
import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.coroutines.flow.FlowBindable
import de.trbnb.mvvmbase.coroutines.flow.OnCompletion
import de.trbnb.mvvmbase.coroutines.flow.OnException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * Interface that defines coroutines extensions for [ViewModel].
 */
interface CoroutineViewModel : ViewModel {
    /**
     * Gets a [CoroutineScope] that is cancelled when the ViewModel is destroyed.
     */
    val viewModelScope: CoroutineScope
        get() = (this as? androidx.lifecycle.ViewModel)?.viewModelScope ?: throw RuntimeException(
            "ViewModel doesn't extend androidx.lifecycle.ViewModel and has to implement viewModelScope manually."
        )

    /**
     * Creates a new FlowBindable.Provider instance.
     *
     * @param defaultValue Value of the property from the start.
     * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
     */
    @ExperimentalCoroutinesApi
    fun <T> Flow<T>.toBindable(
        defaultValue: T,
        fieldId: Int? = null,
        onException: OnException<T>? = null,
        onCompletion: OnCompletion<T>? = null,
        scope: CoroutineScope = viewModelScope
    ): FlowBindable.Provider<T> = FlowBindable.Provider(this, onException, onCompletion, scope, fieldId, defaultValue)

    /**
     * Creates a new FlowBindable.Provider instance with `null` as default value.
     *
     * @param fieldId ID of the field as in the BR.java file. A `null` value will cause automatic detection of that field ID.
     */
    @ExperimentalCoroutinesApi
    fun <T> Flow<T>.toBindable(
        fieldId: Int? = null,
        onException: OnException<T>? = null,
        onCompletion: OnCompletion<T>? = null,
        scope: CoroutineScope = viewModelScope
    ): FlowBindable.Provider<T?> = toBindable(null, fieldId, onException, onCompletion, scope)
}
