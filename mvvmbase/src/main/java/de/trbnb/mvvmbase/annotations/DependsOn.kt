package de.trbnb.mvvmbase.annotations

/**
 * Marks ViewModel properties as dependent on other properties.
 * Everytime those other properties change `notifyPropertyChanged` should be called for the
 * marked property.
 *
 * This should be supported by every ViewModel implementation.
 * BaseViewModel supports this by default.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DependsOn(vararg val value: Int)
