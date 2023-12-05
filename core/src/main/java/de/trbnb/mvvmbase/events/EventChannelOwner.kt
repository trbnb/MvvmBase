package de.trbnb.mvvmbase.events

/**
 * Marker interface for objects that contain an [EventChannel]
 */
public interface EventChannelOwner {
    /**
     * Gets an EventChannel that can be used for sending one-time events.
     */
    public val eventChannel: EventChannel
}
