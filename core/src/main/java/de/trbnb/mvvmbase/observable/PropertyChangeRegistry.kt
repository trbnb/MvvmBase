package de.trbnb.mvvmbase.observable

import de.trbnb.mvvmbase.OnPropertyChangedCallback

internal class PropertyChangeRegistry(
    private val dependencyPairs: List<Pair<String, Array<out String>>>
) : CallbackRegistry<OnPropertyChangedCallback, ObservableContainer, String>(
    object : NotifierCallback<OnPropertyChangedCallback, ObservableContainer, String>() {
        override fun onNotifyCallback(callback: OnPropertyChangedCallback, sender: ObservableContainer, arg: String) {
            callback.onPropertyChanged(sender, arg)
        }
    }
) {
    fun notifyChange(sender: ObservableContainer, propertyName: String) {
        notifyCallbacks(sender, propertyName)

        dependencyPairs.forEach { (dependentProperty, source) ->
            if (propertyName in source) {
                notifyChange(sender, dependentProperty)
                return@forEach
            }
        }
    }
}
