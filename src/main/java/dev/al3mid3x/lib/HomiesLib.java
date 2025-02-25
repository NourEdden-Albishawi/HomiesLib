package dev.al3mid3x.lib;

import dev.al3mid3x.lib.configurations.ConfigManager;
import dev.al3mid3x.lib.configurations.DatabaseConfig;
import dev.al3mid3x.lib.gui.MenuManager;
import dev.al3mid3x.lib.managers.ActionBarManager;
import dev.al3mid3x.lib.tasks.WriteBehindTask;
import dev.al3mid3x.lib.utils.UnitOfWork;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Getter
public final class HomiesLib extends JavaPlugin {

    private static HomiesLib plugin;

    private MenuManager menuManager;
    private ConfigManager configManager;
    private ActionBarManager actionBarManager;
    private WriteBehindTask writeBehindTask;
    private UnitOfWork unitOfWork;
    public HomiesLib() {
        if (plugin == null)
            plugin = this;
    }

    public void onEnable() {
        configManager = new ConfigManager(this);
        menuManager = new MenuManager(this);
        actionBarManager = new ActionBarManager(plugin);
        DatabaseConfig databaseConfig = configManager.loadConfig(DatabaseConfig.class);

        try {
            Connection connection = DriverManager.getConnection(databaseConfig.mysqlUrl, databaseConfig.mysqlUsername, databaseConfig.mysqlPassword);

            unitOfWork = new UnitOfWork(connection);
            unitOfWork.loadAll();
            getLogger().info("Data loaded into memory.");

            writeBehindTask = new WriteBehindTask(unitOfWork);
            writeBehindTask.runTaskTimerAsynchronously(this, 0L, 20L); // Run every second
        } catch (Exception e) {
            getLogger().severe("Failed to initialize Unit of Work: " + e.getMessage());
        }
    }

    @Override
    public void onDisable() {
        try {
            unitOfWork.saveAll();
            unitOfWork.close();
            getLogger().info("Data saved and connection closed.");
        } catch (SQLException e) {
            getLogger().severe("Failed to save changes: " + e.getMessage());
        }
    }
}