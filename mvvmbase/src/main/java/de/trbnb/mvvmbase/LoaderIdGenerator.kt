package de.trbnb.mvvmbase

internal object LoaderIdGenerator {

    const val NO_ID = Int.MIN_VALUE

    private val idRange = (Int.MIN_VALUE + 1 .. Int.MAX_VALUE).iterator()

    fun generate(): Int = idRange.nextInt()

}