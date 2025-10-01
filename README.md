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
      - HomiesLibSpigot # This tells the server to load your framework first!
    ```

4. **Create Your Commands!**
   Simply create your command classes with annotations (see example below). **No manual registration is needed!** Just
   build your plugin with Maven, and HomiesLib will automatically find and register them on server startup.

---

## 🚀 Usage Example: The Annotation-Driven Command System

Define each command in its own class. The framework handles registration, sub-command dispatching, and tab completion
automatically.

### Full Example: A Comprehensive `/gamemode` Command

This example demonstrates subcommands, permissions, parameter injection, and explicit tab-completion.

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
        Player bukkitPlayer = sender.getAs(Player.class);
        bukkitPlayer.setGameMode(GameMode.CREATIVE);
        HomiesLib.getMessageUtils().send(sender, "&aYour gamemode has been set to &eCreative&a.");
    }

    // 3. Subcommand with a player argument: `/gamemode creative <player>`
    @SubCommand(value = "creative <player>", description = "Set another player's gamemode to Creative.")
    @Permission("myplugin.gamemode.creative.other")
    public void onCreativeOther(HomiesPlayer sender, HomiesPlayer target) {
        Player targetPlayer = target.getAs(Player.class);
        targetPlayer.setGameMode(GameMode.CREATIVE);
        HomiesLib.getMessageUtils().send(sender, "&aSet &b" + target.getName() + "'s &agamemode to &eCreative&a.");
        HomiesLib.getMessageUtils().send(target, "&aYour gamemode was set to &eCreative &aby &b" + sender.getName() + "&a.");
    }

    // 4. Another subcommand: `/gamemode survival`
    @SubCommand(value = "survival", aliases = {"0", _s"}, description = "Set your gamemode to Survival.")
            @Permission("myplugin.gamemode.survival")
    public void onSurvival(HomiesPlayer sender) {
        Player bukkitPlayer = sender.getAs(Player.class);
        bukkitPlayer.setGameMode(GameMode.SURVIVAL);
        HomiesLib.getMessageUtils().send(sender, "&aYour gamemode has been set to &eSurvival&a.");
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

### How It Works

- **`@Command` (Class):** Defines the main command `/gamemode` and its alias `/gm`.
- **`@Command` (Method):** The `execute` method is the default handler. It runs for `/gamemode` if no other subcommand
  matches.
- **`@SubCommand`:** Each method handles a specific subcommand path. The framework automatically parses arguments and
  calls the correct method.
    - `value = "creative <player>"`: The `<player>` placeholder tells the framework to expect a player name and inject
      it as a `HomiesPlayer` object.
- **`@Permission`:** Automatically checks if the `CommandSender` has the required permission node.
- **`@TabComplete`:**
    - `value = ""`: Targets the first argument, suggesting `creative` and `survival`.
    - `value = "creative"`: Targets the argument *after* `creative`, suggesting online player names.
- **Automatic Registration:** You do **not** need to write any code in your `onEnable()` to register this command. Just
  build your plugin, and HomiesLib handles the rest!

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

## 📜 License

This project is licensed under the MIT License. See the `LICENSE` file in the repository root.

---

## 🤝 Contributing

Contributions, issues, and feature requests are always welcome! Let's make HomiesLib the best framework out there!

*   Open an issue describing the change.
*   Keep PRs focused and include minimal reproducible examples.
*   Follow the module structure and Java 17 compatibility.

---