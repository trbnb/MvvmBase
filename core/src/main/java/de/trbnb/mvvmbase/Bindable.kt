package de.trbnb.mvvmbase

@Target(AnnotationTarget.PROPERTY_GETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Bindable(vararg val value: String)