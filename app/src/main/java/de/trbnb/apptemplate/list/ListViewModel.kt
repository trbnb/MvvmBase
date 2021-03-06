package de.trbnb.apptemplate.list

import androidx.databinding.Bindable
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.childrenBindable
import java.util.UUID

class ListViewModel : BaseViewModel() {
    @get:Bindable
    val items by childrenBindable(List(5) { ItemViewModel(UUID.randomUUID().toString()) })
}
