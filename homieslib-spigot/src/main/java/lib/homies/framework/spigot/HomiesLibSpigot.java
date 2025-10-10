package lib.homies.framework.spigot;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.cooldown.DefaultCooldownService;
import lib.homies.framework.database.DatabaseService;
import lib.homies.framework.party.DefaultPartyService;
import lib.homies.framework.playerdata.DefaultPlayerDataService;
import lib.homies.framework.spigot.command.SpigotCommandManager;
import lib.homies.framework.spigot.config.SpigotConfigManager;
import lib.homies.framework.spigot.database.SpigotCachingDatabaseService;
import lib.homies.framework.spigot.database.SpigotMySQLDatabaseService;
import lib.homies.framework.spigot.database.SpigotRedisDatabaseService;
import lib.homies.framework.spigot.database.SpigotSQLiteDatabaseService;
import lib.homies.framework.spigot.events.SpigotEventBus;
import lib.homies.framework.spigot.events.SpigotEventManager;
import lib.homies.framework.spigot.menu.SpigotMenuManager;
import lib.homies.framework.spigot.minigame.DefaultMiniGameManager;
import lib.homies.framework.spigot.scheduler.SpigotSchedulerService;
import lib.homies.framework.spigot.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;

public final class HomiesLibSpigot extends JavaPlugin {

    private DatabaseService databaseService;
    private HomiesLibConfig homiesLibConfig;

    @Override
    public void onEnable() {
        // --- DIAGNOSTIC MESSAGE --- //
        getLogger().info("################################################");
        getLogger().info("---      RUNNING LATEST HomiesLib BUILD      ---");
        getLogger().info("################################################");

        // --- CORE SERVICES INITIALIZATION ---
        // These services must be initialized first as other components depend on them.
        HomiesLib.setEventBus(new SpigotEventBus(this));
        HomiesLib.setConfigManager(new SpigotConfigManager());
        HomiesLib.setSchedulerService(new SpigotSchedulerService(this));

        // --- CONFIGURATION LOADING ---
        SpigotPluginContext homiesLibPluginContext = new SpigotPluginContext(this);
        this.homiesLibConfig = HomiesLib.getConfigManager().loadConfig(homiesLibPluginContext, HomiesLibConfig.class);

        // --- DATABASE INITIALIZATION ---
        List<Class<?>> entityClasses = discoverEntityClasses();
        initializeDatabase(homiesLibPluginContext, entityClasses);
        HomiesLib.setDatabaseService(this.databaseService);

        // --- FEATURE MANAGERS & UTILITIES INITIALIZATION ---
        HomiesLib.setMiniGameManager(new DefaultMiniGameManager());
        HomiesLib.setMenuManager(new SpigotMenuManager(this));
        HomiesLib.setPlayerDataService(new DefaultPlayerDataService());
        HomiesLib.setCooldownService(new DefaultCooldownService());
        HomiesLib.setPartyService(new DefaultPartyService());
        HomiesLib.setTextUtils(new SpigotTextUtils());
        HomiesLib.setMessageUtils(new SpigotMessageUtils());
        HomiesLib.setLocationUtils(new SpigotLocationUtils());
        HomiesLib.setInventoryUtils(new SpigotInventoryUtils());
        HomiesLib.setPlayerHeadUtils(new SpigotPlayerHeadUtils());
        HomiesLib.setItemBuilderFactory(new SpigotItemBuilderFactory());

        // --- COMMAND AND EVENT REGISTRATION ---
        // These should be last, as they may depend on all other services.
        new SpigotCommandManager(this).registerAllCommands();
        new SpigotEventManager(this).registerAllListeners();

        getLogger().info("HomiesLibSpigot enabled!");
    }

    private void initializeDatabase(SpigotPluginContext context, List<Class<?>> entityClasses) {
        boolean connected = false;

        if (homiesLibConfig.redisEnabled) {
            getLogger().info("Redis is enabled. Attempting to initialize caching database service...");
            SpigotRedisDatabaseService redisService = new SpigotRedisDatabaseService(
                    homiesLibConfig.redisHost, homiesLibConfig.redisPort, homiesLibConfig.redisPassword, homiesLibConfig.redisDatabase);

            SpigotMySQLDatabaseService mysqlService = new SpigotMySQLDatabaseService(
                    homiesLibConfig.mysqlHost, homiesLibConfig.mysqlPort, homiesLibConfig.mysqlDatabase,
                    homiesLibConfig.mysqlUsername, homiesLibConfig.mysqlPassword, entityClasses);

            SpigotCachingDatabaseService cachingService = new SpigotCachingDatabaseService(redisService, mysqlService);
            if (cachingService.init()) {
                this.databaseService = cachingService;
                connected = true;
            }
        }

        if (!connected && homiesLibConfig.mysqlEnabled) {
            getLogger().info("Redis is disabled or failed. Attempting to initialize MySQL as primary store...");
            SpigotMySQLDatabaseService mysqlService = new SpigotMySQLDatabaseService(
                    homiesLibConfig.mysqlHost, homiesLibConfig.mysqlPort, homiesLibConfig.mysqlDatabase,
                    homiesLibConfig.mysqlUsername, homiesLibConfig.mysqlPassword, entityClasses);
            if (mysqlService.init()) {
                this.databaseService = mysqlService;
                connected = true;
            }
        }

        if (!connected && homiesLibConfig.sqliteFallbackEnabled) {
            getLogger().info("Redis and MySQL are disabled or failed. Attempting to initialize SQLite as fallback...");
            SpigotSQLiteDatabaseService sqliteService = new SpigotSQLiteDatabaseService(context, entityClasses);
            if (sqliteService.init()) {
                this.databaseService = sqliteService;
                connected = true;
            }
        }

        if (!connected) {
            getLogger().severe("All database services (Redis, MySQL, SQLite) are disabled or failed to connect. No data persistence will be available.");
            this.databaseService = null;
        }
    }

    @Override
    public void onDisable() {
        if (databaseService != null) {
            getLogger().info("Disabling Database Service...");
            if (databaseService instanceof SpigotCachingDatabaseService) {
                ((SpigotCachingDatabaseService) databaseService).onDisable();
            } else if (databaseService instanceof SpigotMySQLDatabaseService) {
                ((SpigotMySQLDatabaseService) databaseService).onDisable();
            } else if (databaseService instanceof SpigotSQLiteDatabaseService) {
                ((SpigotSQLiteDatabaseService) databaseService).onDisable();
            }
            getLogger().info("Database Service disabled.");
        }
        getLogger().info("HomiesLibSpigot disabled!");
    }

    private List<Class<?>> discoverEntityClasses() {
        List<Class<?>> discoveredClasses = new ArrayList<>();
        String resourceName = "META-INF/homieslib/entities.list";

        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin.getClass().getClassLoader() == null) continue;
            try {
                Enumeration<URL> urls = plugin.getClass().getClassLoader().getResources(resourceName);
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    try (InputStream is = url.openStream();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim();
                            if (!line.isEmpty()) {
                                try {
                                    discoveredClasses.add(Class.forName(line, true, plugin.getClass().getClassLoader()));
                                } catch (ClassNotFoundException e) {
                                    getLogger().log(Level.WARNING, "Could not load entity class '" + line + "' from plugin '" + plugin.getName() + "'. It might have been removed or renamed.");
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // This is often noisy and not a true error, so we log at a lower level.
            }
        }
        if (!discoveredClasses.isEmpty()) {
            getLogger().info("Discovered " + discoveredClasses.size() + " @DbEntity classes across all plugins.");
        }
        return discoveredClasses;
    }
}
