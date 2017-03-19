package de.trbnb.mvvmbase

/**
 * Singleton that is used to generate loader IDs for [MvvmFragment]s.
 *
 * A new ID can be generated and will be returned by [generate].
 * The range of possible IDs is the range of [Int] minus one. A single value of the [Int] range is
 * excluded so it can be used as "not-an-ID" value.
 *
 * If the last value of the range has been generated it will be returned for the rest
 * of the runtimes life. This behaviour can be ignored however due to the fact that this will
 * practically never happen.
 */
internal object LoaderIdGenerator {

    /**
     * A "not-an-ID" value that will not be returned by [generate].
     */
    const val NO_ID = Int.MIN_VALUE

    /**
     * An [Iterator] that iterates through the range of possible IDs.
     */
    private val rangeIterator = (Int.MIN_VALUE + 1 .. Int.MAX_VALUE).iterator()

    /**
     * Generates a new ID that has never been returned before.
     *
     * @return The new ID.
     */
    fun generate(): Int = rangeIterator.nextInt()

}