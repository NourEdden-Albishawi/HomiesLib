package lib.homies.framework.spigot.menu;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.menu.HomiesMenu;
import lib.homies.framework.menu.HomiesMenuItem;
import lib.homies.framework.menu.MenuManager;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.spigot.player.SpigotPlayer;
import lib.homies.framework.spigot.utils.SpigotItemStack;
import lib.homies.framework.spigot.utils.SpigotTextUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

/**
 * Spigot-specific implementation of the {@link MenuManager} interface.
 * This class handles the creation, opening, closing, and interaction logic for inventory-based menus
 * on a Bukkit/Spigot server. It also manages menu animations and event handling.
 */
public class SpigotMenuManager implements MenuManager, Listener {

    private final Plugin plugin;
    private final SpigotTextUtils textUtils = new SpigotTextUtils();
    private final Map<String, HomiesMenu> registeredMenus = new HashMap<>();
    private final Map<UUID, HomiesMenu> openMenus = new ConcurrentHashMap<>(); // Use ConcurrentHashMap for thread safety

    /**
     * Constructs a new SpigotMenuManager.
     * @param plugin The {@link Plugin} instance of the framework, used for registering Bukkit listeners.
     */
    public SpigotMenuManager(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Opens a specific menu for a given player.
     * Handles menu animations (pop-in) and initial item population.
     * @param homiesPlayer The {@link HomiesPlayer} for whom to open the menu.
     * @param menu The {@link HomiesMenu} instance to open.
     */
    @Override
    public void openMenu(HomiesPlayer homiesPlayer, HomiesMenu menu) {
        if (!(homiesPlayer instanceof SpigotPlayer)) {
            plugin.getLogger().warning("Attempted to open menu for a non-Spigot player: " + homiesPlayer.getName());
            return;
        }
        Player player = ((SpigotPlayer) homiesPlayer).getPlayer();

        Inventory inventory = Bukkit.createInventory(null, menu.getSize(), textUtils.colorize(menu.getTitle(homiesPlayer)));

        long animationDelay = menu.getAnimationDelayTicks();
        String animationSound = menu.getAnimationSound();
        Map<Integer, HomiesMenuItem> menuItems = menu.getItems(homiesPlayer);

        if (animationDelay > 0) {
            // Open the empty inventory immediately, items will be added by scheduler
            player.openInventory(inventory);

            // Staggered item placement for animation
            final long[] currentDelay = {0L};
            menuItems.forEach((slot, menuItem) -> {
                HomiesLib.getSchedulerService().runLater(() -> {
                    // Ensure menu is still open and player is viewing this specific inventory
                    if (player.getOpenInventory().getTopInventory().equals(inventory)) {
                        if (menuItem.getItemStack(homiesPlayer) instanceof SpigotItemStack) {
                            inventory.setItem(slot, ((SpigotItemStack) menuItem.getItemStack(homiesPlayer)).getItemStack());
                            if (animationSound != null) {
                                try {
                                    player.playSound(player.getLocation(), Sound.valueOf(animationSound.toUpperCase()), 0.5f, 1.0f);
                                } catch (IllegalArgumentException e) {
                                    plugin.getLogger().warning("Invalid animation sound: " + animationSound + ": " + e.getMessage());
                                }
                            }
                        }
                    }
                }, currentDelay[0]);
                currentDelay[0] += animationDelay;
            });
        } else {
            // Instant item placement
            for (Map.Entry<Integer, HomiesMenuItem> entry : menuItems.entrySet()) {
                int slot = entry.getKey();
                HomiesMenuItem menuItem = entry.getValue();
                if (menuItem.getItemStack(homiesPlayer) instanceof SpigotItemStack) {
                    inventory.setItem(slot, ((SpigotItemStack) menuItem.getItemStack(homiesPlayer)).getItemStack());
                }
            }
            player.openInventory(inventory);
        }

        openMenus.put(player.getUniqueId(), menu);
        menu.handleOpen(homiesPlayer);
    }

    /**
     * Closes the currently open menu for a given player.
     * Handles menu animations (pop-out) if configured.
     * @param homiesPlayer The {@link HomiesPlayer} for whom to close the menu.
     */
    @Override
    public void closeMenu(HomiesPlayer homiesPlayer) {
        if (!(homiesPlayer instanceof SpigotPlayer)) {
            plugin.getLogger().warning("Attempted to close menu for a non-Spigot player: " + homiesPlayer.getName());
            return;
        }
        Player player = ((SpigotPlayer) homiesPlayer).getPlayer();
        HomiesMenu menu = openMenus.get(player.getUniqueId());

        if (menu != null) {
            long animationOutDelay = menu.getAnimationOutDelayTicks();
            if (animationOutDelay > 0) {
                Inventory currentInventory = player.getOpenInventory().getTopInventory();
                List<Integer> slots = new ArrayList<>(menu.getItems(homiesPlayer).keySet());
                Collections.shuffle(slots);

                final long[] currentDelay = {0L};
                for (int slot : slots) {
                    HomiesLib.getSchedulerService().runLater(() -> {
                        if (player.getOpenInventory().getTopInventory().equals(currentInventory)) {
                            currentInventory.setItem(slot, null);
                        }
                    }, currentDelay[0]);
                    currentDelay[0] += animationOutDelay;
                }
                HomiesLib.getSchedulerService().runLater(player::closeInventory, currentDelay[0]);
            } else {
                player.closeInventory();
            }
        } else {
            player.closeInventory();
        }
    }

    /**
     * Registers a menu with a unique ID, allowing it to be retrieved later.
     * @param id A unique string identifier for the menu.
     * @param menu The {@link HomiesMenu} instance to register.
     */
    @Override
    public void registerMenu(String id, HomiesMenu menu) {
        registeredMenus.put(id, menu);
    }

    /**
     * Retrieves a registered menu by its unique ID.
     * @param id The unique string identifier of the menu.
     * @return The {@link HomiesMenu} instance if found, otherwise {@code null}.
     */
    @Override
    public HomiesMenu getMenu(String id) {
        return registeredMenus.get(id);
    }

    /**
     * Handles Bukkit's {@link InventoryClickEvent}.
     * Delegates the click to the currently open {@link HomiesMenu} and its {@link HomiesMenuItem}.
     * @param event The Bukkit InventoryClickEvent.
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        HomiesMenu openMenu = openMenus.get(player.getUniqueId());

        if (openMenu != null) {
            event.setCancelled(true);
            HomiesPlayer homiesPlayer = new SpigotPlayer(player);
            HomiesMenuItem clickedItem = openMenu.getItems(homiesPlayer).get(event.getSlot());
            if (clickedItem != null && clickedItem.getAction() != null) {
                try {
                    clickedItem.getAction().accept(homiesPlayer);
                } catch (Exception e) {
                    plugin.getLogger().log(Level.SEVERE, "Error executing menu item action for " + player.getName() + " in menu " + openMenu.getTitle(homiesPlayer), e);
                }
            }
            openMenu.handleClick(homiesPlayer, event.getSlot());
        }
    }

    /**
     * Handles Bukkit's {@link InventoryCloseEvent}.
     * Notifies the {@link HomiesMenu} that it has been closed for the player.
     * @param event The Bukkit InventoryCloseEvent.
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        HomiesMenu closedMenu = openMenus.remove(player.getUniqueId());

        if (closedMenu != null) {
            HomiesPlayer homiesPlayer = new SpigotPlayer(player);
            closedMenu.handleClose(homiesPlayer);
        }
    }
}
