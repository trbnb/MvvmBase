package de.trbnb.mvvmbase.sample.list

import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.bindable
import java.util.UUID

class ListViewModel : BaseViewModel() {
    val items by bindable(List(5) { Item(text = UUID.randomUUID().toString()) })
}
