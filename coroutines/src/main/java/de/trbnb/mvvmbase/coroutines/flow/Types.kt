package de.trbnb.mvvmbase.coroutines.flow

import kotlinx.coroutines.flow.FlowCollector

public typealias OnException<T> = suspend FlowCollector<T>.(Throwable) -> Unit
public typealias OnCompletion<T> = suspend FlowCollector<T>.(Throwable?) -> Unit
