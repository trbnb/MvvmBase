package de.trbnb.apptemplate.main

import de.trbnb.mvvmbase.events.Event

sealed class MainEvent : Event {
    object ShowToast : MainEvent()
    object ShowSecondActivityEvent : MainEvent()
    object ShowMainActivityAgainEvent : MainEvent()
    object ShowConductorEvent : MainEvent()
    object ShowListEvent : MainEvent()
    object ShowDialog : MainEvent()
    class ShowSnackbar(val text: String) : MainEvent()
}
