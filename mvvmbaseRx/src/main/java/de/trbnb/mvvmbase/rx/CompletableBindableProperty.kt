package de.trbnb.mvvmbase.rx

import de.trbnb.mvvmbase.ViewModel
import de.trbnb.mvvmbase.bindableproperty.BindablePropertyBase
import de.trbnb.mvvmbase.utils.resolveFieldId
import io.reactivex.Completable
import io.reactivex.rxkotlin.subscribeBy
import kotlin.reflect.KProperty

/**
 * BindableProperty implementation for [Completable]s.
 */
class CompletableBindableProperty internal constructor(
    private val viewModel: ViewModel,
    private var fieldId: Int?,
    completable: Completable,
    onError: (Throwable) -> Unit
) : BindablePropertyBase() {
    internal var afterSet: ((Boolean) -> Unit)? = null

    private var isCompleted = false
        set(value) {
            field = value
            fieldId?.let { viewModel.notifyPropertyChanged(it) }
            afterSet?.invoke(value)
        }

    init {
        completable.subscribeBy(onError = onError, onComplete = { isCompleted = true })
            .autoDispose(viewModel.lifecycle)
    }

    fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        if (fieldId == null) {
            fieldId = property.resolveFieldId()
        }

        return isCompleted
    }
}

/**
 * Sets [CompletableBindableProperty.afterSet] of a [CompletableBindableProperty] instance to a given function and
 * returns that instance.
 */
fun CompletableBindableProperty.afterSet(action: (Boolean) -> Unit): CompletableBindableProperty = apply {
    afterSet = action
}
