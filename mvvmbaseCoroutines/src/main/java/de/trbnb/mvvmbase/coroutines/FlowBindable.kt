package de.trbnb.mvvmbase.coroutines

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.AfterSet
import de.trbnb.mvvmbase.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.bindableproperty.BindableProperty
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.bindableproperty.Validate
import de.trbnb.mvvmbase.utils.resolveFieldId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

typealias OnException<T> = (suspend FlowCollector<T>.(Throwable) -> Unit)?
typealias OnCompletion<T> = (suspend FlowCollector<T>.(Throwable?) -> Unit)?

/**
 * Bindable delegate property that has the last emitted value of a given [Flow] or `defaultValue` if no value has been emitted.
 */
@ExperimentalCoroutinesApi
class FlowBindable<T> private constructor(
    private val viewModel: ViewModel,
    private val fieldId: Int,
    defaultValue: T,
    flow: Flow<T>,
    onException: OnException<T>,
    onCompletion: OnCompletion<T>,
    coroutineScope: CoroutineScope,
    distinct: Boolean,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : BindablePropertyBase<T>(distinct, afterSet, beforeSet, validate), ReadOnlyProperty<ViewModel, T> {
    private var value: T = defaultValue
        set(value) {
            if (distinct && value === field) return

            beforeSet?.invoke(field, value)
            field = when (val validate = validate) {
                null -> value
                else -> validate(field, value)
            }

            viewModel.notifyPropertyChanged(fieldId)
            afterSet?.invoke(field)
        }

    init {
        flow.onEach { value = it }
            .apply {
                onCompletion?.let { onCompletion(it) }
                onException?.let { catch(it) }
            }
            .launchIn(coroutineScope)
    }

    override fun getValue(thisRef: ViewModel, property: KProperty<*>): T = value

    /**
     * Property delegate provider for [FlowBindable].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see BindableProperty
     */
    class Provider<T>(
        private val flow: Flow<T>,
        private val onException: OnException<T>,
        private val onCompletion: OnCompletion<T>,
        private val coroutineScope: CoroutineScope,
        private val fieldId: Int? = null,
        private val defaultValue: T
    ) : BindablePropertyBase.Provider<T>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>) = FlowBindable(
            viewModel = thisRef,
            flow = flow,
            onException = onException,
            onCompletion = onCompletion,
            coroutineScope = coroutineScope,
            fieldId = fieldId ?: property.resolveFieldId(),
            defaultValue = defaultValue,
            distinct = distinct,
            afterSet = afterSet,
            beforeSet = beforeSet,
            validate = validate
        )
    }
}
