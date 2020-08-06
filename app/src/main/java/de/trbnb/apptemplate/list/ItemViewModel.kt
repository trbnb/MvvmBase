package de.trbnb.apptemplate.list

import de.trbnb.mvvmbase.BaseViewModel

class ItemViewModel(val text: String) : BaseViewModel() {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is ItemViewModel) return false

        return other.text == text
    }

    override fun hashCode() = text.hashCode()
}