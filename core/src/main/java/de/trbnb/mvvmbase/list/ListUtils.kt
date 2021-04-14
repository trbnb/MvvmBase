package de.trbnb.mvvmbase.list

import de.trbnb.mvvmbase.ViewModel

/**
 * Calls [ViewModel.destroy] on every element in the receiver collection.
 */
fun <VM : ViewModel> Collection<VM>.destroyAll() = forEach { it.destroy() }
