package de.trbnb.kotlindaggerdatabindingtemplate

import android.databinding.BaseObservable
import android.databinding.Bindable
import javax.inject.Inject

/**
 * Created by Thorben on 13.07.2016.
 */
class MainViewModel : BaseObservable(){

    @Inject
    lateinit var api: API

    var text: String = "Loading..."
        @Bindable get
        set(value) {
            field = value
            notifyPropertyChanged(BR.text)
        }

    init {
        App.instance.component.inject(this)

        api.loadData {
            text = it.joinToString(separator = "\n")
        }
    }

}