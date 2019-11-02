package de.trbnb.apptemplate.second

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableInt
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.bindableproperty.validate

class SecondViewModel : BaseViewModel() {
    @get:Bindable
    var text by bindable("This is a fragment!")

    @get:Bindable
    var progress by bindableInt()
        .distinct()
        .validate { _, new -> new.coerceAtMost(100) }

    override fun onRestore(savedStateHandle: SavedStateHandle) {
        super.onRestore(savedStateHandle)
        val key = "foo"
        if (key in savedStateHandle) {
            text = "Restored!"
        }
        savedStateHandle[key] = true
    }
}
