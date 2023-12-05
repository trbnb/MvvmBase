package de.trbnb.mvvmbase.databinding.utils

import de.trbnb.mvvmbase.databinding.ViewModel

/**
 * Calls [ViewModel.destroy] on every element in the receiver collection.
 */
public fun <VM : ViewModel> Collection<VM>.destroyAll(): Unit = forEach { it.destroy() }
