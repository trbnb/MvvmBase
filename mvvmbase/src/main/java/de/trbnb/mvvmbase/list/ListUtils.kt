package de.trbnb.mvvmbase.list

import de.trbnb.mvvmbase.ViewModel

fun <VM : ViewModel> List<VM>.onDestroy() = forEach { it.onDestroy() }
