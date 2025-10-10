# ✨ HomiesLib: The Ultimate Minecraft Plugin Framework ✨

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/your-username/HomiesLib/actions)
[![Version](https://img.shields.io/badge/version-1.0.0-blue)](https://github.com/your-username/HomiesLib/releases)
[![License](https://img.shields.io/badge/license-MIT-lightgrey)](https://github.com/your-username/HomiesLib/blob/main/LICENSE)

**HomiesLib** is a powerful, multi-platform framework designed for Minecraft plugin developers who want to build
high-performance, feature-rich plugins with unparalleled simplicity. By providing a clean, API-driven architecture,
HomiesLib takes the pain out of boilerplate code, letting you focus on innovation. Whether you're a solo developer or
part of a team, this framework is built to make your development experience a joy.

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

* **💬 Annotation-Driven Commands:** A fully automatic command system. Define commands, subcommands, permissions, and
  tab-completion with simple annotations.
* **⚙️ Effortless Configuration:** Easily manage YAML configurations using annotation-based POJOs (Plain Old Java
  Objects).
* **📢 Powerful Event Bus:** A robust event system for both custom `LibEvent`s and native platform events (e.g., Bukkit
  events).
* **🖼️ Dynamic GUI Menus:** Create complex, paginated, and animated inventory GUIs.
* **⏱️ Flexible Task Scheduling:** A clean and fluent API for scheduling synchronous and asynchronous tasks, with
  support for delays and repeating intervals.
* **💾 Multi-Backend Database Service:** A powerful ORM with out-of-the-box support for **Redis**, **MySQL**, and *
  *SQLite**. Features an intelligent caching layer to minimize database load.
* **🎮 Abstracted Minigame Core:** A foundational structure to build and manage complex minigame logic, states, and
  player sessions.
*   **👤 Player Data:** Platform-independent service for managing player-specific data.
*   **⏳ Cooldowns:** Efficiently manage time-based restrictions for player actions.
*   **🥳 Parties:** Robust system for creating and managing player groups.
* **✨ Platform-Agnostic Utilities:** A rich set of helpers for creating items (`ItemBuilder`), colorizing text (
  `TextUtils`), managing player heads, and more, all designed to be independent of the server platform.

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

This section provides a detailed look at how to use the core systems of HomiesLib.

### ⚙️ Configuration Management

Define your `config.yml` structure in a simple Java class.

```java
// 1. Define the configuration class
package com.yourcompany.config;

import lib.homies.framework.config.annotations.ConfigFile;
import lib.homies.framework.config.annotations.ConfigKey;

@ConfigFile(fileName = "settings.yml")
public class PluginSettings {
    @ConfigKey(path = "welcome.message", comment = "Message sent to players on join.")
    public String welcomeMessage = "&aWelcome, %player_name%!";

    @ConfigKey(path = "features.pvp-enabled")
    public boolean pvpEnabled = true;
}

// 2. Load it in your plugin's onEnable
import lib.homies.framework.HomiesLib;
import lib.homies.framework.PluginContext;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPlugin extends JavaPlugin {
    private PluginSettings settings;

    @Override
    public void onEnable() {
        // For Spigot/Paper:
        PluginContext pluginContext = new lib.homies.framework.spigot.SpigotPluginContext(this);

        // Load the configuration
        settings = HomiesLib.getConfigManager().loadConfig(pluginContext, PluginSettings.class);

        // Now you can use it
        getLogger().info("PvP Enabled: " + settings.pvpEnabled);
    }
}
```

### 📢 Event Bus

Subscribe to custom framework events or native platform events.

```java
// 1. Define a custom event
package com.yourcompany.events;

import lib.homies.framework.events.LibEvent;
import lib.homies.framework.player.HomiesPlayer;
import lombok.Getter;

@Getter
public class PlayerRankUpEvent extends LibEvent {
    private final HomiesPlayer player;
    private final String oldRank;
    private final String newRank;

    public PlayerRankUpEvent(HomiesPlayer player, String oldRank, String newRank) {
        this.player = player;
        this.oldRank = oldRank;
        this.newRank = newRank;
    }
}

// 2. Subscribe to events in your onEnable
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import com.yourcompany.events.PlayerRankUpEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MyPluginEventExample extends JavaPlugin implements Listener {

    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    @Override
    public void onEnable() {
        // Subscribe to a custom event
        HomiesLib.getEventBus().subscribe(PlayerRankUpEvent.class, event -> {
            HomiesLib.getMessageUtils().broadcast(
                    "&e" + event.getPlayer().getName() + " has ranked up from &7" + event.getOldRank() + " &eto &a" + event.getNewRank() + "&e!"
            );
            getLogger().info(event.getPlayer().getName() + " ranked up!");
        });

        // Subscribe to a native Bukkit event
        HomiesLib.getEventBus().subscribePlatform(PlayerJoinEvent.class, event -> {
            HomiesLib.getMessageUtils().broadcast("&b" + event.getPlayer().getName() + " has joined the server!");
        });
    }

    // Example of calling a custom event
    public void giveRank(Player bukkitPlayer, String newRank) {
        HomiesPlayer player = getHomiesPlayer(bukkitPlayer);
        String currentRank = "Member"; // Get actual rank from player data
        if (!currentRank.equals(newRank)) {
            HomiesLib.getEventBus().call(new PlayerRankUpEvent(player, currentRank, newRank));
        }
    }
}
```

### 💬 Command System

Define a full command with subcommands, arguments, permissions, and tab-completion in a single class.

```java
package com.yourcompany.commands;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.command.annotations.Command;
import lib.homies.framework.command.annotations.SubCommand;
import lib.homies.framework.command.annotations.TabComplete;
import lib.homies.framework.player.HomiesPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "gamemode", description = "Set a player's gamemode.", aliases = {"gm"}, permission = "myplugin.gamemode")
public class GamemodeCommand {

    // Default executor for /gamemode
    @Command(playerOnly = true)
    public void execute(HomiesPlayer sender) {
        HomiesLib.getMessageUtils().send(sender, "&cUsage: /gamemode <creative|survival> [player]");
    }

    // Subcommand: /gamemode creative
    @SubCommand(value = "creative", aliases = {"1", "c"}, permission = "myplugin.gamemode.creative")
    public void onCreative(HomiesPlayer sender) {
        Player bukkitPlayer = sender.getAs(Player.class);
        if (bukkitPlayer != null) {
            bukkitPlayer.setGameMode(GameMode.CREATIVE);
            HomiesLib.getMessageUtils().send(sender, "&aYour gamemode is now &eCreative&a.");
        }
    }

    // Subcommand with argument: /gamemode creative <player>
    @SubCommand(value = "creative <player>", permission = "myplugin.gamemode.creative.other")
    public void onCreativeOther(HomiesPlayer sender, HomiesPlayer target) {
        Player targetPlayer = target.getAs(Player.class);
        if (targetPlayer != null) {
            targetPlayer.setGameMode(GameMode.CREATIVE);
            HomiesLib.getMessageUtils().send(sender, "&aSet &b" + target.getName() + "'s &agamemode to &eCreative&a.");
            HomiesLib.getMessageUtils().send(target, "&aYour gamemode was set by &b" + sender.getName() + "&a.");
        }
    }

    // Tab completion for the first argument
    @TabComplete("")
    public List<String> completeBase(CommandSender sender) {
        return Arrays.asList("creative", "survival", "spectator", "adventure");
    }

    // Tab completion for the player argument
    @TabComplete("creative")
    @TabComplete("survival")
    public List<String> completePlayer(CommandSender sender) {
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
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
import org.bukkit.plugin.java.JavaPlugin;

public class MyPluginSchedulerExample extends JavaPlugin {

    private HomiesTask repeatingTask;

    @Override
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

    @Override
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

### 💾 Database Service (Cross-Server Data)

Store player data in a central database (Redis, MySQL, or SQLite) to share it across multiple servers.

```java
// 1. Define your data entity
package com.yourcompany.data;

import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@DbEntity(tableName = "player_profiles")
public class PlayerProfile {
    @DbField(id = true)
    private String uuid;

    @DbField
    private long coins = 0;

    @DbField
    private boolean muted = false;

    public PlayerProfile(UUID uuid) {
        this.uuid = uuid.toString();
    }
}

// 2. Use it to manage cross-server state
import lib.homies.framework.HomiesLib;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin; // Import JavaPlugin

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MyPluginDatabaseExample extends JavaPlugin implements org.bukkit.event.Listener { // Implement Bukkit Listener

    private final Map<UUID, PlayerProfile> cachedPlayerProfiles = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();

        // Asynchronously fetch the player's profile from the central database
        HomiesLib.getSchedulerService().runAsync(() -> {
            PlayerProfile profile = HomiesLib.getDatabaseService()
                    .findById(PlayerProfile.class, uuid)
                    .orElse(new PlayerProfile(event.getPlayer().getUniqueId())); // Create if it doesn't exist

            cachedPlayerProfiles.put(event.getPlayer().getUniqueId(), profile); // Cache locally

            if (profile.isMuted()) {
                // This player is muted network-wide!
                // Apply mute logic on this server...
                event.getPlayer().sendMessage("§cYou are muted network-wide!");
            }
            event.getPlayer().sendMessage("§aWelcome back! You have §e" + profile.getCoins() + " coins.");
        });
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent event) {
        PlayerProfile profile = cachedPlayerProfiles.remove(event.getPlayer().getUniqueId());
        if (profile != null) {
            // Example: Update coins on quit
            profile.setCoins(profile.getCoins() + 10);
            HomiesLib.getSchedulerService().runAsync(() -> {
                HomiesLib.getDatabaseService().saveEntity(profile);
            });
        }
    }
}
```

### 👤 Player Data Service

Manage platform-independent player data.

```java
// Assuming PlayerProfile.java from the Database example is used.

// In your plugin's main class or a service
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import com.yourcompany.data.PlayerProfile; // Your custom PlayerData
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class MyPluginPlayerDataExample extends JavaPlugin implements Listener {

    // In-memory cache for player data
    private final Map<UUID, PlayerProfile> cachedPlayerProfiles = new HashMap<>();

    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @org.bukkit.event.EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        HomiesPlayer homiesPlayer = getHomiesPlayer(event.getPlayer());

        HomiesLib.getSchedulerService().runAsync(() -> {
            PlayerProfile profile = HomiesLib.getDatabaseService()
                    .findById(PlayerProfile.class, homiesPlayer.getUniqueId().toString())
                    .orElseGet(() -> new PlayerProfile(homiesPlayer.getUniqueId()));

            cachedPlayerProfiles.put(homiesPlayer.getUniqueId(), profile);
            HomiesLib.getMessageUtils().send(homiesPlayer, "&aWelcome back! You have &e" + profile.getCoins() + " coins.");
        });
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        HomiesPlayer homiesPlayer = getHomiesPlayer(event.getPlayer());
        PlayerProfile profile = cachedPlayerProfiles.remove(homiesPlayer.getUniqueId());
        if (profile != null) {
            // Example: Update coins on quit
            profile.setCoins(profile.getCoins() + 10);
            HomiesLib.getSchedulerService().runAsync(() -> {
                HomiesLib.getDatabaseService().saveEntity(profile);
            });
        }
    }

    // Example of getting player data
    public Optional<PlayerProfile> getPlayerProfile(HomiesPlayer player) {
        return Optional.ofNullable(cachedPlayerProfiles.get(player.getUniqueId()));
    }
}
```

### ⏳ Cooldowns Service

Manage time-based cooldowns for players.

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import org.bukkit.entity.Player; // Import Player
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin; // Import JavaPlugin

import java.util.concurrent.TimeUnit;

public class MyPluginCooldownExample extends JavaPlugin implements Listener {

    private static final String ABILITY_COOLDOWN_KEY = "super_ability_cooldown";

    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @org.bukkit.event.EventHandler
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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class MyPluginPartyExample extends JavaPlugin implements Listener {

    private HomiesPlayer getHomiesPlayer(Player bukkitPlayer) {
        return new lib.homies.framework.spigot.player.SpigotPlayer(bukkitPlayer);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        getServer().getPluginManager().registerEvents(this, this);
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

    @org.bukkit.event.EventHandler
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

### 🖼️ GUI Menu System

Create a dynamic, multi-page menu.

```java
package com.yourcompany.menus;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.menu.AbstractPaginatedHomiesMenu;
import lib.homies.framework.menu.HomiesMenuItem;
import lib.homies.framework.menu.SimpleHomiesMenuItem;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.spigot.player.SpigotPlayer;
import lib.homies.framework.utils.HomiesItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player; // Import Player

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PlayerListMenu extends AbstractPaginatedHomiesMenu {

    public PlayerListMenu() {
        // Asynchronously populate the menu items
        HomiesLib.getSchedulerService().runAsync(() -> {
            List<HomiesMenuItem> playerItems = Bukkit.getOnlinePlayers().stream()
                    .map(p -> {
                        HomiesPlayer homiesPlayer = new SpigotPlayer(p);
                        HomiesItemStack playerHead = HomiesLib.getPlayerHeadUtils().createPlayerHead(homiesPlayer.getUniqueId());

                        return new SimpleHomiesMenuItem(
                                HomiesLib.getItemBuilder(playerHead)
                                        .name("&a" + homiesPlayer.getName())
                                        .lore("&7Click to view profile!")
                                        .build(),
                                clickedPlayer -> {
                                    HomiesLib.getMessageUtils().send(clickedPlayer, "&aYou clicked on " + homiesPlayer.getName() + "!");
                                    // Open player profile menu, etc.
                                }
                        );
                    })
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
        return new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
    }

    @Override
    protected HomiesMenuItem getNextPageButton(HomiesPlayer player) {
        return new SimpleHomiesMenuItem(
                HomiesLib.getItemBuilder(Material.ARROW.name()).name("§aNext Page").build(),
                p -> {
                    nextPage();
                    HomiesLib.getMenuManager().openMenu(p, this);
                }
        );
    }

    @Override
    protected HomiesMenuItem getPreviousPageButton(HomiesPlayer player) {
        return new SimpleHomiesMenuItem(
                HomiesLib.getItemBuilder(Material.ARROW.name()).name("§cPrevious Page").build(),
                p -> {
                    previousPage();
                    HomiesLib.getMenuManager().openMenu(p, this);
                }
        );
    }

    @Override
    protected int getNextPageButtonSlot() {
        return 50;
    }

    @Override
    protected int getPreviousPageButtonSlot() {
        return 48;
    }

    @Override
    public Map<Integer, HomiesMenuItem> getItems(HomiesPlayer player) {
        Map<Integer, HomiesMenuItem> items = super.getItems(player);
        HomiesMenuItem filler = new SimpleHomiesMenuItem(HomiesLib.getItemBuilder(Material.GRAY_STAINED_GLASS_PANE.name()).name(" ").build());
        for (int i = 0; i < getSize(); i++) {
            items.putIfAbsent(i, filler);
        }
        return items;
    }
}
```

### 🎮 Minigames (Abstract Core)

The `MinigameManager` and `AbstractMiniGame` provide a core structure for building minigames.

```java
package com.yourcompany.minigames;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.minigame.AbstractMiniGame;
import lib.homies.framework.minigame.MiniGameState;
import lib.homies.framework.player.HomiesPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player; // Import Player
import org.bukkit.plugin.java.JavaPlugin; // Import JavaPlugin

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
        super.onEnable(); // Call superclass onEnable
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
        super.onDisable(); // Call superclass onDisable
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

This project is licensed under the **MIT License**. See the `LICENSE` file for full details.

---

## 🤝 Contributing

This is an open-source project, and contributions are always welcome! Whether it's reporting a bug, suggesting a new
feature, or submitting a code change, your input is valued.

* **Bug Reports & Feature Requests:** Please open an issue on the GitHub Issues page.
* **Code Contributions:** Please follow the standard Fork & Pull Request workflow.
    1. Fork the repository.
    2. Create a new branch for your feature or fix.
    3. Make your changes and commit them with a clear message.
    4. Push your branch and open a Pull Request.

---
