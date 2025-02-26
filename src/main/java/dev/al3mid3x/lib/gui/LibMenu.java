package dev.al3mid3x.lib.gui;

import dev.al3mid3x.lib.HomiesLib;
import dev.al3mid3x.lib.events.HomiesEventBus;
import dev.al3mid3x.lib.events.menus.MenuClickEvent;
import dev.al3mid3x.lib.events.menus.MenuCloseEvent;
import dev.al3mid3x.lib.events.menus.MenuOpenEvent;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

@Getter
public abstract class LibMenu implements Listener {
    private final UUID uniqueId = UUID.randomUUID();
    private final Inventory inventory;
    private final String name;
    private Player playerWhoOpenedInventory;
    private int page = 0;
    private final HomiesEventBus eventBus;

    protected abstract int getMaxItemsPerPage();

    protected abstract boolean isPaginated();

    public abstract List<LibItemMenu> getItems();

    public LibMenu(String name, int size, HomiesEventBus eventBus) {
        this.name = name;
        this.inventory = Bukkit.createInventory(null, size, name);
        this.eventBus = eventBus;
        Bukkit.getPluginManager().registerEvents(this, HomiesLib.getPlugin(HomiesLib.class));
    }

    public void open(Player player) {
        this.playerWhoOpenedInventory = player;
        eventBus.fireEvent(new MenuOpenEvent(player, this));
        player.openInventory(inventory);
    }

    public void setItem(int slot, ItemStack item) {
        inventory.setItem(slot, item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (!event.getInventory().equals(this.inventory)) return;
        event.setCancelled(true);

        eventBus.fireEvent(new MenuClickEvent(player, this, event.getSlot(), event.getCurrentItem()));
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        if (!event.getInventory().equals(this.inventory)) return;

        eventBus.fireEvent(new MenuCloseEvent(player, this));
    }
}
