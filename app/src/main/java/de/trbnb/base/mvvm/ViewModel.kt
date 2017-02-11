package de.trbnb.base.mvvm

import android.databinding.Observable

interface ViewModel : Observable{
    fun onDestroy()
    fun onViewFinishing()
}