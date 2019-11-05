package de.trbnb.mvvmbase.list

import de.trbnb.mvvmbase.ViewModel

/**
 * Calls [ViewModel.onDestroy] on every element in the receiver list.
 */
fun <VM : ViewModel> List<VM>.onDestroy() = forEach { it.onDestroy() }
