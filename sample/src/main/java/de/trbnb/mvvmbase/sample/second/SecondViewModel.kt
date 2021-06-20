package de.trbnb.mvvmbase.sample.second

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.bindableproperty.validate
import de.trbnb.mvvmbase.sample.R
import de.trbnb.mvvmbase.sample.app.resource.ResourceProvider
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel
import javax.inject.Inject

@HiltViewModel
class SecondViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    resourceProvider: ResourceProvider
) : BaseStateSavingViewModel(savedStateHandle) {
    var text by bindable(resourceProvider.getString(R.string.not_restored))

    var progress by bindable(0)
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
