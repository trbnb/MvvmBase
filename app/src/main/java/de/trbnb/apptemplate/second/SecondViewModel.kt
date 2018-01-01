package de.trbnb.apptemplate.second

import android.databinding.Bindable
import de.trbnb.apptemplate.BR
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindable
import de.trbnb.mvvmbase.distinct
import de.trbnb.mvvmbase.validate

class SecondViewModel : BaseViewModel() {

    val text = "This is a fragment!"

    @get:Bindable
    var progress by bindable(BR.progress)
            .distinct()
            .validate { _, new -> Math.min(new, 100) }

}