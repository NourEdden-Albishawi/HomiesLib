# ✨ HomiesLib: The Ultimate Minecraft Plugin Framework ✨

Welcome to **HomiesLib** – your go-to, multi-platform framework for crafting high-performance, feature-rich Minecraft plugins with unparalleled simplicity! 🚀 Designed for third-party developers, HomiesLib takes the pain out of plugin development, letting you focus on innovation, not boilerplate.

---

## 🌟 Overview: Build Smarter, Not Harder!

HomiesLib is a modular Java framework built to power Minecraft plugins across various platforms (currently Paper/Spigot, with more to come!). We've meticulously engineered a clean, API-driven architecture that prioritizes:

*   **⚡ Performance:** Optimized systems to keep your server running smoothly.
*   **✨ Simplicity:** Intuitive APIs and powerful abstractions for rapid development.
*   **🌍 Multi-Platform Support:** Write your core logic once, deploy it everywhere!
*   **🛠️ Modularity:** Only use what you need, keep your plugins lean.

Say goodbye to tangled code and hello to a framework that makes development a joy! 🎉

---

## 🚀 Key Features at a Glance

HomiesLib provides robust, abstracted systems for:

*   **💬 Commands:** Annotation-driven command registration with automatic dispatcher generation.
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
    Include the `homieslib-api` and the platform-specific implementation (e.g., `homieslib-spigot`) as `compile` dependencies.

    ```xml
    <dependencies>
        <!-- Your plugin's other dependencies -->

        <dependency>
            <groupId>lib.homies.framework</groupId>
            <artifactId>homieslib-api</artifactId>
            <version>1.0-SNAPSHOT</version> <!-- Use your current version -->
            <scope>compile</scope>
        </dependency>

        <dependency>
            <groupId>lib.homies.framework</groupId>
            <artifactId>homieslib-spigot</artifactId>
            <version>1.0-SNAPSHOT</version> <!-- Use your current version -->
            <scope>compile</scope>
        </dependency>
    </dependencies>
    ```

2.  **Configure Maven for Annotation Processing (Crucial for Commands & other features):**
    If your plugin uses annotation-driven features (like the Command System with `@Command` and `@SubCommand`), you *must* configure your plugin's `pom.xml` to run the `homieslib-processor`. This ensures that necessary boilerplate code is generated during compilation.

    Add the `homieslib-processor` as a `provided` dependency and configure the `maven-compiler-plugin` and `build-helper-maven-plugin` in your plugin's `pom.xml` like so:

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
            <!-- Add generated sources to the build path -->
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <version>3.6.0</version> <!-- Use a recent version -->
                <executions>
                    <execution>
                        <id>add-source</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>add-source</goal>
                        </goals>
                        <configuration>
                            <sources>
                                <source>${project.build.directory}/generated-sources/annotations</source>
                            </sources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.14.0</version> <!-- Use a recent version -->
                <configuration>
                    <source>17</source> <!-- Match your project's Java version -->
                    <target>17</target> <!-- Match your project's Java version -->
                    <annotationProcessorPaths>
                        <!-- Lombok processor (if you use Lombok) -->
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.38</version> <!-- Match your Lombok version -->
                        </path>
                        <!-- HomiesLib's annotation processor -->
                        <path>
                            <groupId>lib.homies.framework</groupId>
                            <artifactId>homieslib-processor</artifactId>
                            <version>1.0-SNAPSHOT</version> <!-- Use your current version -->
                        </path>
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
    api-version: 1.19 # Or your target Minecraft API version

    depend:
      - HomiesLibSpigot # This tells the server to load your framework first!
    ```

4.  **Access Services via `HomiesLib`:**
    All framework features are available through the static `lib.homies.framework.HomiesLib` class.

    ```java
    import lib.homies.framework.HomiesLib;
    // ... other imports
    ```

---

## 🚀 Usage Examples: See the Magic in Action!

### 💬 Command System

Define each command in its own class using the `@Command` annotation. The framework will automatically handle registration and sub-command dispatching.

**1. Define Your Command Class:**

Create a separate class for each main command.

```java
// In your plugin's source code (e.g., com.yourcompany.commands.HealCommand.java)
package com.yourcompany.commands;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.command.annotations.Command;
import lib.homies.framework.command.annotations.SubCommand;
import lib.homies.framework.player.HomiesPlayer;

@Command(name = "heal", description = "Heals a player", aliases = {"feed"})
public class HealCommand {

    // This method acts as the executor for the main /heal command
    public void onHealCommand(HomiesPlayer sender) {
        // In a real scenario, you would get the underlying player and set their health.
        sender.setHealth(sender.getMaxHealth()); 
        HomiesLib.getMessageUtils().send(sender, "&aYou have been healed!");
    }

    // A subcommand: /heal <player>
    @SubCommand("other")
    @Permission(value = "myplugin.heal.other", message = "&cYou do not have permission to heal other players.")
    @Async
    public void onHealOtherCommand(HomiesPlayer sender, HomiesPlayer target) {
        // This code will only run if the sender has the 'myplugin.heal.other' permission.
        // It will also run off the main server thread.

        // Pretend to do a slow database lookup...
        // Database.logHealEvent(sender, target); 

        HomiesLib.getMessageUtils().send(sender, "&aYou have healed " + target.getName() + ".");
        HomiesLib.getMessageUtils().send(target, "&aYou have been healed by " + sender.getName() + ".");
        
        // Since this is async, you need to schedule a sync task to modify the player
        // Bukkit.getScheduler().runTask(plugin, () -> {
        //     target.setHealth(target.getMaxHealth());
        // });
    }
}
```

**2. Register Your Command:**

In your plugin's `onEnable()` method, create an instance of your command class and register it.

```java
// In your main plugin class
import org.bukkit.plugin.java.JavaPlugin;
import com.yourcompany.commands.HealCommand;
import lib.homies.framework.HomiesLib;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Register your command class instance
        HomiesLib.getCommandManager().registerCommand(new HealCommand());
        getLogger().info("Heal command registered!");
    }
}
```

### ⚙️ Configuration System

Define your plugin's configuration using simple POJOs and annotations. HomiesLib handles loading, saving, and default values!

```java
// In your plugin's source code (e.g., com.yourcompany.MyConfig.java)
package com.yourcompany;

import lib.homies.framework.config.annotations.ConfigFile;
import lib.homies.framework.config.annotations.ConfigKey;

@ConfigFile(fileName = "config.yml")
public class MyConfig {

    @ConfigKey(columnName = "welcome-message")
    public String welcomeMessage = "&aWelcome to the server, %player%!";

    @ConfigKey(columnName = "max-players")
    public int maxPlayers = 100;

    @ConfigKey(columnName = "feature-enabled")
    public boolean featureEnabled = true;
}
```

**Loading and Saving (in your plugin's `onEnable()`):**

```java
import org.bukkit.plugin.java.JavaPlugin;
import lib.homies.framework.HomiesLib;
import com.yourcompany.MyConfig;

public class MyPlugin extends JavaPlugin {
    private MyConfig config;

    @Override
    public void onEnable() {
        // Load your configuration
        config = HomiesLib.getConfigManager().loadConfig(MyConfig.class);
        getLogger().info("Welcome Message: " + config.welcomeMessage);

        // Modify and save (e.g., onDisable or after a /reload command)
        config.welcomeMessage = "&bHello from the updated config!";
        HomiesLib.getConfigManager().saveConfig(config);
    }
}
```

### 📢 Event Handling System

Subscribe to both custom framework events and native platform events with a unified API!

```java
// 1. Define a custom event (in your plugin's API or core module)
package com.yourcompany.events;

import lib.homies.framework.events.LibEvent;
import lib.homies.framework.player.HomiesPlayer;

public class PlayerJoinCustomEvent extends LibEvent {
    private final HomiesPlayer player;
    public PlayerJoinCustomEvent(HomiesPlayer player) { this.player = player; }
    public HomiesPlayer getPlayer() { return player; }
}
```

```java
// 2. Subscribe to events (in your plugin's onEnable() or a listener class)
import org.bukkit.event.player.PlayerJoinEvent; // Native Bukkit event
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.spigot.player.SpigotPlayer; // For casting
import com.yourcompany.MyEventListener;

public class MyEventListener {

    public MyEventListener() {
        // Subscribe to a custom HomiesLib event
        HomiesLib.getEventBus().subscribe(PlayerJoinCustomEvent.class, event -> {
            HomiesLib.getMessageUtils().send(event.getPlayer(), "&6A custom event just fired!");
        });

        // Subscribe to a native Bukkit event
        HomiesLib.getEventBus().subscribePlatform(PlayerJoinEvent.class, event -> {
            HomiesPlayer homiesPlayer = new SpigotPlayer(event.getPlayer()); // Wrap native player
            HomiesLib.getMessageUtils().send(homiesPlayer, "&aWelcome, " + homiesPlayer.getName() + "!");
            // You can also call your custom events from here!
            HomiesLib.getEventBus().call(new PlayerJoinCustomEvent(homiesPlayer));
        });
    }
}
```

**Initialize your listener (in your plugin's `onEnable()`):**

```java
import org.bukkit.plugin.java.JavaPlugin;
import com.yourcompany.MyEventListener;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        new MyEventListener(); // Register event handlers
        getLogger().info("MyPlugin event listeners registered!");
    }
}
```

### 🖼️ Menu System

Create dynamic, paginated, and animated inventory GUIs with ease!

#### Simple Action Menu

For menus where each item triggers a specific action without pagination.

```java
// 1. Define your custom menu (e.g., com.yourcompany.MySimpleActionMenu.java)
package com.yourcompany;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.menu.AbstractHomiesMenu;
import lib.homies.framework.menu.HomiesMenuItem;
import lib.homies.framework.menu.SimpleHomiesMenuItem;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.utils.HomiesItemStack;

import java.util.HashMap;
import java.util.Map;

public class MySimpleActionMenu extends AbstractHomiesMenu {

    private final HomiesPlayer owner;

    public MySimpleActionMenu(HomiesPlayer owner) {
        this.owner = owner;
    }

    @Override
    public String getTitle(HomiesPlayer player) {
        return "&8Select an Action"; // Title of the inventory menu
    }

    @Override
    public int getSize() {
        return 27; // A 3x9 inventory size
    }

    @Override
    public Map<Integer, HomiesMenuItem> getItems(HomiesPlayer player) {
        Map<Integer, HomiesMenuItem> items = new HashMap<>();

        // --- Item 1: Teleport to Spawn ---
        HomiesItemStack spawnItem = HomiesLib.getItemBuilder("GRASS_BLOCK")
                .name("&aTeleport to Spawn")
                .lore("&7Click to go to the server spawn.")
                .build();
        items.put(10, new SimpleHomiesMenuItem(spawnItem, p -> {
            HomiesLib.getMessageUtils().send(p, "&aTeleporting you to spawn!");
            // In a real scenario, you'd use a platform-specific teleport method
            // e.g., p.teleport(HomiesLib.getPlatform().getSpawnLocation());
            HomiesLib.getMenuManager().closeMenu(p); // Close menu after action
        }));

        // --- Item 2: Give a Diamond ---
        HomiesItemStack diamondItem = HomiesLib.getItemBuilder("DIAMOND")
                .name("&bGet a Diamond")
                .lore("&7Click to receive a shiny diamond!")
                .build();
        items.put(13, new SimpleHomiesMenuItem(diamondItem, p -> {
            HomiesLib.getMessageUtils().send(p, "&bHere's a diamond for you!");
            // In a real scenario, you'd add the item to the player's inventory
            // e.g., p.getInventory().addItem(HomiesLib.getItemBuilder("DIAMOND").build());
            HomiesLib.getMenuManager().closeMenu(p); // Close menu after action
        }));

        // --- Item 3: Open another menu (e.g., your paginated menu) ---
        HomiesItemStack nextMenu = HomiesLib.getItemBuilder("BOOK")
                .name("&eOpen Awesome Picks Menu")
                .lore("&7Click to see a list of awesome pickaxes.")
                .build();
        items.put(16, new SimpleHomiesMenuItem(nextMenu, p -> {
            HomiesLib.getMessageUtils().send(p, "&eOpening the Awesome Picks menu...");
            // Assuming MyAwesomeMenu is your paginated menu from the README example
            HomiesLib.getMenuManager().openMenu(p, new MyAwesomeMenu(p));
        }));

        // --- Filler Item (optional) ---
        HomiesItemStack filler = HomiesLib.getItemBuilder("GRAY_STAINED_GLASS_PANE")
                .name(" ") // Empty name to make it look clean
                .build();
        // Fill remaining slots if desired, for example, the border
        for (int i = 0; i < getSize(); i++) {
            if (!items.containsKey(i)) {
                items.put(i, new SimpleHomiesMenuItem(filler, p -> { /* Do nothing on click */ }));
            }
        }

        return items;
    }

    // You can override these if you want specific sounds or animations for this menu
    // @Override
    // public long getAnimationDelayTicks() { return 1; }
    // @Override
    // public String getAnimationSound() { return "BLOCK_NOTE_BLOCK_PLING"; }
}
```

**Opening the Simple Action Menu (e.g., from a command):**

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.spigot.player.SpigotPlayer; // For casting from Bukkit Player
import org.bukkit.entity.Player;

// ... inside a command method or event handler ...

public void openMyActionMenu(Player bukkitPlayer) {
    HomiesPlayer homiesPlayer = new SpigotPlayer(bukkitPlayer); // Wrap native player
    MySimpleActionMenu menu = new MySimpleActionMenu(homiesPlayer);
    HomiesLib.getMenuManager().openMenu(homiesPlayer, menu);
}
```

#### Paginated Menu

For menus that display a large number of items across multiple pages.

```java
// 1. Define your custom menu (e.g., com.yourcompany.MyAwesomeMenu.java)
package com.yourcompany;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.menu.AbstractPaginatedHomiesMenu;
import lib.homies.framework.menu.HomiesMenuItem;
import lib.homies.framework.menu.SimpleHomiesMenuItem;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.utils.HomiesItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class MyAwesomeMenu extends AbstractPaginatedHomiesMenu {

    private static final int MENU_SIZE = 54;
    private static final int[] PAGINATION_SLOTS = IntStream.range(9, 45).toArray();
    private static final int PREVIOUS_PAGE_BUTTON_SLOT = 45;
    private static final int NEXT_PAGE_BUTTON_SLOT = 53;

    public MyAwesomeMenu(HomiesPlayer player) {
        // Simulate loading items asynchronously (e.g., from a database)
        loadPaginatedContentItemsAsync(player, loadedItems -> {
            List<HomiesMenuItem> dummyItems = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                int itemNumber = i + 1;
                HomiesItemStack itemStack = HomiesLib.getItemBuilder("DIAMOND_PICKAXE")
                        .name("&bAwesome Pick #" + itemNumber)
                        .lore("&7This pickaxe is super cool!")
                        .build();
                dummyItems.add(new SimpleHomiesMenuItem(itemStack, p -> {
                    HomiesLib.getMessageUtils().send(p, "&eYou picked up Awesome Pick #" + itemNumber + "!");
                }));
            }
            setPaginatedContentItems(dummyItems);
        });
    }

    @Override
    public String getTitle(HomiesPlayer player) {
        return isLoading ? "&eLoading Awesome Picks..." : "&8Awesome Picks (Page " + (currentPage + 1) + "/" + getMaxPage() + ")";
    }

    @Override
    public int getSize() { return MENU_SIZE; }

    // --- Animation & Feedback ---
    @Override
    public long getAnimationDelayTicks() { return 1; } // Pop-in animation
    @Override
    public String getAnimationSound() { return "BLOCK_NOTE_BLOCK_HAT"; } // Sound for pop-in
    @Override
    public long getAnimationOutDelayTicks() { return 1; } // Pop-out animation
    @Override
    protected HomiesMenuItem getLoadingItem() {
        return new SimpleHomiesMenuItem(HomiesLib.getItemBuilder("COMPASS").name("&eFetching items...").build());
    }
    // ----------------------------

    @Override
    protected int[] getPaginationSlots() { return PAGINATION_SLOTS; }
    @Override
    protected HomiesMenuItem getNextPageButton(HomiesPlayer player) {
        return new SimpleHomiesMenuItem(HomiesLib.getItemBuilder("SPECTRAL_ARROW").name("&aNext Page ->").build(),
                p -> { nextPage(); HomiesLib.getMenuManager().openMenu(p, this); });
    }
    @Override
    protected HomiesMenuItem getPreviousPageButton(HomiesPlayer player) {
        return new SimpleHomiesMenuItem(HomiesLib.getItemBuilder("SPECTRAL_ARROW").name("<- &aPrevious Page").build(),
                p -> { previousPage(); HomiesLib.getMenuManager().openMenu(p, this); });
    }
    @Override
    protected HomiesMenuItem getFillerItem() {
        return new SimpleHomiesMenuItem(HomiesLib.getItemBuilder("BLACK_STAINED_GLASS_PANE").name(" ").build());
    }
    @Override
    protected int getNextPageButtonSlot() { return NEXT_PAGE_BUTTON_SLOT; }
    @Override
    protected int getPreviousPageButtonSlot() { return PREVIOUS_PAGE_BUTTON_SLOT; }

    @Override
    public Map<Integer, HomiesMenuItem> getItems(HomiesPlayer player) {
        Map<Integer, HomiesMenuItem> items = super.getItems(player);
        // Add a static close button
        items.put(49, new SimpleHomiesMenuItem(HomiesLib.getItemBuilder("BARRIER").name("&cClose").build(),
                HomiesLib.getMenuManager()::closeMenu));
        return items;
    }
}
```

**Opening the Paginated Menu (e.g., from a command):**

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.spigot.player.SpigotPlayer; // For casting
import org.bukkit.entity.Player;

// ... inside a command or event handler
public void openMyMenu(Player bukkitPlayer) {
    HomiesPlayer homiesPlayer = new SpigotPlayer(bukkitPlayer); // Wrap native player
    MyAwesomeMenu menu = new MyAwesomeMenu(homiesPlayer);
    HomiesLib.getMenuManager().openMenu(homiesPlayer, menu);
}
```

### ⏱️ Scheduler System

Run tasks synchronously or asynchronously with flexible delays and repetitions.

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.scheduler.HomiesTask;

// Assuming 'player' is a HomiesPlayer instance
public void scheduleTasks(HomiesPlayer player) {
    // Run a task after 5 seconds (100 ticks)
    HomiesLib.getSchedulerService().runLater(() -> {
        HomiesLib.getMessageUtils().send(player, "&aThis message appeared after 5 seconds!");
    }, 100L);

    // Run a task asynchronously
    HomiesLib.getSchedulerService().runAsync(() -> {
        // Perform a heavy, non-blocking operation here
        System.out.println("Heavy task running asynchronously...");
        HomiesLib.getMessageUtils().send(player, "&bAsync task completed!");
    });

    // Run a task every 2 seconds (40 ticks)
    HomiesTask repeatingTask = HomiesLib.getSchedulerService().runRepeating(() -> {
        HomiesLib.getMessageUtils().send(player, "&eRepeating message!");
    }, 0L, 40L); // Start immediately, repeat every 2 seconds

    // Cancel the repeating task after 10 seconds
    HomiesLib.getSchedulerService().runLater(repeatingTask::cancel, 200L);

    // Use the task builder for more complex schedules
    HomiesLib.getSchedulerService().task()
        .run(() -> HomiesLib.getMessageUtils().send(player, "&dBuilder task finished!"))
        .async() // Run asynchronously
        .delay(60L) // After 3 seconds
        .group("my-plugin-tasks") // Assign to a group for easy cancellation
        .start();

    // Cancel all tasks in a group
    // HomiesLib.getSchedulerService().cancelGroup("my-plugin-tasks");
}
```

### 💾 Database System (Redis ORM Example)

Persist your custom data objects to Redis with simple annotations!

```java
// 1. Define your entity (e.g., com.yourcompany.PlayerStats.java)
package com.yourcompany;

import lib.homies.framework.database.annotations.DbEntity;
import lib.homies.framework.database.annotations.DbField;
import lombok.Data; // Using Lombok for boilerplate

@Data // Lombok annotation for getters, setters, equals, hashCode
@DbEntity(tableName = "player_stats") // Maps to Redis hash key prefix
public class PlayerStats {

    @DbField(columnName = "uuid", id = true) // Marks as primary ID
    private String uuid;

    @DbField(columnName = "kills")
    private int kills;

    @DbField(columnName = "deaths")
    private int deaths;

    @DbField(columnName = "last_login")
    private long lastLogin;
}
```

**Saving and Loading Entities:**

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import com.yourcompany.PlayerStats;

import java.util.Optional;

// Assuming 'player' is a HomiesPlayer instance
public void managePlayerStats(HomiesPlayer player) {
    String playerUuid = player.getUniqueId().toString(); // Assuming HomiesPlayer has getUniqueId()

    // Load player stats
    Optional<PlayerStats> optionalStats = HomiesLib.getDatabaseService().findById(PlayerStats.class, playerUuid);

    PlayerStats stats;
    if (optionalStats.isPresent()) {
        stats = optionalStats.get();
        stats.setKills(stats.getKills() + 1); // Increment kills
        HomiesLib.getMessageUtils().send(player, "&aYour kills: " + stats.getKills());
    } else {
        // New player, create default stats
        stats = new PlayerStats();
        stats.setUuid(playerUuid);
        stats.setKills(1);
        stats.setDeaths(0);
        stats.setLastLogin(System.currentTimeMillis());
        HomiesLib.getMessageUtils().send(player, "&aWelcome, new player! Your first kill recorded.");
    }

    // Save updated stats
    HomiesLib.getDatabaseService().saveEntity(stats);
}
```

### 🎮 Minigame System

Build your minigames on a solid, abstracted foundation.

```java
// 1. Define your custom minigame (e.g., com.yourcompany.MyMinigame.java)
package com.yourcompany;

import lib.homies.framework.HomiesLib;
import lib.homies.framework.minigame.AbstractMiniGame;
import lib.homies.framework.minigame.MiniGameState;
import lib.homies.framework.player.HomiesPlayer;

public class MyMinigame extends AbstractMiniGame {

    public MyMinigame(String name) {
        super(name);
    }

    @Override
    protected void onStart() {
        HomiesLib.getMessageUtils().broadcast("&aMinigame '" + getName() + "' has started!");
        getPlayers().forEach(p -> HomiesLib.getMessageUtils().send(p, "&eGood luck!"));
    }

    @Override
    protected void onStop() {
        HomiesLib.getMessageUtils().broadcast("&cMinigame '" + getName() + "' has ended!");
        getPlayers().forEach(p -> HomiesLib.getMessageUtils().send(p, "&eThanks for playing!"));
    }

    @Override
    protected void onPlayerJoin(HomiesPlayer player) {
        HomiesLib.getMessageUtils().broadcast("&b" + player.getName() + " joined " + getName() + "!");
        if (getState() == MiniGameState.RUNNING) {
            HomiesLib.getMessageUtils().send(player, "&eYou joined a game in progress!");
        }
    }

    @Override
    protected void onPlayerLeave(HomiesPlayer player) {
        HomiesLib.getMessageUtils().broadcast("&b" + player.getName() + " left " + getName() + "!");
    }
}
```

**Managing Minigames:**

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.minigame.MiniGame;
import lib.homies.framework.player.HomiesPlayer;
import com.yourcompany.MyMinigame;

import java.util.Optional;

// Assuming 'player' is a HomiesPlayer instance
public void manageMinigames(HomiesPlayer player) {
    // Register a new minigame
    MiniGame myGame = new MyMinigame("Deathmatch");
    HomiesLib.getMiniGameManager().registerGame(myGame);
    HomiesLib.getMessageUtils().send(player, "&aRegistered new minigame: Deathmatch!");

    // Get a minigame and interact with it
    Optional<MiniGame> game = HomiesLib.getMiniGameManager().getGame("Deathmatch");
    game.ifPresent(g -> {
        if (g.getState() == lib.homies.framework.minigame.MiniGameState.STOPPED) {
            g.start();
            g.addPlayer(player);
            HomiesLib.getMessageUtils().send(player, "&eYou joined Deathmatch!");
        } else {
            HomiesLib.getMessageUtils().send(player, "&cDeathmatch is already running!");
        }
    });
}
```

### 👤 Player Data Service

Store and retrieve platform-agnostic player data with ease!

```java
// 1. Access the PlayerDataService
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.playerdata.PlayerData;

// Assuming 'player' is a HomiesPlayer instance
public void updatePlayerLogin(HomiesPlayer player) {
    HomiesLib.getPlayerDataService().getPlayerData(player).ifPresentOrElse(
        playerData -> {
            // Player data exists, update last login
            playerData.setLastLogin(System.currentTimeMillis());
            HomiesLib.getPlayerDataService().savePlayerData(playerData);
            HomiesLib.getMessageUtils().send(player, "&aWelcome back, " + player.getName() + "!");
        },
        () -> {
            // New player, create new data
            PlayerData newPlayerData = new PlayerData();
            newPlayerData.setUuid(player.getUniqueId().toString());
            newPlayerData.setLastLogin(System.currentTimeMillis());
            HomiesLib.getPlayerDataService().savePlayerData(newPlayerData);
            HomiesLib.getMessageUtils().send(player, "&aWelcome to the server, " + player.getName() + "!");
        }
    );
}
```

### ⏳ Cooldown Service

Manage time-based cooldowns for players with a simple, platform-independent API!

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;

import java.util.concurrent.TimeUnit;

// Assuming 'player' is a HomiesPlayer instance
public void handleAbilityCooldown(HomiesPlayer player) {
    String abilityKey = "super_jump_ability";

    if (HomiesLib.getCooldownService().isOnCooldown(player, abilityKey)) {
        HomiesLib.getCooldownService().getRemainingCooldown(player, abilityKey, TimeUnit.SECONDS).ifPresent(remaining -> {
            HomiesLib.getMessageUtils().send(player, "&cSuper Jump is on cooldown for " + remaining + " seconds!");
        });
    } else {
        // Ability is not on cooldown, use it!
        HomiesLib.getMessageUtils().send(player, "&aYou used Super Jump!");
        // Set a 10-second cooldown
        HomiesLib.getCooldownService().setCooldown(player, abilityKey, 10, TimeUnit.SECONDS);
    }
}
```

### 🥳 Party System

Create and manage player parties with a robust, platform-independent API!

```java
import lib.homies.framework.HomiesLib;
import lib.homies.framework.player.HomiesPlayer;
import lib.homies.framework.party.Party;

import java.util.Optional;

// Assuming 'player' and 'targetPlayer' are HomiesPlayer instances
public void manageParty(HomiesPlayer player, HomiesPlayer targetPlayer) {
    Optional<Party> playerParty = HomiesLib.getPartyService().getPartyByPlayer(player);

    if (playerParty.isEmpty()) {
        // Player is not in a party, let them create one
        Party newParty = HomiesLib.getPartyService().createParty(player);
        if (newParty != null) {
            HomiesLib.getMessageUtils().send(player, "&aYou created a party!");
            HomiesLib.getPartyService().invitePlayer(player, targetPlayer);
        }
    } else {
        Party party = playerParty.get();
        if (party.isLeader(player)) {
            // Player is a leader, invite someone
            HomiesLib.getPartyService().invitePlayer(player, targetPlayer);
        } else {
            // Player is a member, try to leave
            HomiesLib.getPartyService().leaveParty(player);
        }
    }

    // Example of accepting an invitation
    // HomiesLib.getPartyService().acceptInvitation(targetPlayer, player);
}
```

---

## 🌳 Project Structure: Clean & Modular

```
HomiesLib/
├── pom.xml
├── homieslib-api/          (Interfaces, DTOs, Annotations - The Universal Blueprint)
├── homieslib-spigot/       (Spigot/Paper Implementation - The Engine's Guts)
└── homieslib-processor/    (Annotation Processor - Code Generation Wizard)
```

---

## ⚙️ Setup & Build (For Framework Developers)

From the repository root:

*   **Build all modules:** `mvn clean install`
*   **Build without tests:** `mvn -DskipTests clean install`

Artifacts will be in each module’s `target` directory.

---

## 🚀 Run (As a Paper Plugin)

The `HomiesLibSpigot` module is your entry point.

1.  **Build the project** (see above).
2.  The final plugin JAR will be produced by `homieslib-spigot/target/*.jar`.
3.  Copy this JAR into your Paper server’s `plugins` directory.
4.  Start the Paper server and watch the console for "HomiesLibSpigot enabled!"

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