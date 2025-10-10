package lib.homies.framework.events;

/**
 * A custom event fired when a menu needs to be reloaded, typically after a configuration change.
 * Menus can subscribe to this event to refresh their contents.
 */
public class MenuReloadEvent extends LibEvent {
    // This event doesn't need any specific data, its presence is enough to signal a reload.
}
