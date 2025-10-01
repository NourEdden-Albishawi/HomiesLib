package lib.homies.framework.spigot;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.cooldown.DefaultCooldownService;
import lib.homies.framework.party.DefaultPartyService;
import lib.homies.framework.playerdata.DefaultPlayerDataService;
import lib.homies.framework.spigot.command.SpigotCommandManager;
import lib.homies.framework.spigot.config.SpigotConfigManager;
import lib.homies.framework.spigot.database.SpigotRedisDatabaseService;
import lib.homies.framework.spigot.events.SpigotEventBus;
import lib.homies.framework.spigot.menu.SpigotMenuManager;
import lib.homies.framework.spigot.minigame.DefaultMiniGameManager;
import lib.homies.framework.spigot.scheduler.SpigotSchedulerService;
import lib.homies.framework.spigot.utils.*;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main plugin class for the HomiesLib Spigot/Paper implementation.
 * This class extends {@link JavaPlugin} and is responsible for initializing
 * and registering all Spigot-specific service implementations with the
 * {@link HomiesLib} service locator.
 */
public final class HomiesLibSpigot extends JavaPlugin {

    private SpigotRedisDatabaseService databaseService;
    private HomiesLibConfig homiesLibConfig;

    @Override
    public void onEnable() {
        HomiesLib.setConfigManager(new SpigotConfigManager());

        SpigotPluginContext homiesLibPluginContext = new SpigotPluginContext(this);

        this.homiesLibConfig = HomiesLib.getConfigManager().loadConfig(homiesLibPluginContext, HomiesLibConfig.class);

        this.databaseService = new SpigotRedisDatabaseService(
                homiesLibConfig.redisHost,
                homiesLibConfig.redisPort,
                homiesLibConfig.redisPassword,
                homiesLibConfig.redisDatabase
        );
        this.databaseService.init();
        getLogger().info("Using Redis database service.");

        HomiesLib.setDatabaseService(this.databaseService);
        HomiesLib.setMiniGameManager(new DefaultMiniGameManager());
        HomiesLib.setEventBus(new SpigotEventBus(this));
        HomiesLib.setMenuManager(new SpigotMenuManager(this));
        HomiesLib.setSchedulerService(new SpigotSchedulerService(this));
        HomiesLib.setPlayerDataService(new DefaultPlayerDataService());
        HomiesLib.setCooldownService(new DefaultCooldownService());
        HomiesLib.setPartyService(new DefaultPartyService());
        HomiesLib.setTextUtils(new SpigotTextUtils());
        HomiesLib.setMessageUtils(new SpigotMessageUtils());
        HomiesLib.setLocationUtils(new SpigotLocationUtils());
        HomiesLib.setInventoryUtils(new SpigotInventoryUtils());
        HomiesLib.setPlayerHeadUtils(new SpigotPlayerHeadUtils());
        HomiesLib.setItemBuilderFactory(new SpigotItemBuilderFactory());

        new SpigotCommandManager(this).registerAllCommands();

        getLogger().info("HomiesLibSpigot enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseService != null) {
            databaseService.onDisable();
        }
        getLogger().info("HomiesLibSpigot disabled!");
    }
}
