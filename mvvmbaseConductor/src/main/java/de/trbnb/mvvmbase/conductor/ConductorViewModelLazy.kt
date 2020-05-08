package de.trbnb.mvvmbase.conductor

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.bluelinelabs.conductor.Controller

@MainThread
inline fun <reified VM : ViewModel> Controller.activityViewModels(
    noinline factoryProducer: () -> ViewModelProvider.Factory
) = ViewModelLazy(
    viewModelClass = VM::class,
    storeProducer = { (activity as ViewModelStoreOwner).viewModelStore },
    factoryProducer = factoryProducer
)
