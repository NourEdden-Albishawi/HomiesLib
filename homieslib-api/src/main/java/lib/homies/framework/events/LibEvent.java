package lib.homies.framework.events;

/**
 * The base class for all custom, platform-agnostic events within the HomiesLib framework.
 * Extend this class to create your own custom events that can be called and subscribed to
 * via the {@link EventBus}.
 */
public abstract class LibEvent {
    private boolean cancelled = false;

    /**
     * Checks if this event has been cancelled.
     * @return {@code true} if the event is cancelled, {@code false} otherwise.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation state of this event.
     * @param cancelled {@code true} to cancel the event, {@code false} to uncancel it.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
