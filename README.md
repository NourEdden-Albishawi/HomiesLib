# ✨ HomiesLib: The Ultimate Minecraft Plugin Framework ✨

Welcome to **HomiesLib** – your go-to, multi-platform framework for crafting high-performance, feature-rich Minecraft plugins with unparalleled simplicity! 🚀 Designed for third-party developers, HomiesLib takes the pain out of plugin development, letting you focus on innovation, not boilerplate.

---

## 🌟 Overview: Build Smarter, Not Harder!

HomiesLib is a modular Java framework built to power Minecraft plugins across various platforms (currently
Paper/Spigot!). We've meticulously engineered a clean, API-driven architecture that prioritizes:

*   **⚡ Performance:** Optimized systems to keep your server running smoothly.
*   **✨ Simplicity:** Intuitive APIs and powerful abstractions for rapid development.
*   **🌍 Multi-Platform Support:** Write your core logic once, deploy it everywhere!
*   **🛠️ Modularity:** Only use what you need, keep your plugins lean.

Say goodbye to tangled code and hello to a framework that makes development a joy! 🎉

---

## 🚀 Key Features at a Glance

HomiesLib provides robust, abstracted systems for:

* **💬 Commands:** A fully automatic, annotation-driven command system with dispatcher and tab-completer generation.
*   **⚙️ Configuration:** Easy, annotation-based YAML configuration management.
*   **📢 Events:** A powerful event bus for both custom and native platform events.
*   **🖼️ Menus:** Dynamic, paginated, and animated inventory GUIs.
*   **⏱️ Scheduling:** Flexible task scheduling for sync and async operations.
*   **💾 Database:** Redis-based ORM with annotation-driven entity mapping.
*   **🎮 Minigames:** Abstracted core for building complex minigame logic.
*   **👤 Player Data:** Platform-independent service for managing player-specific data.
*   **⏳ Cooldowns:** Efficiently manage time-based restrictions for player actions.
*   **🥳 Parties:** Robust system for creating and managing player groups.
*   **✨ Utilities:** Platform-agnostic helpers for items, text, locations, and more!

---

## 💡 Why HomiesLib? The Multi-Platform Magic!

Our new architecture is a game-changer. Here's why it's awesome:

*   **`homieslib-api` (The Blueprint 🗺️):** Contains only interfaces and data objects. It defines *what* your framework does, with **zero** platform dependencies. Code against this, and your logic is instantly portable!
*   **`homieslib-spigot` (The Engine ⚙️):** The concrete implementation for Spigot/Paper servers. All the `org.bukkit` magic lives here, isolated and encapsulated.
*   **`homieslib-processor` (The Wizard 🧙):** Generates boilerplate code automatically, keeping your hands clean.

This means you write your core plugin logic once, and if we add `homieslib-bungeecord` or `homieslib-velocity` implementations, your plugin will work there too! 🤯

---

## 🛠️ Getting Started (For Third-Party Developers)

To use HomiesLib in your plugin:

1.  **Add HomiesLib to your `pom.xml`:**
    Include the `homieslib-api` and the `homieslib-spigot` implementation as `compile` dependencies.

    ```xml
    <dependencies>
        <!-- Your plugin's other dependencies -->

        <dependency>
            <groupId>lib.homies.framework</groupId>
            <artifactId>homieslib-api</artifactId>
            <version>1.0-SNAPSHOT</version> <!-- Use your current version -->
            <scope>compile</scope>
        </dependency>

        <!-- For Spigot/Paper servers: -->
        <dependency>
            <groupId>lib.homies.framework</groupId>
            <artifactId>homieslib-spigot</artifactId>
            <version>1.0-SNAPSHOT</version> <!-- Use your current version -->
            <scope>compile</scope>
        </dependency>
    </dependencies>
    ```

2.  **Configure Maven for Annotation Processing (Crucial for Commands & other features):**
    The command system is **fully automatic**. To make it work, you *must* configure your plugin's `pom.xml` to run the
    `homieslib-processor`. This generates all necessary command code during compilation.

    Add the `homieslib-processor` as a `provided` dependency and configure the `maven-compiler-plugin`.

    ```xml
    <dependencies>
        <!-- ... existing HomiesLib dependencies ... -->

        <!-- Required for HomiesLib's annotation processing -->
        <dependency>
            <groupId>lib.homies.framework</groupId>
            <artifactId>homieslib-processor</artifactId>
            <version>1.0-SNAPSHOT</version> <!-- Use your current version -->
            <scope>provided</scope>
        </dependency>
        <!-- ... other dependencies ... -->
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version> <!-- Use a recent version -->
                <configuration>
                    <source>17</source> <!-- Match your project's Java version -->
                    <target>17</target> <!-- Match your project's Java version -->
                    <annotationProcessorPaths>
                        <!-- HomiesLib's annotation processor -->
                        <path>
                            <groupId>lib.homies.framework</groupId>
                            <artifactId>homieslib-processor</artifactId>
                            <version>1.0-SNAPSHOT</version> <!-- Use your current version -->
                        </path>
                        <!-- Add other processors like Lombok here if needed -->
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <!-- ... other plugins ... -->
        </plugins>
    </build>
    ```

3.  **Declare Dependency in `plugin.yml`:**
    Ensure your plugin loads *after* HomiesLib by adding it to your `plugin.yml`'s `depend` section.

    ```yaml
    # Your plugin's plugin.yml
    name: MyAwesomePlugin
    version: 1.0
    main: com.yourcompany.MyAwesomePlugin
    api-version: 1.21 # Or your target Minecraft API version

    depend:
      - HomiesLibSpigot
    ```

4. **Initialize HomiesLib in your `onEnable()`:**
   Your main plugin class should extend `JavaPlugin` (or `HomiesLibSpigot` if you want to directly use the framework's
   main plugin class).
   The framework will automatically initialize its services.

---

## 📚 Usage Examples

Here are examples demonstrating how to use various systems provided by HomiesLib.

### 💬 Command System

Define each command in its own class. The framework handles registration, sub-command dispatching, and tab completion
automatically.

```java
// In your plugin's source code (e.g., com.yourcompany.commands.GamemodeCommand.java)
package com.yourcompany.commands;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.command.annotations.*;
import lib.homies.framework.player.HomiesPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A comprehensive /gamemode command demonstrating all features of the command framework.
 */
@Command(name = "gamemode", description = "Set a player's gamemode.", aliases = {"gm"}, usage = "&cUsage: /<command> <mode> [player]")
public class GamemodeCommand {

    // 1. Default Executor: Called for `/gamemode`
    @Command(playerOnly = true)
    public void execute(HomiesPlayer sender) {
        // This method is required and serves as the entry point.
        // It runs when no subcommands match.
        HomiesLib.getMessageUtils().send(sender, "&cUsage: /gamemode <creative|survival> [player]");
    }

    // 2. Subcommand without arguments: `/gamemode creative`
    @SubCommand(value = "creative", aliases = {"1", "c"}, description = "Set your gamemode to Creative.")
    @Permission(value = "myplugin.gamemode.creative", message = "&cYou don't have permission.")
    public void onCreative(HomiesPlayer sender) {
        Player bukkitPlayer = sender.getAs(Player.class); // Safely get the underlying Bukkit Player
        if (bukkitPlayer != null) {
            bukkitPlayer.setGameMode(GameMode.CREATIVE);
            HomiesLib.getMessageUtils().send(sender, "&aYour gamemode has been set to &eCreative&a.");
        } else {
            HomiesLib.getMessageUtils().send(sender, "&cThis command can only be run by a player.");
        }
    }

    // 3. Subcommand with a player argument: `/gamemode creative <player>`
    @SubCommand(value = "creative <player>", description = "Set another player's gamemode to Creative.")
    @Permission("myplugin.gamemode.creative.other")
    public void onCreativeOther(HomiesPlayer sender, HomiesPlayer target) {
        Player targetPlayer = target.getAs(Player.class);
        if (targetPlayer != null) {
            targetPlayer.setGameMode(GameMode.CREATIVE);
            HomiesLib.getMessageUtils().send(sender, "&aSet &b" + target.getName() + "'s &agamemode to &eCreative&a.");
            HomiesLib.getMessageUtils().send(target, "&aYour gamemode was set to &eCreative &aby &b" + sender.getName() + "&a.");
        } else {
            HomiesLib.getMessageUtils().send(sender, "&cTarget player not found or not online.");
        }
    }

    // 4. Another subcommand: `/gamemode survival`
    @SubCommand(value = "survival", aliases = {"0", "_s"}, description = "Set your gamemode to Survival.")
    @Permission("myplugin.gamemode.survival")
    public void onSurvival(HomiesPlayer sender) {
        Player bukkitPlayer = sender.getAs(Player.class);
        if (bukkitPlayer != null) {
            bukkitPlayer.setGameMode(GameMode.SURVIVAL);
            HomiesLib.getMessageUtils().send(sender, "&aYour gamemode has been set to &eSurvival&a.");
        } else {
            HomiesLib.getMessageUtils().send(sender, "&cThis command can only be run by a player.");
        }
    }

    // --- TAB COMPLETION --- //

    /**
     * Provides suggestions for the first argument (e.g., /gamemode <TAB>).
     * The empty value `""` targets the first argument.
     */
    @TabComplete(value = "")
    public List<String> completeGamemode(CommandSender sender) {
        return Arrays.asList("creative", "survival");
    }

    /**
     * Provides player name suggestions for the argument AFTER "creative".
     * (e.g., /gamemode creative <TAB>)
     */
    @TabComplete(value = "creative")
    public List<String> completePlayerNames(CommandSender sender) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }

    /**
     * You can reuse the same method for multiple paths.
     * This also provides player names for `/gamemode survival <TAB>`.
     */
    @TabComplete(value = "survival")
    public List<String> completePlayerNamesForSurvival(CommandSender sender) {
        return completePlayerNames(sender); // Reuse the same logic
    }
}
```

### ⏱️ Scheduler Service

Schedule tasks to run synchronously or asynchronously, with delays or repeating intervals.

```java
// In your plugin's main class or a service

import lib.homies.framework.HomiesLib;
import lib.homies.framework.scheduler.HomiesTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MyPluginSchedulerExample {

    private HomiesTask repeatingTask;

    public void onEnable() {
        // Run a task once after 100 ticks (5 seconds) on the main thread
        HomiesLib.getSchedulerService().task()
                .run(() -> Bukkit.broadcastMessage("§aThis message appeared after 5 seconds!"))
                .delay(100L)
                .start();

        // Run an asynchronous task immediately
        HomiesLib.getSchedulerService().task()
                .run(() -> {
                    // Perform a heavy computation off the main thread
                    long result = 0;
                    for (int i = 0; i < 1_000_000; i++) result += i;
                    System.out.println("Async computation finished: " + result);
                })
                .async()
                .start();

        // Run a repeating task every 20 ticks (1 second) after an initial 0-tick delay
        repeatingTask = HomiesLib.getSchedulerService().task()
                .run(() -> Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage("§bRepeating message!")))
                .repeat(20L)
                .start();

        // Run a repeating task with a delay and add it to a group
        HomiesLib.getSchedulerService().task()
                .run(() -> Bukkit.broadcastMessage("§eGrouped repeating task!"))
                .delay(40L)
                .repeat(60L)
                .group("my-special-tasks")
                .start();
    }

    public void onDisable() {
        // Cancel a specific repeating task
        if (repeatingTask != null) {
            repeatingTask.cancel();
        }
        // Cancel all tasks belonging to a specific group
        HomiesLib.getSchedulerService().cancelGroup("my-special-tasks");
        // Cancel ALL tasks scheduled by HomiesLib for this plugin
        HomiesLib.getSchedulerService().cancelAll();
    }
}
```

### ⚙️ Configuration Management

Define your configuration as a simple POJO and let HomiesLib handle loading and saving.

```java
// 1. Define your configuration class (e.g., com.yourcompany.config.MyPluginConfig.java)
package com.yourcompany.config;

import lib.homies.framework.config.annotations.ConfigFile;
import lib.homies.framework.config.annotations.ConfigKey;

@ConfigFile(fileName = "config.yml", path = "settings") // Creates plugins/YourPlugin/settings/config.yml
public class MyPluginConfig {

    @ConfigKey(path = "welcome-message", comment = "The message sent to new players.")
    public String welcomeMessage = "&aWelcome, %player_name%!";

    @ConfigKey(path = "spawn-location.world")
    public String spawnWorld = "world";

    @ConfigKey(path = "spawn-location.x")
    public double spawnX = 0.5;

    @ConfigKey(path = "spawn-location.y")
    public double spawnY = 64.0;

    @ConfigKey(path = "spawn-location.z")
    public double spawnZ = 0.5;

    @ConfigKey(path = "features.economy-enabled")
    public boolean economyEnabled = true;

    @ConfigKey(path = "features.max-players-per-party")
    public int maxPartySize = 8;
}

// 2. Load and use in your plugin's main class
import lib.homies.framework.HomiesLib;
import lib.homies.framework.PluginContext;
import org.bukkit.plugin.java.JavaPlugin;
import com.yourcompany.config.MyPluginConfig;

public class MyPlugin extends JavaPlugin {

    private MyPluginConfig config;

    @Override
    public void onEnable() {
        // Assuming your plugin's main class implements PluginContext or you create an adapter
        // For Spigot/Paper:
        PluginContext pluginContext = new lib.homies.framework.spigot.SpigotPluginContext(this);

        // Load the configuration
        config = HomiesLib.getConfigManager().loadConfig(pluginContext, MyPluginConfig.class);

        // Use config values
        getLogger().info("Welcome message: " + config.welcomeMessage);
        getLogger().info("Economy enabled: " + config.economyEnabled);

        // Example of modifying and saving config (e.g., via an admin command)
        // config.economyEnabled = false;
        // HomiesLib.getConfigManager().saveConfig(pluginContext, config);
    }
}
```

### 💾 Database Service (Redis ORM)

Persist and retrieve simple POJOs to/from Redis using annotations.

```java
// 1. Define your PlayerData entity (e.g., com.yourcompany.data.MyPlayerData.java)
package com.yourcompany.data;

import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;
import lombok.Data; // Requires Lombok
import lombok.NoArgsConstructor; // Requires Lombok

@Data // Lombok annotation for getters, setters, equals, hashCode, toString
@NoArgsConstructor // Required for reflection-based instantiation by the database service
@DbEntity(tableName = "my_player_data") // Stores data under keys like "my_player_data:PLAYER_UUID"
public class MyPlayerData {

    @DbField(columnName = "uuid", id = true) // Marks this field as the unique ID in Redis
    private String uuid;

    @DbField(columnName = "coins")
    private int coins = 0;

    @DbField(columnName = "last_seen_world")
    private String lastSeenWorld;

    // Custom constructor for convenience
    public MyPlayerData(String uuid) {
        this.uuid = uuid;
    }
}

// 2. Use the service in your plugin
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import com.yourcompany.data.MyPlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class MyPluginDatabaseExample implements Listener {

    // Assuming you have a way to get HomiesPlayer from a Bukkit Player
    // For example, if your main plugin extends HomiesLibSpigot, you might have a method.
    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        // For Spigot:
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        HomiesPlayer homiesPlayer = getHomiesPlayer(event.getPlayer());
        if (homiesPlayer == null) return;

        Optional<MyPlayerData> data = HomiesLib.getDatabaseService().findById(MyPlayerData.class, homiesPlayer.getUniqueId().toString());

        MyPlayerData playerData = data.orElseGet(() -> {
            MyPlayerData newData = new MyPlayerData(homiesPlayer.getUniqueId().toString());
            Player bukkitPlayer = homiesPlayer.getAs(Player.class);
            if (bukkitPlayer != null) {
                newData.setLastSeenWorld(bukkitPlayer.getWorld().getName()); // Example of platform-specific data
            }
            return newData;
        });

        HomiesLib.getMessageUtils().send(homiesPlayer, "&aWelcome back! You have &e" + playerData.getCoins() + " coins.");
        // Store in-memory for quick access if needed
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HomiesPlayer homiesPlayer = getHomiesPlayer(event.getPlayer());
        if (homiesPlayer == null) return;

        // Retrieve player data (e.g., from an in-memory map or a dedicated PlayerDataService cache)
        // For this example, let's assume we're updating some data before saving
        MyPlayerData playerData = HomiesLib.getDatabaseService().findById(MyPlayerData.class, homiesPlayer.getUniqueId().toString())
                .orElseGet(() -> new MyPlayerData(homiesPlayer.getUniqueId().toString()));

        playerData.setCoins(playerData.getCoins() + 1); // Example: give 1 coin on quit
        Player bukkitPlayer = homiesPlayer.getAs(Player.class);
        if (bukkitPlayer != null) {
            playerData.setLastSeenWorld(bukkitPlayer.getWorld().getName());
        }

        HomiesLib.getDatabaseService().saveEntity(playerData); // Save changes to Redis
    }
}
```

### 📢 Event Bus

Subscribe to and call custom events, or listen to native platform events.

```java
// 1. Define a custom event (e.g., com.yourcompany.events.PlayerLevelUpEvent.java)
package com.yourcompany.events;

import lib.homies.framework.events.LibEvent;
import lib.homies.framework.player.HomiesPlayer;
import lombok.Getter; // Requires Lombok

@Getter
public class PlayerLevelUpEvent extends LibEvent {
    private final HomiesPlayer player;
    private final int oldLevel;
    private final int newLevel;

    public PlayerLevelUpEvent(HomiesPlayer player, int oldLevel, int newLevel) {
        this.player = player;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }
}

// 2. Use the EventBus in your plugin
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import com.yourcompany.events.PlayerLevelUpEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPluginEventExample extends JavaPlugin implements Listener {

    // Assuming you have a way to get HomiesPlayer from a Bukkit Player
    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        // For Spigot:
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    @Override
    public void onEnable() {
        // Subscribe to a custom event
        HomiesLib.getEventBus().subscribe(PlayerLevelUpEvent.class, event -> {
            HomiesLib.getMessageUtils().send(event.getPlayer(),
                    "&aCongratulations! You leveled up from &e" + event.getOldLevel() + " &ato &e" + event.getNewLevel() + "&a!");
            getLogger().info(event.getPlayer().getName() + " leveled up!");
        });

        // Subscribe to a native Bukkit event (e.g., PlayerJoinEvent)
        // Note: For platform events, you still need to register your class as a Bukkit Listener
        // if you're using @EventHandler annotations for other events.
        HomiesLib.getEventBus().subscribePlatform(PlayerJoinEvent.class, event -> {
            HomiesLib.getMessageUtils().broadcast("&b" + event.getPlayer().getName() + " has joined the server!");
        });

        // Register this class as a Bukkit listener for the subscribePlatform example
        getServer().getPluginManager().registerEvents(this, this);
    }

    // Example of calling a custom event
    public void giveExperience(HomiesPlayer player, int amount) {
        int currentLevel = 1; // Get actual level from player data
        int newLevel = currentLevel + 1; // Calculate new level
        if (newLevel > currentLevel) {
            HomiesLib.getEventBus().call(new PlayerLevelUpEvent(player, currentLevel, newLevel));
        }
    }
}
```

### 🖼️ Menu System (Inventory GUIs)

Create dynamic, paginated, and animated inventory menus.

```java
// 1. Define your custom menu (e.yourcompany.menus.PlayerListMenu.java)
package com.yourcompany.menus;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.menu.AbstractPaginatedHomiesMenu;
import lib.homies.framework.menu.HomiesMenuItem;
import lib.homies.framework.menu.SimpleHomiesMenuItem;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.utils.HomiesItemStack;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerListMenu extends AbstractPaginatedHomiesMenu {

    public PlayerListMenu() {
        // Load players asynchronously when the menu is created
        // In a real scenario, you might pass the player who opened the menu
        // and fetch data relevant to them.
        HomiesLib.getSchedulerService().runAsync(() -> {
            List<HomiesMenuItem> playerItems = Bukkit.getOnlinePlayers().stream()
                    .map(p -> {
                        // For Spigot:
                        HomiesPlayer homiesPlayer = new lib.homies.framework.spigot.player.SpigotPlayer(p);
                        if (homiesPlayer == null) return null; // Should not happen for online players

                        // Create a menu item for each player
                        return new SimpleHomiesMenuItem(
                                HomiesLib.getItemBuilder(Material.PLAYER_HEAD.name())
                                        .name("&a" + homiesPlayer.getName())
                                        .lore("&7Click to view profile!")
                                        .setPlayerProfile(HomiesLib.getPlayerHeadUtils().createPlayerProfile(homiesPlayer.getUniqueId())) // Assuming this method exists
                                        .build(),
                                clickedPlayer -> {
                                    HomiesLib.getMessageUtils().send(clickedPlayer, "&aYou clicked on " + homiesPlayer.getName() + "!");
                                    // Open player profile menu, etc.
                                }
                        );
                    })
                    .filter(item -> item != null)
                    .collect(Collectors.toList());
            setPaginatedContentItems(playerItems);
        });
    }

    @Override
    public String getTitle(HomiesPlayer player) {
        return "&bOnline Players (Page " + (currentPage + 1) + "/" + getMaxPage() + ")";
    }

    @Override
    public int getSize() {
        return 54; // 6 rows
    }

    @Override
    protected int[] getPaginationSlots() {
        // Slots for items (rows 1-4)
        return new int[]{10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43};
    }

    @Override
    protected HomiesMenuItem getNextPageButton(HomiesPlayer player) {
        return new SimpleHomiesMenuItem(
                HomiesLib.getItemBuilder(Material.ARROW.name())
                        .name("&aNext Page")
                        .lore("&7Click to go to the next page.")
                        .build(),
                p -> {
                    nextPage();
                    HomiesLib.getMenuManager().openMenu(p, this); // Re-open to refresh
                }
        );
    }

    @Override
    protected HomiesMenuItem getPreviousPageButton(HomiesPlayer player) {
        return new SimpleHomiesMenuItem(
                HomiesLib.getItemBuilder(Material.ARROW.name())
                        .name("&aPrevious Page")
                        .lore("&7Click to go to the previous page.")
                        .build(),
                p -> {
                    previousPage();
                    HomiesLib.getMenuManager().openMenu(p, this); // Re-open to refresh
                }
        );
    }

    @Override
    protected HomiesMenuItem getLoadingItem() {
        return new SimpleHomiesMenuItem(
                HomiesLib.getItemBuilder(Material.CLOCK.name())
                        .name("&eLoading Players...")
                        .lore("&7Please wait.")
                        .build()
        );
    }

    @Override
    protected int getNextPageButtonSlot() {
        return 50; // Bottom right
    }

    @Override
    protected int getPreviousPageButtonSlot() {
        return 48; // Bottom left
    }

    @Override
    public Map<Integer, HomiesMenuItem> getItems(HomiesPlayer player) {
        Map<Integer, HomiesMenuItem> items = super.getItems(player);
        // Add any fixed items here (e.g., close button, filler glass)
        HomiesMenuItem filler = new SimpleHomiesMenuItem(
                HomiesLib.getItemBuilder(Material.GRAY_STAINED_GLASS_PANE.name())
                        .name(" ")
                        .build()
        );
        for (int i = 0; i < getSize(); i++) {
            items.putIfAbsent(i, filler);
        }
        return items;
    }

    // Example of opening this menu via a command
    // @Command(name = "players", playerOnly = true)
    // public void openPlayerList(HomiesPlayer sender) {
    //     HomiesLib.getMenuManager().openMenu(sender, new PlayerListMenu());
    // }
}
```

### 👤 Player Data Service

Manage platform-independent player data.

```java
// Assuming MyPlayerData.java from the Database example is used.

// In your plugin's main class or a service

import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.playerdata.PlayerData; // Base PlayerData from API
import com.yourcompany.data.MyPlayerData; // Your custom PlayerData
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MyPluginPlayerDataExample implements Listener {

    // In-memory cache for player data
    private final Map<UUID, MyPlayerData> cachedPlayerData = new HashMap<>();

    // Assuming you have a way to get HomiesPlayer from a Bukkit Player
    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        // For Spigot:
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        HomiesPlayer homiesPlayer = getHomiesPlayer(event.getPlayer());
        if (homiesPlayer == null) return;

        // Load data from database (or create new if not exists)
        MyPlayerData data = HomiesLib.getDatabaseService().findById(MyPlayerData.class, homiesPlayer.getUniqueId().toString())
                .orElseGet(() -> new MyPlayerData(homiesPlayer.getUniqueId().toString()));

        cachedPlayerData.put(homiesPlayer.getUniqueId(), data);
        HomiesLib.getMessageUtils().send(homiesPlayer, "&aYour current coins: &e" + data.getCoins());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HomiesPlayer homiesPlayer = getHomiesPlayer(event.getPlayer());
        if (homiesPlayer == null) return;

        MyPlayerData data = cachedPlayerData.remove(homiesPlayer.getUniqueId());
        if (data != null) {
            // Save updated data to database
            HomiesLib.getDatabaseService().saveEntity(data);
            HomiesLib.getMessageUtils().send(homiesPlayer, "&aYour data has been saved.");
        }
    }

    // Example of getting player data
    public Optional<MyPlayerData> getPlayerCoins(HomiesPlayer player) {
        return Optional.ofNullable(cachedPlayerData.get(player.getUniqueId()));
    }
}
```

### ⏳ Cooldowns Service

Manage time-based cooldowns for players.

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;

import java.util.concurrent.TimeUnit;

public class MyPluginCooldownExample implements Listener {

    private static final String ABILITY_COOLDOWN_KEY = "super_ability_cooldown";

    // Assuming you have a way to get HomiesPlayer from a Bukkit Player
    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        // For Spigot:
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || event.getItem().getType() != Material.BLAZE_ROD) {
            return;
        }

        HomiesPlayer homiesPlayer = getHomiesPlayer(event.getPlayer());
        if (homiesPlayer == null) return;

        if (HomiesLib.getCooldownService().isOnCooldown(homiesPlayer, ABILITY_COOLDOWN_KEY)) {
            HomiesLib.getCooldownService().getRemainingCooldown(homiesPlayer, ABILITY_COOLDOWN_KEY, TimeUnit.SECONDS)
                    .ifPresent(remaining -> {
                        HomiesLib.getMessageUtils().send(homiesPlayer, "&cYou must wait &e" + remaining + " seconds &cbefore using this again!");
                    });
            event.setCancelled(true);
            return;
        }

        // Use the ability
        HomiesLib.getMessageUtils().send(homiesPlayer, "&aYou used your super ability!");
        // Set a 10-second cooldown
        HomiesLib.getCooldownService().setCooldown(homiesPlayer, ABILITY_COOLDOWN_KEY, 10, TimeUnit.SECONDS);
    }
}
```

### 🥳 Parties Service

Create, manage, and interact with player parties.

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.party.Party;
import lib.homies.framework.player.HomiesPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Optional;

public class MyPluginPartyExample implements Listener {

    // Assuming you have a way to get HomiesPlayer from a Bukkit Player
    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        // For Spigot:
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    // Example: Command to create a party
    // @Command(name = "party", playerOnly = true)
    // public void partyCommand(HomiesPlayer sender) {
    //     HomiesLib.getMessageUtils().send(sender, "&cUsage: /party <create|invite|accept|leave>");
    // }

    // @SubCommand(value = "create", playerOnly = true)
    // public void createParty(HomiesPlayer sender) {
    //     Party newParty = HomiesLib.getPartyService().createParty(sender);
    //     if (newParty != null) {
    //         newParty.sendMessage("&a" + sender.getName() + " has created a party!");
    //     }
    // }

    // @SubCommand(value = "invite <player>", playerOnly = true)
    // public void invitePlayer(HomiesPlayer sender, HomiesPlayer target) {
    //     HomiesLib.getPartyService().invitePlayer(sender, target);
    // }

    // @SubCommand(value = "accept <player>", playerOnly = true)
    // public void acceptInvite(HomiesPlayer sender, HomiesPlayer inviter) {
    //     HomiesLib.getPartyService().acceptInvitation(sender, inviter);
    // }

    // @SubCommand(value = "leave", playerOnly = true)
    // public void leaveParty(HomiesPlayer sender) {
    //     HomiesLib.getPartyService().leaveParty(sender);
    // }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HomiesPlayer homiesPlayer = getHomiesPlayer(event.getPlayer());
        if (homiesPlayer == null) return;

        // If a player leaves, ensure they are removed from their party
        Optional<Party> partyOpt = HomiesLib.getPartyService().getPartyByPlayer(homiesPlayer);
        partyOpt.ifPresent(party -> {
            party.removeMember(homiesPlayer);
            if (party.getSize() == 0) {
                HomiesLib.getPartyService().disbandParty(party);
            }
        });
    }
}
```

### ✨ Utilities

HomiesLib provides various utility methods for common tasks.

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.utils.HomiesItemStack;
import lib.homies.framework.world.HomiesLocation;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MyPluginUtilityExample {

    // Assuming you have a way to get HomiesPlayer from a Bukkit Player
    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        // For Spigot:
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    public void exampleUsage(Player bukkitPlayer) {
        HomiesPlayer homiesPlayer = getHomiesPlayer(bukkitPlayer);
        if (homiesPlayer == null) return;

        // --- TextUtils ---
        String coloredText = HomiesLib.getTextUtils().colorize("&aHello &bWorld!");
        HomiesLib.getMessageUtils().send(homiesPlayer, coloredText); // Uses MessageUtils

        String gradientText = HomiesLib.getTextUtils().gradient("HomiesLib", "RED", "BLUE");
        HomiesLib.getMessageUtils().send(homiesPlayer, gradientText);

        // --- MessageUtils ---
        HomiesLib.getMessageUtils().broadcast("&eEveryone, look at &a" + homiesPlayer.getName() + "&e!");
        HomiesLib.getMessageUtils().actionBar(homiesPlayer, "&fYou are doing great!");
        HomiesLib.getMessageUtils().title(homiesPlayer, "&6Welcome", "&7To the server!", 10, 70, 20);

        // --- ItemBuilder & PlayerHeadUtils ---
        HomiesItemStack customItem = HomiesLib.getItemBuilder(Material.DIAMOND_SWORD.name())
                .name("&cLegendary Sword")
                .lore("&7A sword of legends.", "&7Damage: &c+10")
                .enchant("SHARPNESS", 5)
                .flag("HIDE_ENCHANTS")
                .build();
        // Give item to player (platform-specific)
        // You need to cast HomiesItemStack back to the platform-specific ItemStack
        org.bukkit.inventory.ItemStack bukkitItemStack = customItem.getAs(org.bukkit.inventory.ItemStack.class);
        if (bukkitItemStack != null) {
            bukkitPlayer.getInventory().addItem(bukkitItemStack);
        }

        HomiesItemStack playerHead = HomiesLib.getPlayerHeadUtils().createPlayerHead(homiesPlayer.getUniqueId());
        org.bukkit.inventory.ItemStack playerHeadItemStack = playerHead.getAs(org.bukkit.inventory.ItemStack.class);
        if (playerHeadItemStack != null) {
            bukkitPlayer.getInventory().addItem(playerHeadItemStack);
        }

        // --- LocationUtils ---
        Location bukkitLoc = bukkitPlayer.getLocation();
        // Create a platform-specific HomiesLocation wrapper
        HomiesLocation homiesLoc;
        // For Spigot:
        homiesLoc = new lib.homies.framework.spigot.world.SpigotLocation(bukkitLoc);

        String serializedLoc = HomiesLib.getLocationUtils().serialize(homiesLoc);
        HomiesLib.getMessageUtils().send(homiesPlayer, "&aYour location: &f" + serializedLoc);

        HomiesLocation deserializedLoc = HomiesLib.getLocationUtils().deserialize(serializedLoc);
        if (deserializedLoc != null && HomiesLib.getLocationUtils().isSafe(deserializedLoc)) {
            HomiesLib.getLocationUtils().safeTeleport(homiesPlayer, deserializedLoc);
            HomiesLib.getMessageUtils().send(homiesPlayer, "&aTeleported to saved location!");
        }
    }
}
```

### 🎮 Minigames (Abstract Core)

The `MinigameManager` and `AbstractMiniGame` provide a core structure for building minigames.

```java
// 1. Define your custom minigame (e.g., com.yourcompany.minigames.MyAwesomeMinigame.java)
package com.yourcompany.minigames;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.minigame.AbstractMiniGame;
import lib.homies.framework.minigame.MiniGameState;
import lib.homies.framework.player.HomiesPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MyAwesomeMinigame extends AbstractMiniGame {

    private final World gameWorld;
    private final Location lobbySpawn;
    private final Location gameSpawn;

    public MyAwesomeMinigame(String name, World gameWorld, Location lobbySpawn, Location gameSpawn) {
        super(name);
        this.gameWorld = gameWorld;
        this.lobbySpawn = lobbySpawn;
        this.gameSpawn = gameSpawn;
        setState(MiniGameState.WAITING); // Initial state
    }

    @Override
    protected void onStart() {
        HomiesLib.getMessageUtils().broadcast("&a" + getName() + " is starting!");
        getMembers().forEach(p -> {
            Player bukkitPlayer = p.getAs(Player.class);
            if (bukkitPlayer != null) {
                bukkitPlayer.teleport(gameSpawn);
                HomiesLib.getMessageUtils().send(p, "&aWelcome to " + getName() + "!");
            }
        });
        // Start game logic, timers, etc.
    }

    @Override
    protected void onStop() {
        HomiesLib.getMessageUtils().broadcast("&c" + getName() + " has ended!");
        getMembers().forEach(p -> {
            Player bukkitPlayer = p.getAs(Player.class);
            if (bukkitPlayer != null) {
                bukkitPlayer.teleport(lobbySpawn); // Teleport back to lobby
                HomiesLib.getMessageUtils().send(p, "&cThanks for playing " + getName() + "!");
            }
        });
        // Clean up game resources
    }

    @Override
    protected void onPlayerJoin(HomiesPlayer player) {
        HomiesLib.getMessageUtils().send(player, "&aYou joined " + getName() + "!");
        Player bukkitPlayer = player.getAs(Player.class);
        if (bukkitPlayer != null) {
            bukkitPlayer.teleport(lobbySpawn);
        }
        // Give kit, set up player state
    }

    @Override
    protected void onPlayerLeave(HomiesPlayer player) {
        HomiesLib.getMessageUtils().send(player, "&cYou left " + getName() + ".");
        // Reset player state, remove from game
    }

    // Custom game logic methods can be added here
    public void addSpectator(HomiesPlayer player) {
        // ...
    }
}

// 2. Register and manage minigames
import lib.homies.framework.HomiesLib;
import com.yourcompany.minigames.MyAwesomeMinigame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPluginMinigameExample extends JavaPlugin {

    private MyAwesomeMinigame activeMinigame;

    @Override
    public void onEnable() {
        // Example: Create and register a minigame
        World world = Bukkit.getWorld("world"); // Or get your specific game world
        if (world == null) {
            getLogger().severe("Game world 'world' not found!");
            return;
        }
        Location lobby = new Location(world, 0, 100, 0);
        Location gameStart = new Location(world, 100, 100, 100);

        activeMinigame = new MyAwesomeMinigame("Deathmatch", world, lobby, gameStart);
        HomiesLib.getMiniGameManager().registerGame(activeMinigame);

        getLogger().info("Minigame 'Deathmatch' registered!");

        // Example: Start the minigame after some time
        HomiesLib.getSchedulerService().task()
                .run(() -> {
                    if (activeMinigame.getState() == MiniGameState.WAITING) {
                        activeMinigame.start();
                    }
                })
                .delay(20 * 60L) // Start after 1 minute
                .start();
    }

    @Override
    public void onDisable() {
        if (activeMinigame != null && activeMinigame.getState() != MiniGameState.STOPPED) {
            activeMinigame.stop();
        }
        HomiesLib.getMiniGameManager().unregisterGame(activeMinigame.getName());
    }
}
```

---

## 🌳 Project Structure: Clean & Modular

```
HomiesLib/
├── pom.xml
├── homieslib-api/          (Interfaces, DTOs, Annotations - The Universal Blueprint)
├── homieslib-core/         (Platform-Agnostic Core Logic - Shared Functionality)
├── homieslib-spigot/       (Spigot/Paper Implementation - The Engine's Guts)
├── homieslib-processor/    (Annotation Processor - Code Generation Wizard)
└── homieslib-assembly/     (Build Assembly - Packages Platform-Specific JARs)
```

---

## ⚙️ Setup & Build (For Framework Developers)

From the repository root:

*   **Build all modules:** `mvn clean install`
*   **Build without tests:** `mvn -DskipTests clean install`

Artifacts will be in each module’s `target` directory.

---

## 📜 License

This project is licensed under the MIT License. See the `LICENSE` file in the repository root.

---

## 🤝 Contributing

Contributions, issues, and feature requests are always welcome! Let's make HomiesLib the best framework out there!

*   Open an issue describing the change.
*   Keep PRs focused and include minimal reproducible examples.
*   Follow the module structure and Java 17 compatibility.

---