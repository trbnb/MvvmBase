package de.trbnb.mvvmbase.databinding.list

import de.trbnb.mvvmbase.databinding.ViewModel

/**
 * Calls [ViewModel.destroy] on every element in the receiver collection.
 */
fun <VM : ViewModel> Collection<VM>.destroyAll() = forEach { it.destroy() }
