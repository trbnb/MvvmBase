package de.trbnb.mvvmbase.sample.main

import de.trbnb.mvvmbase.events.Event

sealed class MainEvent : Event {
    class ShowToast(val text: String) : MainEvent()
}
