package de.trbnb.mvvmbase.coroutines.flow

import de.trbnb.mvvmbase.databinding.ViewModel
import de.trbnb.mvvmbase.databinding.bindableproperty.AfterSet
import de.trbnb.mvvmbase.databinding.bindableproperty.BeforeSet
import de.trbnb.mvvmbase.databinding.bindableproperty.BindableProperty
import de.trbnb.mvvmbase.databinding.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.databinding.bindableproperty.Validate
import de.trbnb.mvvmbase.databinding.utils.resolveFieldId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Bindable delegate property that collects the emitted values of a given [Flow] and uses them for [getValue].
 * Uses `defaultValue` if no value has been emitted.
 */
@ExperimentalCoroutinesApi
public class FlowBindable<T> private constructor(
    private val viewModel: ViewModel,
    private val fieldId: Int,
    defaultValue: T,
    flow: Flow<T>,
    onException: OnException<T>?,
    onCompletion: OnCompletion<T>?,
    coroutineScope: CoroutineScope,
    distinct: Boolean,
    afterSet: AfterSet<T>?,
    beforeSet: BeforeSet<T>?,
    validate: Validate<T>?
) : BindablePropertyBase<T>(distinct, afterSet, beforeSet, validate), ReadOnlyProperty<ViewModel, T> {
    private var value: T = defaultValue
        set(value) {
            if (distinct && value === field) return

            val oldValue = field
            beforeSet?.invoke(oldValue, value)
            field = when (val validate = validate) {
                null -> value
                else -> validate(oldValue, value)
            }

            viewModel.notifyPropertyChanged(fieldId)
            afterSet?.invoke(oldValue, field)
        }

    init {
        flow.onEach { value = it }
            .run { onCompletion(onCompletion ?: return@run this) }
            .run { catch(onException ?: return@run this) }
            .launchIn(coroutineScope)
    }

    override fun getValue(thisRef: ViewModel, property: KProperty<*>): T = value

    /**
     * Property delegate provider for [FlowBindable].
     * Needed so that reflection via [KProperty] is only necessary once, during delegate initialization.
     *
     * @see BindableProperty
     */
    public class Provider<T>(
        private val flow: Flow<T>,
        private val onException: OnException<T>?,
        private val onCompletion: OnCompletion<T>?,
        private val coroutineScope: CoroutineScope,
        private val defaultValue: T
    ) : BindablePropertyBase.Provider<ViewModel, T>() {
        override operator fun provideDelegate(thisRef: ViewModel, property: KProperty<*>): FlowBindable<T> = FlowBindable(
            viewModel = thisRef,
            flow = flow,
            fieldId = property.resolveFieldId(),
            onException = onException,
            onCompletion = onCompletion,
            coroutineScope = coroutineScope,
            defaultValue = defaultValue,
            distinct = distinct,
            afterSet = afterSet,
            beforeSet = beforeSet,
            validate = validate
        )
    }
}
