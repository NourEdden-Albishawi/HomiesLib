package lib.homies.framework;

import lib.homies.framework.config.ConfigManager;
import lib.homies.framework.cooldown.CooldownService;
import lib.homies.framework.database.DatabaseService;
import lib.homies.framework.events.EventBus;
import lib.homies.framework.events.Listener;
import lib.homies.framework.menu.MenuManager;
import lib.homies.framework.minigame.MiniGameManager;
import lib.homies.framework.party.PartyService;
import lib.homies.framework.playerdata.PlayerDataService;
import lib.homies.framework.scheduler.SchedulerService;
import lib.homies.framework.utils.*;

/**
 * The central service locator for the HomiesLib framework.
 * Provides static access to all registered platform-agnostic services.
 * This class is initialized by the platform-specific plugin (e.g., HomiesLibSpigot)
 * on server startup.
 */
public final class HomiesLib {

    private static MenuManager menuManager;
    private static SchedulerService schedulerService;
    private static DatabaseService databaseService;
    private static ConfigManager configManager;
    private static EventBus eventBus;
    private static MiniGameManager miniGameManager;
    private static PlayerDataService playerDataService;
    private static CooldownService cooldownService;
    private static PartyService partyService;
    private static ItemBuilderFactory itemBuilderFactory;
    private static TextUtils textUtils;
    private static MessageUtils messageUtils;
    private static LocationUtils locationUtils;
    private static InventoryUtils inventoryUtils;
    private static PlayerHeadUtils playerHeadUtils;

    /**
     * Private constructor to prevent instantiation.
     * All access to services is via static getters.
     */
    private HomiesLib() {}

    /**
     * Registers the given listeners.
     *
     * @param listeners The listeners to register.
     */
    public static void registerListeners(Listener... listeners) {
        for (Listener listener : listeners) {
            listener.register();
        }
    }

    /**
     * Retrieves the MenuManager service.
     * @return The MenuManager instance.
     */
    public static MenuManager getMenuManager() {
        return menuManager;
    }

    /**
     * Sets the MenuManager service. Intended for internal framework use during initialization.
     * @param menuManager The MenuManager instance to set.
     */
    public static void setMenuManager(MenuManager menuManager) {
        HomiesLib.menuManager = menuManager;
    }

    /**
     * Retrieves the SchedulerService.
     * @return The SchedulerService instance.
     */
    public static SchedulerService getSchedulerService() {
        return schedulerService;
    }

    /**
     * Sets the SchedulerService. Intended for internal framework use during initialization.
     * @param schedulerService The SchedulerService instance to set.
     */
    public static void setSchedulerService(SchedulerService schedulerService) {
        HomiesLib.schedulerService = schedulerService;
    }

    /**
     * Retrieves the DatabaseService.
     * @return The DatabaseService instance.
     */
    public static DatabaseService getDatabaseService() {
        return databaseService;
    }

    /**
     * Sets the DatabaseService. Intended for internal framework use during initialization.
     * @param databaseService The DatabaseService instance to set.
     */
    public static void setDatabaseService(DatabaseService databaseService) {
        HomiesLib.databaseService = databaseService;
    }

    /**
     * Retrieves the ConfigManager service.
     * @return The ConfigManager instance.
     */
    public static ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Sets the ConfigManager service. Intended for internal framework use during initialization.
     * @param configManager The ConfigManager instance to set.
     */
    public static void setConfigManager(ConfigManager configManager) {
        HomiesLib.configManager = configManager;
    }

    /**
     * Retrieves the EventBus service.
     * @return The EventBus instance.
     */
    public static EventBus getEventBus() {
        return eventBus;
    }

    /**
     * Sets the EventBus service. Intended for internal framework use during initialization.
     * @param eventBus The EventBus instance to set.
     */
    public static void setEventBus(EventBus eventBus) {
        HomiesLib.eventBus = eventBus;
    }

    /**
     * Retrieves the MiniGameManager service.
     * @return The MiniGameManager instance.
     */
    public static MiniGameManager getMiniGameManager() {
        return miniGameManager;
    }

    /**
     * Sets the MiniGameManager service. Intended for internal framework use during initialization.
     * @param miniGameManager The MiniGameManager instance to set.
     */
    public static void setMiniGameManager(MiniGameManager miniGameManager) {
        HomiesLib.miniGameManager = miniGameManager;
    }

    /**
     * Retrieves the PlayerDataService.
     * @return The PlayerDataService instance.
     */
    public static PlayerDataService getPlayerDataService() {
        return playerDataService;
    }

    /**
     * Sets the PlayerDataService. Intended for internal framework use during initialization.
     * @param playerDataService The PlayerDataService instance to set.
     */
    public static void setPlayerDataService(PlayerDataService playerDataService) {
        HomiesLib.playerDataService = playerDataService;
    }

    /**
     * Retrieves the CooldownService.
     * @return The CooldownService instance.
     */
    public static CooldownService getCooldownService() {
        return cooldownService;
    }

    /**
     * Sets the CooldownService. Intended for internal framework use during initialization.
     * @param cooldownService The CooldownService instance to set.
     */
    public static void setCooldownService(CooldownService cooldownService) {
        HomiesLib.cooldownService = cooldownService;
    }

    /**
     * Retrieves the PartyService.
     * @return The PartyService instance.
     */
    public static PartyService getPartyService() {
        return partyService;
    }

    /**
     * Sets the PartyService. Intended for internal framework use during initialization.
     * @param partyService The PartyService instance to set.
     */
    public static void setPartyService(PartyService partyService) {
        HomiesLib.partyService = partyService;
    }

    /**
     * Retrieves an ItemBuilder instance for a given material.
     * @param material The name of the material (e.g., "STONE", "DIAMOND_SWORD").
     * @return A new ItemBuilder instance.
     * @throws IllegalStateException if the ItemBuilderFactory has not been initialized.
     */
    public static ItemBuilder getItemBuilder(String material) {
        if (itemBuilderFactory == null) {
            throw new IllegalStateException("ItemBuilderFactory has not been initialized.");
        }
        return itemBuilderFactory.create(material);
    }

    /**
     * Sets the ItemBuilderFactory. Intended for internal framework use during initialization.
     * @param factory The ItemBuilderFactory instance to set.
     */
    public static void setItemBuilderFactory(ItemBuilderFactory factory) {
        itemBuilderFactory = factory;
    }

    /**
     * Retrieves the TextUtils service.
     * @return The TextUtils instance.
     */
    public static TextUtils getTextUtils() {
        return textUtils;
    }

    /**
     * Sets the TextUtils service. Intended for internal framework use during initialization.
     * @param textUtils The TextUtils instance to set.
     */
    public static void setTextUtils(TextUtils textUtils) {
        HomiesLib.textUtils = textUtils;
    }

    /**
     * Retrieves the MessageUtils service.
     * @return The MessageUtils instance.
     */
    public static MessageUtils getMessageUtils() {
        return messageUtils;
    }

    /**
     * Sets the MessageUtils service. Intended for internal framework use during initialization.
     * @param messageUtils The MessageUtils instance to set.
     */
    public static void setMessageUtils(MessageUtils messageUtils) {
        HomiesLib.messageUtils = messageUtils;
    }

    /**
     * Retrieves the LocationUtils service.
     * @return The LocationUtils instance.
     */
    public static LocationUtils getLocationUtils() {
        return locationUtils;
    }

    /**
     * Sets the LocationUtils service. Intended for internal framework use during initialization.
     * @param locationUtils The LocationUtils instance to set.
     */
    public static void setLocationUtils(LocationUtils locationUtils) {
        HomiesLib.locationUtils = locationUtils;
    }

    /**
     * Retrieves the InventoryUtils service.
     * @return The InventoryUtils instance.
     */
    public static InventoryUtils getInventoryUtils() {
        return inventoryUtils;
    }

    /**
     * Sets the InventoryUtils service. Intended for internal framework use during initialization.
     * @param inventoryUtils The InventoryUtils instance to set.
     */
    public static void setInventoryUtils(InventoryUtils inventoryUtils) {
        HomiesLib.inventoryUtils = inventoryUtils;
    }

    /**
     * Retrieves the PlayerHeadUtils service.
     * @return The PlayerHeadUtils instance.
     */
    public static PlayerHeadUtils getPlayerHeadUtils() {
        return playerHeadUtils;
    }

    /**
     * Sets the PlayerHeadUtils service. Intended for internal framework use during initialization.
     * @param playerHeadUtils The PlayerHeadUtils instance to set.
     */
    public static void setPlayerHeadUtils(PlayerHeadUtils playerHeadUtils) {
        HomiesLib.playerHeadUtils = playerHeadUtils;
    }
}
