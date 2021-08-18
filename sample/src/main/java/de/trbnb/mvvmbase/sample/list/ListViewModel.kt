package de.trbnb.mvvmbase.sample.list

import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.observableproperty.observable
import java.util.UUID

class ListViewModel : BaseViewModel() {
    val items by observable(List(5) { Item(text = UUID.randomUUID().toString()) })
}
