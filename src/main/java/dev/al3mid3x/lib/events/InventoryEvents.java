package dev.al3mid3x.lib.events;

import dev.al3mid3x.lib.HomiesLib;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public class InventoryEvents implements Listener
{
    private final HomiesLib plugin;
    public InventoryEvents(HomiesLib plugin){
        this.plugin = plugin;
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClickEvent(InventoryClickEvent event) {
        plugin.getMenuManager().getAll().forEach(m -> {
            if(!event.getInventory().equals(m.getInventory())) return;
            m.consume(event);
        });
    }
}
