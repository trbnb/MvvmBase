package de.trbnb.mvvmbase

/**
 * Describes an annotated property as observable.
 * Its getter may return a different value than before if another property has changed.
 *
 * @param value names of the dependency properties
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DependsOn(vararg val value: String)
