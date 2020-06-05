package de.trbnb.mvvmbase.coroutines.flow

import kotlinx.coroutines.flow.FlowCollector

typealias OnException<T> = suspend FlowCollector<T>.(Throwable) -> Unit
typealias OnCompletion<T> = suspend FlowCollector<T>.(Throwable?) -> Unit