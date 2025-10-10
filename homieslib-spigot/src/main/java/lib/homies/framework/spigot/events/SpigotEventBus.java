package lib.homies.framework.spigot.events;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lib.homies.framework.events.EventBus;
import lib.homies.framework.events.LibEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;

public class SpigotEventBus implements EventBus {

    private final Plugin plugin;
    private final Multimap<Class<? extends LibEvent>, Consumer<LibEvent>> customEventSubscribers = HashMultimap.create();

    // New implementation for platform events
    private final Multimap<Class<? extends Event>, Consumer<? extends Event>> platformSubscribers = HashMultimap.create();
    private final Set<Class<? extends Event>> registeredBukkitListeners = new HashSet<>();

    public SpigotEventBus(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E extends LibEvent> void subscribe(Class<E> eventClass, Consumer<E> handler) {
        customEventSubscribers.put(eventClass, (Consumer<LibEvent>) handler);
    }

    @Override
    public void call(LibEvent event) {
        customEventSubscribers.get(event.getClass()).forEach(handler -> handler.accept(event));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void subscribePlatform(Class<T> platformEventClass, Consumer<T> handler) {
        if (!Event.class.isAssignableFrom(platformEventClass)) {
            plugin.getLogger().warning("Attempted to subscribe to a non-Bukkit event via subscribePlatform: " + platformEventClass.getName());
            return;
        }

        Class<? extends Event> eventClass = (Class<? extends Event>) platformEventClass;
        platformSubscribers.put(eventClass, (Consumer<? extends Event>) handler);

        // Register a master listener for this event type only if we haven't already.
        // This is more efficient than registering a listener for every single subscription.
        if (registeredBukkitListeners.add(eventClass)) {
            Bukkit.getPluginManager().registerEvent(
                    eventClass,
                    new Listener() {},
                    org.bukkit.event.EventPriority.NORMAL,
                    this::dispatchEvent,
                    plugin,
                    false
            );
        }
    }

    /**
     * Dispatches a received Bukkit event to all registered consumers, respecting event inheritance.
     *
     * @param listener The dummy listener registered with Bukkit.
     * @param event The event that was fired.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void dispatchEvent(Listener listener, Event event) {
        Class<?> eventClass = event.getClass();

        while (eventClass != null && Event.class.isAssignableFrom(eventClass)) {
            Collection<Consumer<? extends Event>> consumers = platformSubscribers.get((Class<? extends Event>) eventClass);

            for (Consumer consumer : consumers) {
                try {
                    consumer.accept(event);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Error passing event " + event.getEventName() + " to consumer " + consumer.getClass().getName(), e);
                }
            }
            eventClass = eventClass.getSuperclass();
        }
    }


    @Override
    public void register(Object listener) {
        plugin.getLogger().warning("The register(Object listener) method is deprecated and does nothing. Please register event handlers manually.");
    }
}
