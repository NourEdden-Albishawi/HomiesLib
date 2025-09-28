package lib.homies.framework.spigot.events;

import lib.homies.framework.events.EventBus;
import lib.homies.framework.events.LibEvent;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.logging.Level;

/**
 * Spigot-specific implementation of the {@link EventBus} interface.
 * This class handles subscribing to and calling custom {@link LibEvent}s,
 * as well as registering and dispatching native Bukkit {@link Event}s.
 */
public class SpigotEventBus implements EventBus {

    private final Plugin plugin;
    // Stores handlers for both custom LibEvents and platform events
    private final Map<Class<?>, List<Consumer<?>>> customEventListeners = new ConcurrentHashMap<>();
    // Tracks which Bukkit event classes have had a generic listener registered with Bukkit's PluginManager
    private final Set<Class<?>> registeredBukkitEvents = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Constructs a new SpigotEventBus.
     * @param plugin The {@link Plugin} instance of the framework, used for registering Bukkit listeners.
     */
    public SpigotEventBus(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Subscribes a handler to a custom {@link LibEvent}.
     * The handler will be called when {@link #call(LibEvent)} is invoked for an event of the specified type.
     * @param eventClass The class of the custom event to listen for.
     * @param handler The {@link Consumer} that will handle the event.
     * @param <E> The type of the {@link LibEvent}.
     */
    @Override
    public <E extends LibEvent> void subscribe(Class<E> eventClass, Consumer<E> handler) {
        customEventListeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(handler);
    }

    /**
     * Calls a custom {@link LibEvent}, triggering all subscribed handlers for that event type.
     * @param event The {@link LibEvent} object to call.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void call(LibEvent event) {
        List<Consumer<?>> handlers = customEventListeners.get(event.getClass());
        if (handlers != null) {
            for (Consumer<?> handler : handlers) {
                ((Consumer<LibEvent>) handler).accept(event);
            }
        }
    }

    /**
     * Subscribes a handler to a native Bukkit {@link Event}.
     * A generic Bukkit {@link Listener} will be registered once per event class.
     * When the Bukkit event fires, all subscribed handlers for that event type will be called.
     * @param platformEventClass The class of the Bukkit event.
     * @param handler The {@link Consumer} that will handle the event.
     * @param <T> The type of the platform event.
     */
    @Override
    public <T> void subscribePlatform(Class<T> platformEventClass, Consumer<T> handler) {
        // Ensure the event is a Bukkit event
        if (!Event.class.isAssignableFrom(platformEventClass)) {
            plugin.getLogger().warning("Attempted to subscribe to a non-Bukkit event via subscribePlatform: " + platformEventClass.getName());
            return;
        }

        // Add the specific handler to our internal list
        customEventListeners.computeIfAbsent(platformEventClass, k -> new ArrayList<>()).add(handler);

        // Register a generic listener for this event type with Bukkit's PluginManager if we haven't already
        if (registeredBukkitEvents.add(platformEventClass)) {
            plugin.getServer().getPluginManager().registerEvent(
                    (Class<Event>) platformEventClass,
                    new Listener() {},
                    EventPriority.NORMAL,
                    (listener, event) -> {
                        // When the Bukkit event fires, call all registered handlers for it
                        callPlatformHandlers(event);
                    },
                    plugin,
                    false
            );
        }
    }

    /**
     * Dispatches a native platform event to all internal handlers subscribed to its type.
     * @param event The native platform event to dispatch.
     * @param <T> The type of the platform event.
     */
    @SuppressWarnings("unchecked")
    private <T> void callPlatformHandlers(T event) {
        List<Consumer<?>> handlers = customEventListeners.get(event.getClass());
        if (handlers != null) {
            for (Consumer<?> handler : handlers) {
                try {
                    ((Consumer<T>) handler).accept(event);
                } catch (ClassCastException e) {
                    plugin.getLogger().log(Level.SEVERE, "Error casting event handler for " + event.getClass().getName(), e);
                }
            }
        }
    }
}
