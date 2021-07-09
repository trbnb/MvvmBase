package de.trbnb.mvvmbase.observable

/**
 * A utility for storing and notifying callbacks. This class supports reentrant modification
 * of the callbacks during notification without adversely disrupting notifications.
 * A common pattern for callbacks is to receive a notification and then remove
 * themselves. This class handles this behavior with constant memory under
 * most circumstances.
 *
 *
 * A subclass of [CallbackRegistry.NotifierCallback] must be passed to
 * the constructor to define how notifications should be called. That implementation
 * does the actual notification on the listener. It is typically a static instance
 * that can be reused for all similar CallbackRegistries.
 *
 *
 * This class supports only callbacks with at most three parameters.
 * Typically, these are the notification originator and a parameter, with another to
 * indicate which method to call, but these may be used as required. If more than
 * three parameters are required or primitive types other than the single int provided
 * must be used, `A` should be some kind of containing structure that
 * the subclass may reuse between notifications.
 *
 * Creates an EventRegistry that notifies the event with notifier.
 *
 * @param <C> The callback type.
 * @param <T> The notification sender type. Typically this is the containing class.
 * @param <A> Opaque argument used to pass additional data beyond an int.
 * @param notifier The notification mechanism for notifying an event.
 */
internal open class CallbackRegistry<C, T, A> (private val notifier: NotifierCallback<C, T, A>) : Cloneable {
    /** An ordered collection of listeners waiting to be notified.  */
    private val callbacks: MutableList<C> = mutableListOf()

    /**
     * A bit flag for the first 64 listeners that are removed during notification.
     * The lowest significant bit corresponds to the 0th index into mCallbacks.
     * For a small number of callbacks, no additional array of objects needs to
     * be allocated.
     */
    private var first64Removed: Long = 0x0

    /**
     * Bit flags for the remaining callbacks that are removed during notification.
     * When there are more than 64 callbacks and one is marked for removal, a dynamic
     * array of bits are allocated for the callbacks.
     */
    private var remainderRemoved: LongArray = longArrayOf()

    /** The recursion level of the notification  */
    private var notificationLevel = 0

    /**
     * Notify all callbacks.
     *
     * @param sender The originator. This is an opaque parameter passed to [CallbackRegistry.NotifierCallback.onNotifyCallback]
     * @param arg An opaque parameter passed to [CallbackRegistry.NotifierCallback.onNotifyCallback]
     */
    @Synchronized
    fun notifyCallbacks(sender: T, arg: A) {
        notificationLevel++
        notifyRecurse(sender, arg)
        notificationLevel--
        if (notificationLevel == 0) {
            for (i in remainderRemoved.indices.reversed()) {
                val removedBits = remainderRemoved[i]
                if (removedBits != 0L) {
                    removeRemovedCallbacks((i + 1) * Long.SIZE_BITS, removedBits)
                    remainderRemoved[i] = 0
                }
            }

            if (first64Removed != 0L) {
                removeRemovedCallbacks(0, first64Removed)
                first64Removed = 0
            }
        }
    }

    /**
     * Notify up to the first Long.SIZE callbacks that don't have a bit set in `removed`.
     *
     * @param sender The originator. This is an opaque parameter passed to [CallbackRegistry.NotifierCallback.onNotifyCallback]
     * @param arg An opaque parameter passed to [CallbackRegistry.NotifierCallback.onNotifyCallback]
     */
    private fun notifyFirst64(sender: T, arg: A) {
        val maxNotified = Long.SIZE_BITS.coerceAtMost(callbacks.size)
        notifyCallbacks(sender, arg, 0, maxNotified, first64Removed)
    }

    /**
     * Notify all callbacks using a recursive algorithm to avoid allocating on the heap.
     * This part captures the callbacks beyond Long.SIZE that have no bits allocated for
     * removal before it recurses into [.notifyRemainder].
     *
     *
     * Recursion is used to avoid allocating temporary state on the heap.
     *
     * @param sender The originator. This is an opaque parameter passed to [CallbackRegistry.NotifierCallback.onNotifyCallback]
     * @param arg An opaque parameter passed to [CallbackRegistry.NotifierCallback.onNotifyCallback]
     */
    private fun notifyRecurse(sender: T, arg: A) {
        val callbackCount = callbacks.size
        val remainderIndex = remainderRemoved.lastIndex

        // Now we've got all callbakcs that have no mRemainderRemoved value, so notify the
        // others.
        notifyRemainder(sender, arg, remainderIndex)

        // notifyRemainder notifies all at maxIndex, so we'd normally start at maxIndex + 1
        // However, we must also keep track of those in mFirst64Removed, so we add 2 instead:
        val startCallbackIndex = (remainderIndex + 2) * Long.SIZE_BITS

        // The remaining have no bit set
        notifyCallbacks(sender, arg, startCallbackIndex, callbackCount, 0)
    }

    /**
     * Notify callbacks that have mRemainderRemoved bits set for remainderIndex. If
     * remainderIndex is -1, the first 64 will be notified instead.
     *
     * @param sender The originator. This is an opaque parameter passed to [CallbackRegistry.NotifierCallback.onNotifyCallback]
     * @param arg An opaque parameter passed to [CallbackRegistry.NotifierCallback.onNotifyCallback]
     * @param remainderIndex The index into mRemainderRemoved that should be notified.
     */
    private fun notifyRemainder(sender: T, arg: A, remainderIndex: Int) {
        if (remainderIndex < 0) {
            notifyFirst64(sender, arg)
        } else {
            val bits = remainderRemoved[remainderIndex]
            val startIndex = (remainderIndex + 1) * Long.SIZE_BITS
            val endIndex = callbacks.size.coerceAtMost(startIndex + Long.SIZE_BITS)
            notifyRemainder(sender, arg, remainderIndex - 1)
            notifyCallbacks(sender, arg, startIndex, endIndex, bits)
        }
    }

    /**
     * Notify callbacks from startIndex to endIndex, using bits as the bit status
     * for whether they have been removed or not. bits should be from mRemainderRemoved or
     * mFirst64Removed. bits set to 0 indicates that all callbacks from startIndex to
     * endIndex should be notified.
     *
     * @param sender The originator. This is an opaque parameter passed to
     * [CallbackRegistry.NotifierCallback.onNotifyCallback]
     * @param arg An opaque parameter passed to
     * [CallbackRegistry.NotifierCallback.onNotifyCallback]
     * @param arg2 An opaque parameter passed to
     * [CallbackRegistry.NotifierCallback.onNotifyCallback]
     * @param startIndex The index into the mCallbacks to start notifying.
     * @param endIndex One past the last index into mCallbacks to notify.
     * @param bits A bit field indicating which callbacks have been removed and shouldn't
     * be notified.
     */
    private fun notifyCallbacks(sender: T, arg: A, startIndex: Int, endIndex: Int, bits: Long) {
        var bitMask: Long = 1
        for (i in startIndex until endIndex) {
            if (bits and bitMask == 0L) {
                notifier.onNotifyCallback(callbacks[i], sender, arg)
            }
            bitMask = bitMask shl 1
        }
    }

    /**
     * Add a callback to be notified. If the callback is already in the list, another won't
     * be added. This does not affect current notifications.
     * @param callback The callback to add.
     */
    @Synchronized
    fun add(callback: C) {
        val index = callbacks.lastIndexOf(callback)
        if (index < 0 || isRemoved(index)) {
            callbacks.add(callback)
        }
    }

    /**
     * Returns true if the callback at index has been marked for removal.
     *
     * @param index The index into mCallbacks to check.
     * @return true if the callback at index has been marked for removal.
     */
    private fun isRemoved(index: Int): Boolean {
        return if (index < Long.SIZE_BITS) {
            // It is in the first 64 callbacks, just check the bit.
            val bitMask = 1L shl index
            first64Removed and bitMask != 0L
        } else {
            val maskIndex = index / Long.SIZE_BITS - 1
            if (maskIndex >= remainderRemoved.size) {
                // There are some items in mRemainderRemoved, but nothing at the given index.
                false
            } else {
                // There is something marked for removal, so we have to check the bit.
                val bits = remainderRemoved[maskIndex]
                val bitMask = 1L shl index % Long.SIZE_BITS
                bits and bitMask != 0L
            }
        }
    }

    /**
     * Removes callbacks from startIndex to startIndex + Long.SIZE, based
     * on the bits set in removed.
     *
     * @param startIndex The index into the mCallbacks to start removing callbacks.
     * @param removed The bits indicating removal, where each bit is set for one callback
     * to be removed.
     */
    private fun removeRemovedCallbacks(startIndex: Int, removed: Long) {
        // The naive approach should be fine. There may be a better bit-twiddling approach.
        val endIndex = startIndex + Long.SIZE_BITS
        var bitMask = 1L shl Long.SIZE_BITS - 1
        for (i in endIndex - 1 downTo startIndex) {
            if (removed and bitMask != 0L) {
                callbacks.removeAt(i)
            }
            bitMask = bitMask ushr 1
        }
    }

    /**
     * Remove a callback. This callback won't be notified after this call completes.
     *
     * @param callback The callback to remove.
     */
    @Synchronized
    fun remove(callback: C) {
        if (notificationLevel == 0) {
            callbacks.remove(callback)
        } else {
            val index = callbacks.lastIndexOf(callback)
            if (index >= 0) {
                setRemovalBit(index)
            }
        }
    }

    private fun setRemovalBit(index: Int) {
        if (index < Long.SIZE_BITS) {
            // It is in the first 64 callbacks, just check the bit.
            val bitMask = 1L shl index
            first64Removed = first64Removed or bitMask
        } else {
            val remainderIndex = index / Long.SIZE_BITS - 1
            if (remainderRemoved.size <= remainderIndex) {
                // need to make it bigger
                val newRemainders = LongArray(callbacks.size / Long.SIZE_BITS)
                System.arraycopy(remainderRemoved, 0, newRemainders, 0, remainderRemoved.size)
                remainderRemoved = newRemainders
            }
            val bitMask = 1L shl index % Long.SIZE_BITS
            remainderRemoved[remainderIndex] = remainderRemoved[remainderIndex] or bitMask
        }
    }

    /**
     * Returns true if there are no registered callbacks or false otherwise.
     *
     * @return true if there are no registered callbacks or false otherwise.
     */
    @get:Synchronized
    val isEmpty: Boolean
        get() {
            return when {
                callbacks.isEmpty() -> true
                notificationLevel == 0 -> false
                else -> {
                    callbacks.forEachIndexed { i, _ ->
                        if (!isRemoved(i)) {
                            return false
                        }
                    }
                    true
                }
            }
        }

    /**
     * Removes all callbacks from the list.
     */
    @Synchronized
    fun clear() {
        if (notificationLevel == 0) {
            callbacks.clear()
        } else if (callbacks.isNotEmpty()) {
            for (i in callbacks.indices.reversed()) {
                setRemovalBit(i)
            }
        }
    }

    /**
     * Class used to notify events from CallbackRegistry.
     *
     * @param <C> The callback type.
     * @param <T> The notification sender type. Typically this is the containing class.
     * @param <A> An opaque argument to pass to the notifier
    </A></T></C> */
    abstract class NotifierCallback<C, T, A> {
        /**
         * Called by CallbackRegistry during
         * [CallbackRegistry.notifyCallbacks]} to notify the callback.
         *
         * @param callback The callback to notify.
         * @param sender The opaque sender object.
         * @param arg The opaque notification parameter.
         * [CallbackRegistry.notifyCallbacks]
         * @see CallbackRegistry
         */
        abstract fun onNotifyCallback(callback: C, sender: T, arg: A)
    }
}
