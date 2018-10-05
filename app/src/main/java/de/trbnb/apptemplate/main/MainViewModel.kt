package de.trbnb.apptemplate.main

import android.content.Context
import androidx.databinding.Bindable
import de.trbnb.apptemplate.R
import de.trbnb.apptemplate.app.App
import de.trbnb.apptemplate.second.SecondActivity
import de.trbnb.mvvmbase.BaseViewModel
import de.trbnb.mvvmbase.bindableproperty.bindable
import org.jetbrains.anko.startActivity
import javax.inject.Inject

class MainViewModel : BaseViewModel() {

    @Inject
    lateinit var context: Context

    val text: String

    @get:Bindable
    var isShowingDialog by bindable(false)

    @get:Bindable
    var isShowingSnackbar: Boolean by bindable(false)

    fun showDialog() {
        isShowingDialog = true
    }

    fun showSnackbar() {
        isShowingSnackbar = true
    }

    fun showFragmentExample() {
        context.startActivity<SecondActivity>()
    }

    fun showMainActivityAgain() {
        context.startActivity<MainActivity>()
    }

    init {
        App.appComponent.inject(this)
        text = context.getString(R.string.example_text)
    }
}
