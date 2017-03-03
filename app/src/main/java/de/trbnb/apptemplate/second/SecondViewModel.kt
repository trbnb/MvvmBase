package de.trbnb.apptemplate.second

import android.databinding.Bindable
import de.trbnb.apptemplate.BR
import de.trbnb.mvvmbase.BaseViewModel

class SecondViewModel : BaseViewModel() {

    val text = "This is a fragment!"

    var progress = 0
        @Bindable get
        set(value) {
            if(field == value) return

            field = Math.min(value, 100)
            notifyPropertyChanged(BR.progress)
        }

}