package lib.homies.framework.events;

import java.util.function.Consumer;

/**
 * A platform-agnostic interface for an event bus.
 * This service allows framework developers to subscribe to and call custom {@link LibEvent}s,
 * as well as listen to native platform events (e.g., Bukkit events).
 */
public interface EventBus {

    /**
     * Subscribes a handler to a custom {@link LibEvent}.
     * When an event of the specified class is {@link #call(LibEvent) called},
     * the provided handler will be executed.
     *
     * @param eventClass The class of the custom event to listen for.
     * @param handler The {@link Consumer} that will handle the event.
     * @param <E> The type of the {@link LibEvent}.
     */
    <E extends LibEvent> void subscribe(Class<E> eventClass, Consumer<E> handler);

    /**
     * Calls a custom {@link LibEvent}, triggering all subscribed handlers for that event type.
     *
     * @param event The {@link LibEvent} object to call.
     */
    void call(LibEvent event);

    /**
     * Subscribes a handler to a native platform event (e.g., a Bukkit event, BungeeCord event).
     * The underlying implementation will handle the platform-specific registration of the listener.
     *
     * @param platformEventClass The class of the platform event to listen for.
     * @param handler The {@link Consumer} that will handle the event.
     * @param <T> The type of the platform event.
     */
    <T> void subscribePlatform(Class<T> platformEventClass, Consumer<T> handler);
}
