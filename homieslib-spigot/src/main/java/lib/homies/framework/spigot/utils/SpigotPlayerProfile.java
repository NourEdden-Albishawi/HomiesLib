package lib.homies.framework.spigot.utils;

import org.bukkit.profile.PlayerProfile;
import lib.homies.framework.utils.HomiesPlayerProfile;

/**
 * Spigot-specific implementation of the {@link HomiesPlayerProfile} interface.
 * This class wraps a Bukkit {@link PlayerProfile} object, providing a platform-specific
 * way to manage player profiles while adhering to the platform-agnostic API.
 */
public class SpigotPlayerProfile implements HomiesPlayerProfile {
    private final PlayerProfile playerProfile;

    /**
     * Constructs a new SpigotPlayerProfile wrapping a given Bukkit PlayerProfile.
     * @param playerProfile The Bukkit {@link PlayerProfile} to wrap.
     */
    public SpigotPlayerProfile(PlayerProfile playerProfile) {
        this.playerProfile = playerProfile;
    }

    /**
     * Gets the underlying Bukkit {@link PlayerProfile} object.
     * This method allows direct access to Bukkit's player profile functionality when needed
     * within the Spigot implementation module.
     * @return The wrapped Bukkit PlayerProfile.
     */
    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }
}
