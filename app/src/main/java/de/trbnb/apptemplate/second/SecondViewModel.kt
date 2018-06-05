package de.trbnb.apptemplate.second

import android.databinding.Bindable
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.bindableInt
import de.trbnb.mvvmbase.bindableproperty.distinct
import de.trbnb.mvvmbase.bindableproperty.validate

class SecondViewModel : BaseViewModel() {

    val text = "This is a fragment!"

    @get:Bindable
    var progress by bindableInt()
            .distinct()
            .validate { _, new -> Math.min(new, 100) }

}
