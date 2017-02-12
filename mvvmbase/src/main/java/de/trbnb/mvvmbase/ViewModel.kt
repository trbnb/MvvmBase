package de.trbnb.mvvmbase

import android.databinding.Observable

interface ViewModel : Observable {
    fun onDestroy()
    fun onViewFinishing()
}