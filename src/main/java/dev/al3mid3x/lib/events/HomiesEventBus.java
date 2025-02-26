package dev.al3mid3x.lib.events;

import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class HomiesEventBus {
    private final Map<Class<? extends LibEvent>, List<Consumer<? extends LibEvent>>> listeners = Maps.newHashMap();

    public <T extends LibEvent> void registerListener(Class<T> eventClass, Consumer<T> listener) {
        listeners.computeIfAbsent(eventClass, k -> new ArrayList<>()).add(listener);
    }

    public <T extends LibEvent> void fireEvent(T event) {
        List<Consumer<? extends LibEvent>> eventListeners = listeners.get(event.getClass());
        if (eventListeners != null) {
            for (Consumer<? extends LibEvent> listener : eventListeners) {
                ((Consumer<T>) listener).accept(event);
            }
        }
    }
}