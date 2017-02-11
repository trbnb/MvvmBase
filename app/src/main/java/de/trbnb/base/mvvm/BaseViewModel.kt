package de.trbnb.base.mvvm

import android.databinding.BaseObservable

abstract class BaseViewModel : BaseObservable(), ViewModel {

    override fun onDestroy() {

    }

    override fun onViewFinishing() {

    }

}