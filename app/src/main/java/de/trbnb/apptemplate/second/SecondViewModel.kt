package de.trbnb.apptemplate.second

import androidx.databinding.Bindable
import androidx.lifecycle.SavedStateHandle
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.resource.ResourceProvider
import de.trbnb.mvvmbase.bindableproperty.bindable
import de.trbnb.mvvmbase.bindableproperty.bindableInt
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.bindableproperty.validate
import de.trbnb.mvvmbase.savedstate.BaseStateSavingViewModel

@Suppress("UndocumentedPublicClass")
class SecondViewModel @AssistedInject constructor(
    resourceProvider: ResourceProvider,
    @Assisted savedStateHandle: SavedStateHandle
) : BaseStateSavingViewModel(savedStateHandle) {
    @Suppress("UndocumentedPublicProperty")
    @get:Bindable
    var text by bindable(resourceProvider.getString(R.string.not_restored))

    @Suppress("UndocumentedPublicProperty", "MagicNumber")
    @get:Bindable
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

    @Suppress("UndocumentedPublicClass")
    @AssistedInject.Factory
    interface Factory {
        @Suppress("UndocumentedPublicFunction")
        operator fun invoke(savedStateHandle: SavedStateHandle): SecondViewModel
    }
}
