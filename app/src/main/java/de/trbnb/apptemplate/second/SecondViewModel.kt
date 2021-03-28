package de.trbnb.apptemplate.second

import androidx.lifecycle.SavedStateHandle
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.resource.ResourceProvider
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableInt
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.bindableproperty.validate
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel

class SecondViewModel(
    savedStateHandle: SavedStateHandle,
    resourceProvider: ResourceProvider
) : BaseStateSavingViewModel(savedStateHandle) {
    var text by bindable(resourceProvider.getString(R.string.not_restored))

    var progress by bindableInt()
        .distinct()
        .validate { _, new -> new.coerceAtMost(100) }

    init {
        val key = "restored"
        if (key in savedStateHandle) {
            text = resourceProvider.getString(R.string.restored)
        }
        savedStateHandle[key] = true
    }
}
