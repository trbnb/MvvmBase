package de.trbnb.mvvmbase

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DependsOn(vararg val value: String)